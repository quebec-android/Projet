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
	public static String HOSTCODE = "localhost:8080";
	public static String HOSTFILE = "localhost:8080";
	public static String HOSTASSOCIATOR = "localhost:8080";
	
	
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
