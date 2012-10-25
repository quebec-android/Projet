package gti785.etsmtl.ca;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

/**
 * Servlet implementation class CodeServerServlet
 * 
 */
public class CodeServerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HttpURLConnection connection;
	private String url = "http://192.168.1.205:8080/Associator/AssociatorServlet";
    
    private static final List<Host> hosts = new ArrayList<Host>();

	private static final XStream xstream = new XStream(new JettisonMappedXmlDriver());
	
	static final Map<DecodeHintType,Object> HINTS;
	static final Map<DecodeHintType,Object> HINTS_PURE;
	private static final long MAX_IMAGE_SIZE = 2000000L;
	
	/**
	 * Configure the decoder
	 */
	static {
	    HINTS = new EnumMap<DecodeHintType,Object>(DecodeHintType.class);
	    HINTS.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
	    HINTS.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.allOf(BarcodeFormat.class));
	    HINTS_PURE = new EnumMap<DecodeHintType,Object>(HINTS);
	    HINTS_PURE.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
	  }
	
	/**
	 * Configure XStream
	 */
	static {
		xstream.setMode(XStream.NO_REFERENCES);
		xstream.alias("Host", Host.class);
		xstream.alias("ActiveHosts", List.class);
		xstream.alias("File", File.class);
		xstream.alias("Files", List.class);
	}
	
	/**
	 * Overriding doGet method
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().write("Code server online");
	}
	
	/**
	 * Overriding doPost method
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DiskFileItemFactory  fileItemFactory = new DiskFileItemFactory ();
		ServletFileUpload upload = new ServletFileUpload(fileItemFactory);
	    upload.setFileSizeMax(MAX_IMAGE_SIZE);
	    
	    try{
	    	List<FileItem> fi = (List<FileItem>) upload.parseRequest(request);
		    for (FileItem item : fi) {
				InputStream is = item.getInputStream();
				String code = this.processImage(is, request, response);
				//String code = "ETS035796";
				is.close();
				if ( code != null ) {
					String IP  = this.checkHostList(code);
					if( IP != null ){ // ASSOCIATION OK
						response.getWriter().write("<data><ip>"+IP+"</ip></data>");
					}
					else{ //IP pas connu, on l'ajoute dans la table des hotes
						if( this.sendGetRequest(url, null) ){
							//read the result from the server
					        BufferedReader rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					        String line = rd.readLine();
					        rd.close();
					        List<Host> activeHost = (List<Host>) xstream.fromXML(line);
					        this.addNewHosts(activeHost);
						}
						
						//on retente après l'ajout
						IP = this.checkHostList(code);
						if( IP != null ){
							response.getWriter().write("<data><ip>"+IP+"</ip></data>");
						} else{
							response.sendError(510, "This barre code is not associated to any IP.");
						}
					}
				} else {//probleme code == null
					response.sendError(511, "Server error while associating the barre code.");
				}
		    }
	    } catch (NotFoundException e) {
	    	response.sendError(512, "No barre code was found on the picture.");
	    } catch(Exception e){
	    	response.sendError(511, "Server error while associating the barre code.");
	    }
	}
	
	/**
	 * Decode the image of the barre code to return the associated code
	 * @param is
	 * @param request
	 * @param response
	 * @return associated code
	 * @throws ServletException
	 * @throws IOException
	 * @throws FormatException 
	 * @throws ChecksumException 
	 * @throws NotFoundException 
	 */
	public String processImage (InputStream is, ServletRequest request, HttpServletResponse response)	throws ServletException, IOException, NotFoundException, ChecksumException, FormatException{
		BufferedImage image;
		
//		OutputStream os = new FileOutputStream("image.jpg");
//
//		byte[] b = new byte[2048];
//		int length;
//
//		while ((length = is.read(b)) != -1) {
//			os.write(b, 0, length);
//		}
//
//		is.close();
//		os.close();
		
		
		image = ImageIO.read(is);
	 	Reader reader = new MultiFormatReader();
	    LuminanceSource source = new BufferedImageLuminanceSource(image);
	    BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));
	    String code = null;
	    
    	Result theResult = reader.decode(bitmap, HINTS);
    	code = theResult.toString();
    	System.out.println(code);
		is.close();
		
		return code;
	}
	
	/**
	 * Send a Get Request
	 * @param url
	 * @param parametre
	 * @return
	 */
	public boolean sendGetRequest(String url, String parametre){
		try{
			URL u;
			if (parametre!=null) {
				u = new URL(url+"?"+parametre);
			} else {
				u = new URL(url);
			}
			connection = (HttpURLConnection) u.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
	
	/**
	 * Retrieve a code in the host list
	 * @param code
	 * @return
	 */
	public String checkHostList(String code){
		for(Host host: hosts){
			if(host.getCode().equals(code)){
				return host.getIp();
			}
		}
		return null;
	}
	
	/**
	 * Add new hosts in the list
	 * @param activeHosts
	 */
	public void addNewHosts( List<Host> activeHosts){
		for(Host host: activeHosts){
			if( !hosts.contains(host) ){
				hosts.add(host);
			}
		}
	}
	
}
