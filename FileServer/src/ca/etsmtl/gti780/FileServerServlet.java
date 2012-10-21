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
import com.thoughtworks.xstream.io.xml.DomDriver;
import common.Const;

/**
 * Servlet implementation class FileServerServlet
 */
public class FileServerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private boolean associated = false;
    private HttpURLConnection connection;
    
    private WatchDog _watchDog;
    private FileFolderListener _folderListener;
    private File folder = new File("testFolder4");
    
    private static final XStream xstream = new XStream(new DomDriver());
    
    /**
     * Configure XStream
     */
    static {
		xstream.setMode(XStream.NO_REFERENCES);
		xstream.alias("File", File.class);
	}
    
    /**
     * Overriding init method
     */
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
	 * Overriding doGet method
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		
		/**GetFiles
		 * Retourne les fichiers du dossier surveille
		 */
		if( action != null && action.equals("getFiles")){
			List<File> files = _folderListener.getFiles();
			response.getWriter().write(xstream.toXML(files));
		}
		
		/**Copy file
		 * Envoi une requete GET au serveur source pour obtenir le fichier a copier
		 */
		if( action != null && action.equals("copyFile") ){
			String IP = request.getParameter("IPsource");
			String file = request.getParameter("file");
			System.out.println("copyFile "+IP+" "+file);
			
			this.sendGetRequest(Const.URLFILEPROTOCOLE,"action=getFile&file="+file);
			if (connection.getResponseCode() == 200) {
				BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line = (String) xstream.fromXML(rd);
				_folderListener.copyfile(file,line);
				response.getWriter().write("File "+file+" created with success!");
			} else { //erreur lors de la récupération du contenu du fichier
				response.sendError(422, connection.getResponseMessage());
			}
		}
		
		/**Get file
		 * Renvoi le xml du fichier demande
		 */
		if( action != null && action.equals("getFile")){
			String getFileName = request.getParameter("file");
			File getFile = _folderListener.getFile(getFileName);
			
			if( getFile != null ){
				String data = copyFile(getFileName);
				if (data != null){
					response.getWriter().write(xstream.toXML(data));
				} else {
					response.sendError(422, "File doesn't exist on the server...");
				}
			}
		}
	}

	/**
	 * Synchronise an IP
	 */
	private void synchro(){
		try{
			this.sendGetRequest(Const.URLASSOCIATEDPROTOCOLE,"host="+Const.URLFILE+"&code=ETS035796");
			BufferedReader rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        rd.readLine();
			associated = true;
		}
		catch(Exception e){
			
		}
	}
	
	/**
	 * Uses to send Get Request
	 * @param url
	 * @param parametre
	 * @return
	 */
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
	public String copyFile (String src) {
	    try {
		      src = _folderListener.get_folder()+"/"+src;
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
		return null;
	}
}
