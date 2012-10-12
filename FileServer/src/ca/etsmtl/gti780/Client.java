package ca.etsmtl.gti780;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Client {
	private static HttpURLConnection connection;


	public static void main(String[] args) {
		callSynch();
	}
	
	public static void callSynch(){
		try {
			URL u = new URL("http://localhost:8080/FileServer/FileServerServlet?action=synchronise");
			connection = (HttpURLConnection) u.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			
			BufferedReader rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String line = rd.readLine();
	        System.out.println(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void callCopy(){
		try {
			URL u = new URL("http://localhost:8080/FileServer/FileServerServlet?action=synchronise");
			connection = (HttpURLConnection) u.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			
			BufferedReader rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String line = rd.readLine();
	        System.out.println(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
