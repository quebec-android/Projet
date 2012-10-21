package mypackage;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.microedition.io.HttpConnection;

import net.rim.device.api.io.transport.ConnectionDescriptor;
import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.xml.parsers.DocumentBuilder;
import net.rim.device.api.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Grandement inspiré de :
 * 		http://docs.blackberry.com/en/developers/deliverables/11938/CS_create_first_available_HTTP_connection_857706_11.jsp
 * 		http://minimalbugs.com/questions/how-to-get-http-get-in-blackberry
 * 
 * Class used to send Get Request
 * 
 */
class ConnectionThread extends Thread
{
	HelloBlackBerryScreen screen;
	byte[] img = null;
	int choix = 0; //1 --> getFiles && 2 --> copyFile && 3 --> send image
	String label = null;
	
	/**
	 * Used to send request to get the list of the available files to copy
	 * @param screen
	 * @param choix
	 */
	public ConnectionThread(HelloBlackBerryScreen screen, int choix) {
		this.screen = screen;
		this.choix = choix;
	}
	
	/**
	 * Used to send request to copy a file
	 * @param screen
	 * @param label
	 */
	public ConnectionThread(HelloBlackBerryScreen screen, String label) {
		this.screen = screen;
		this.label = label;
		choix = 2;
	}
	
	/**
	 * Used to send the request to retrieve the IP associated to a picture
	 * @param screen
	 * @param img
	 */
	public ConnectionThread(HelloBlackBerryScreen screen, byte[] img) {
		this.screen = screen;
		this.img = img; 
		choix = 3;
	}

	/**
	 * 
	 */
	public void run()
	{
		String url = "";
		String bypass= "localhost";
		String port = "8080";
		String complet = bypass+":"+port;
		String completDest = complet;
		//String complet = screen.getIpSource();
		//String completDest = screen.getIpDest();
		
		if (choix == 1) {
			url = "http://"+complet+"/FileServer/FileServerServlet?action=getFiles";
		} else if (choix == 2) {
			url = "http://"+completDest+"/FileServer/FileServerServlet?action=copyFile&IPsource="+complet+"&file="+label;
		} else {
			url = "http://"+complet+"/CodeServer/CodeServerServlet?image="+new String(img);
		}
		ConnectionFactory connFact = new ConnectionFactory();
		ConnectionDescriptor connDesc;
		connDesc = connFact.getConnection(url);
		if (connDesc != null)
		{
			final HttpConnection httpConn;
			httpConn = (HttpConnection)connDesc.getConnection();
			try
			{
				final int iResponseCode = httpConn.getResponseCode();
				UiApplication.getUiApplication().invokeLater(new Runnable()
				{
					public void run()
					{
						String response = "";
						try {
							if(iResponseCode==200){
								if (choix==1) {
									Document doc;
									DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
									DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
									docBuilder.isValidating();
									doc = docBuilder.parse(httpConn.openInputStream());
									doc.getDocumentElement().normalize();
									
									NodeList list = doc.getElementsByTagName("File");
									String choices[] = new String[list.getLength()+1];
									choices[0] = "Choix du fichier";
									for (int i=0; i<list.getLength();i++){
										Element element1 = (Element) list.item(i);
										NodeList fstNm = element1.getChildNodes();
										choices[i+1] = (fstNm.item(0)).getNodeValue().substring((fstNm.item(0)).getNodeValue().indexOf("\\")+1,(fstNm.item(0)).getNodeValue().length());
									}
									screen.createListFile(choices);
									
								} else { //affichage simple de la réponse
									InputStream is = httpConn.openInputStream();
									ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
									int ch;
									while ((ch = is.read()) != -1){
										bytestream.write(ch);
									}
									response = new String(bytestream.toByteArray());
									bytestream.close();
								}
							} else {
								response = "Server error code "+httpConn.getResponseCode()+" : "+httpConn.getResponseMessage();
							} 
							httpConn.close();
							if (choix == 2) {
								screen.finish(response);
							}
						} catch (Exception e) {
							screen.myDialAlert("Exception : "+e.getMessage());
						}
					}
				});
			} 
			catch (Exception e) 
			{
				System.err.println("Caught an Exception: " + e.getMessage());
			}
		}
	}
}    
