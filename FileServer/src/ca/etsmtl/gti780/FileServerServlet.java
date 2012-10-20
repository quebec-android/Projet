package ca.etsmtl.gti780;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;

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
    
    private static final XStream xstream = new XStream(new DomDriver());
    
 // Configuration de XStream
    static {
		xstream.setMode(XStream.NO_REFERENCES);
		xstream.alias("File", File.class);
	}
    
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        if (!folder.exists()) {
        	try{
	        	folder.mkdir();
	        	File addedFile = new File("testFolder4/testFile");
	        	System.out.println(addedFile.getAbsolutePath());
	    		addedFile.createNewFile();
        	}
        	catch(Exception e){
        		System.out.println("Error while creating test files");
        	}
        }
        
        _folderListener = new FileFolderListener(folder);
        _watchDog = new WatchDog(folder, _folderListener);
        _watchDog.watch();
        
        if(!associated){
			synchro();
		}
    }
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		
		/**Association
		 * Association avec l'associator et remplissage du tableau de hosts actif.
		 */
		/*if(action != null && action.equals("synchronise") && !associated){
			synchro();
		}*/
		
		/**GetFiles
		 * Retourne les fichiers du dossier surveillŽ
		 */
		if( action != null && action.equals("getFiles")){
			List<File> files = _folderListener.getFiles();
			response.getWriter().write(xstream.toXML(files));
		}
		
		/**Copy file
		 * Envoi une requ�te GET au serveur source pour obtenir le fichier ˆ copier
		 */
		if( action != null && action.equals("copyFile") ){
			String IP = request.getParameter("IPsource");
			String file = request.getParameter("file");
			System.out.println("copyFile "+IP+" "+file);
			
			this.sendGetRequest("http://localhost:8080/FileServer/FileServerServlet","action=getFile&file="+file);
			BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = (String) xstream.fromXML(rd);
			System.out.println(line);
			
			_folderListener.copyfile(file,line);
		}
		
		/**Get file
		 * Renvoi le xml du fichier demandŽ
		 */
		if( action != null && action.equals("getFile")){
			String getFileName = request.getParameter("file");
			File getFile = _folderListener.getFile(getFileName);
			
			if( getFile != null ){
				//response.getWriter().write(xstream.toXML(getFile));
				String data = copyFile(getFileName);
				response.getWriter().write(xstream.toXML(data));
			}
		}
	}

	private void synchro(){
		try{
			this.sendGetRequest(urlAssociator,"host="+urlSelf+"&code=ETS035796");
			//response.getWriter().write("Association");
			BufferedReader rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String line = rd.readLine();
			associated = true;
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
	
	/**
	 * Fonction qui copie le contenu du fichier en String
	 * On rajoute au nom du fichier le nom du dossier surveille par le WatchDog
	 * @param src
	 * @return
	 * @throws IOException
	 */
	public String copyFile (String src) throws IOException {
	    try {
		      src = folder+"/"+src;
	    	  String content="",ligne ;
		      BufferedReader fichier = new BufferedReader(new FileReader(src));
		      
		      while ((ligne = fichier.readLine()) != null) {
		          content+=ligne;
		          content+="\n";
		      }
	
		      fichier.close();
		      return content;
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }  
		return "ERROR";
	}
}
