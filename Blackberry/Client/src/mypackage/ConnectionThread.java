package mypackage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.HttpConnection;

import net.rim.device.api.io.transport.ConnectionDescriptor;
import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ObjectChoiceField;
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
 * @author Fab
 * 
 * Fonctionnement :
 * 		Démarrer un autre eclipse avec les autres projets
 * 		Mettre le serveur en route, faire l'association
 * 		Lancer le client Blackberry, aller dans les paramètres pour activer le wifi et désactiver le réseau normal
 * 		Lancer l'appli, elle ne fonctionne pas du premier coup, je ne sais pas pourquoi... 
 *
 */

/**TODO
 * 
 * prenne une photo
 * pas de sauvegarde
 * envoyer en POST (payload?)
 * regarder sujet TP pour prendre des photos
 * 
 * @author Fab
 *
 */

class ConnectionThread extends Thread
{
	HelloBlackBerryScreen screen;
	byte[] img = null;
	int choix = 0; //1 --> getFiles && 2 --> copyFile && 3 --> send image
	String label = null;
	
	public ConnectionThread(HelloBlackBerryScreen screen, int choix) {
		this.screen = screen;
		this.choix = choix;
	}
	
	public ConnectionThread(HelloBlackBerryScreen screen, String label) {
		this.screen = screen;
		this.label = label;
		choix = 2;
	}
	
	public ConnectionThread(HelloBlackBerryScreen screen, byte[] img) {
		this.screen = screen;
		this.img = img; 
		choix = 3;
	}

	public void run()
	{
		String url = "";
		if (choix == 1) {
			url = "http://localhost:8080/FileServer/FileServerServlet?action=getFiles";
		} else if (choix == 2) {
			//récupérer le nom au lieu de l'index...
			screen.myDialAlert(label);
			url = "http://localhost:8080/FileServer/FileServerServlet?action=copyFile&IPsource=localhost&file="+label;
		} else {
			screen.myDialAlert(new String(img));
			url = "http://localhost:8080/CodeServer/CodeServerServlet?image="+new String(img);
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
						if(iResponseCode==200){
							try {
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
									String response = "";

									InputStream is = httpConn.openInputStream();
									ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
									int ch;
									while ((ch = is.read()) != -1){
										bytestream.write(ch);
									}
									response = new String(bytestream.toByteArray());
									bytestream.close();
									screen.getInfo().setText("Requete ok : "+response);
								}
							   
							} catch (Exception e) {
								screen.myDialAlert("exception : "+e.getMessage());
							}
						} else {
							screen.getInfo().setText("I'm not workong bitch...");
							screen.myDialAlert("Server is not responding");
						}
					}
				});
			} 
			catch (IOException e) 
			{
				System.err.println("Caught an IOException: " + e.getMessage());
			}
		}
	}
}    
