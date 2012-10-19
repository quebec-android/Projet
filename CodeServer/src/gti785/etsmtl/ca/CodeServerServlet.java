package gti785.etsmtl.ca;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Reader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.output.*;

/**
 * Servlet implementation class CodeServerServlet
 */
public class CodeServerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HttpURLConnection connection;
    
    private static final List<Host> hosts = new ArrayList<Host>();

	private static final XStream xstream = new XStream(new JettisonMappedXmlDriver());
	
	static final Map<DecodeHintType,Object> HINTS;
	static final Map<DecodeHintType,Object> HINTS_PURE;
	private static final long MAX_IMAGE_SIZE = 2000000L;
	
	static {
	    HINTS = new EnumMap<DecodeHintType,Object>(DecodeHintType.class);
	    HINTS.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
	    HINTS.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.allOf(BarcodeFormat.class));
	    HINTS_PURE = new EnumMap<DecodeHintType,Object>(HINTS);
	    HINTS_PURE.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
	  }
	
	// Configuration de XStream
	static {
		xstream.setMode(XStream.NO_REFERENCES);
		xstream.alias("Host", Host.class);
		xstream.alias("ActiveHosts", List.class);
		xstream.alias("File", File.class);
		xstream.alias("Files", List.class);
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		String strImg = request.getParameter("image");
		
		if (strImg != null && !strImg.isEmpty()) {
			
			String code = strImg;
			String IP = null;
			
			/**TODO
			 * utilise api pour avoir le code
			 */
			
			if( (IP = this.checkHostList(code)) != null ){
				/** TODO
				 * Envoyer réponse au portable
				 */
				
				// out.println("<data><ip>"+"192.168.0.1   ......    image="+strImg+"</ip></data>");
			}
			else{
				String url = "http://localhost:8080/Associator/AssociatorServlet";
				if( this.sendGetRequest(url, "") ){
					//read the result from the server
			        BufferedReader rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			        String line = rd.readLine();
			        List<Host> activeHost = (List<Host>) xstream.fromXML(line);
			        this.addNewHosts(activeHost);
				}
				if( (IP = this.checkHostList(code)) != null ){
					/**TODO
					 * send request to IP fileServer to get files
					 */
					
					// out.println("<data><ip>"+"192.168.0.1   ......    image="+strImg+"</ip></data>");
				}
				else{
					response.getWriter().write("IP not found");
					//out.println("<data><ip>-1</ip></data>");
				}
			}
			
			if( IP != null ){
				String answer = "<?xml version=\"1.0\"?>";
		        answer += "<data><ip>"+IP+"</ip></data>";
		        
		        response.getWriter().write(answer);
			}
		}
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		
		DiskFileItemFactory  fileItemFactory = new DiskFileItemFactory ();
		ServletFileUpload upload = new ServletFileUpload(fileItemFactory);
	    upload.setFileSizeMax(MAX_IMAGE_SIZE);
	    
	    
	    try{
	    	List<FileItem> fi = (List<FileItem>) upload.parseRequest(request);
		    for (FileItem item : fi) {
				InputStream is = item.getInputStream();
				this.processImage(is, request, response);
		    }
	    }
	    catch(Exception e){
	    	
	    }
		
	}
	
	public boolean sendGetRequest(String url, String parametre){
		try{
			URL u = new URL(url+"?"+parametre);
			connection = (HttpURLConnection) u.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
	
	public String checkHostList(String code){
		for(Host host: hosts){
			if(host.getCode().equals(code)){
				return host.getIp();
			}
		}
		return null;
	}
	
	public void addNewHosts( List<Host> activeHosts){
		for(Host host: activeHosts){
			if( !hosts.contains(host) ){
				hosts.add(host);
			}
		}
	}
	
	public void processImage (InputStream is, ServletRequest request, HttpServletResponse response)	throws ServletException, IOException{
		BufferedImage image;
		
		image = ImageIO.read(is);
	
	 	Reader reader = new MultiFormatReader();
	    LuminanceSource source = new BufferedImageLuminanceSource(image);
	    BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));
	    String code = null;
	    
	    try{
	    	Result theResult = reader.decode(bitmap, HINTS);
	    	code = theResult.toString();
	    }
	    catch(Exception e){
	    	System.out.println("Error");
	    }
		
		if ( code != null ) {
			
			String IP = null;
			
			/**TODO
			 * utilise api pour avoir le code
			 */
			
			if( (IP = this.checkHostList(code)) != null ){
				/** TODO
				 * Envoyer réponse au portable
				 */
				
				// out.println("<data><ip>"+"192.168.0.1   ......    image="+strImg+"</ip></data>");
			}
			else{
				String url = "http://localhost:8080/Associator/AssociatorServlet";
				if( this.sendGetRequest(url, "") ){
					//read the result from the server
			        BufferedReader rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			        String line = rd.readLine();
			        List<Host> activeHost = (List<Host>) xstream.fromXML(line);
			        this.addNewHosts(activeHost);
				}
				if( (IP = this.checkHostList(code)) != null ){
					/**TODO
					 * send request to IP fileServer to get files
					 */
					
					// out.println("<data><ip>"+"192.168.0.1   ......    image="+strImg+"</ip></data>");
				}
				else{
					response.getWriter().write("IP not found");
					//out.println("<data><ip>-1</ip></data>");
				}
			}
			
			if( IP != null ){
				String answer = "<?xml version=\"1.0\"?>";
		        answer += "<data><ip>"+IP+"</ip></data>";
		        
		        response.getWriter().write(answer);
			}
			else{
				response.getWriter().write("failed"+code);
			}
		}
		
	}
	
}
