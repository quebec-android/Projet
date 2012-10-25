package common;

/**
 * Used by all projects for const values
 * 
 *
 */
public class Const {

	/**
	 * HOST
	 */
	public static String HOSTCODE = "192.168.1.205:8080";
	public static String HOSTFILE = "192.168.1.205:8080";
	public static String HOSTASSOCIATOR = "192.168.1.205:8080";
	
	
	/**
	 * PROTOCOLE
	 */
	public static String PROTOCOLE = "http://";
	
	
	/**
	 * URL
	 */
	public static String URLASSOCIATEDPROTOCOLE = PROTOCOLE+HOSTASSOCIATOR+"/Associator/AssociatorServlet";
	public static String URLFILEPROTOCOLE = PROTOCOLE+HOSTFILE+"/FileServer/FileServerServlet";
	public static String URLFILE= HOSTFILE+"/FileServer/FileServerServlet";
	
}
