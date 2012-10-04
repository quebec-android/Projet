package gti785.etsmtl.ca;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import gti785.etsmtl.ca.Host;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

/**
 * Servlet implementation class CodeServerServlet
 */
public class CodeServerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HttpURLConnection connection;
    
    private static final List<Host> hosts = new ArrayList<Host>();

	private static final XStream xstream = new XStream(
			new JettisonMappedXmlDriver());

	// Configuration de XStream
	static {
		xstream.setMode(XStream.NO_REFERENCES);
		xstream.alias("Host", Host.class);
		xstream.alias("ActiveHosts", List.class);
		xstream.alias("File", File.class);
		xstream.alias("Files", List.class);
	}
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CodeServerServlet() {
        super();
        // TODO Auto-generated constructor stub
        //
  
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		String strImg = request.getParameter("image");
		
		if (strImg != null && !strImg.isEmpty()) {
			
			//response.setContentType("text/xml");
		    //PrintWriter out = response.getWriter();
		    //out.println("<?xml version=\"1.0\"?>");
			
			//utilise helloMidlet je pense pour la remettre bien
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
				if( this.sendGetRequest("http://"+IP, "action=getFile")){
					BufferedReader rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			        String line = rd.readLine();
			        List<File> files = (List<File>) xstream.fromXML(line);
			        String answer = "<?xml version=\"1.0\"?>";
			        answer += "<data>";
			        for(File file: files){
			        	answer += "<file>"+file.toString()+"</file>";
			        }
			        answer += "</data>";
			        
			        response.getWriter().write(answer);
				}
				
				//response.getWriter().write("<data><ip>"+IP+"</ip></data>");
			}
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
	
}
