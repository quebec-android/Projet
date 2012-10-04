package ca.etsmtl.gti780;

import ca.etsmtl.gti780.Host;
import ca.etsmtl.gti780.WatchDog;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

/**
 * Servlet implementation class FileServerServlet
 */
public class FileServerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String urlAssociator = "http://localhost:8080/Associator/AssociatorServlet";
	private String urlSelf = "localhost:8080/FileServer/FileServerServlet";
    private boolean associated = false;
    private HttpURLConnection connection;
    
    private WatchDog _watchDog;
    private FileFolderListener _folderListener;
    private File folder = new File("testFolder4");
    
    private static final XStream xstream = new XStream(
			new JettisonMappedXmlDriver());
    
 // Configuration de XStream
    static {
		xstream.setMode(XStream.NO_REFERENCES);
		xstream.alias("File", Host.class);
		xstream.alias("ActiveHosts", List.class);
	}
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileServerServlet() {
        super();
        // TODO Auto-generated constructor stub
        /**Instancié le watchdog et folderlistener
         * 
         */
        if (!folder.exists()) {
        	try{
	        	folder.mkdir();
	        	File addedFile = new File("testFolder4/testFile");
	    		addedFile.createNewFile();
        	}
        	catch(Exception e){
        		System.out.println("Error while creating test files");
        	}
        }
        
        _folderListener = new FileFolderListener(folder);
        _watchDog = new WatchDog(folder, _folderListener);
        _watchDog.watch();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String action = request.getParameter("action");
		
		/**Association
		 * Association avec l'associator et remplissage du tableau de hosts actif.
		 */
		if(action != null && action.equals("synchronise") && !associated){
			this.sendGetRequest(urlAssociator,"host="+urlSelf+"&code=123");
			response.getWriter().write("Association");
			BufferedReader rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String line = rd.readLine();
			associated = true;
		}
		
		/**GetFiles
		 * Retourne les fichiers du dossier surveillé
		 */
		if( action != null && action.equals("getFile")){
			List<File> files = _folderListener.getFiles();
				response.getWriter().write(xstream.toXML(files));
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
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
}
