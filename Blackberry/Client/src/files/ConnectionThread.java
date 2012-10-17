package files;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.StreamConnection;

import net.rim.device.api.io.transport.ConnectionDescriptor;
import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.xml.parsers.DocumentBuilder;
import net.rim.device.api.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
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
 * regarder sujet TP pour prendre des photos (lien)
 * 
 * @author Fab
 *
 */

class ConnectionThread extends Thread
{
	HelloBlackBerryScreen screen;
	
	public ConnectionThread(HelloBlackBerryScreen screen) {
		this.screen = screen;
	}

	public void run()
	{
		ConnectionFactory connFact = new ConnectionFactory();
		ConnectionDescriptor connDesc;
		connDesc = connFact.getConnection("http://localhost:8080/FileServer/FileServerServlet?action=getFiles");
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
								String response = "";

								Document doc;
				                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
				                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
				                docBuilder.isValidating();
				                doc = docBuilder.parse(httpConn.openInputStream());
				                doc.getDocumentElement().normalize();
				                NodeList list = doc.getElementsByTagName("list");
				                for (int i=0; i<list.getLength();i++){
				                	response+=list.item(i).getNodeName();
				                }
				                screen.getMyField().setText("Here is the response : \n"+response);


								
								
//								InputStream is = httpConn.openInputStream();
//								ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
//								int ch;
//								while ((ch = is.read()) != -1){
//									bytestream.write(ch);
//								}
//								bytestream.close();
//								Dialog.alert("Requete ok");
//
//								screen.getMyField().setText("Here is the response : \n"+response);
//								String choices[] = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
//							    int iSetTo = 2;
//							    screen.add(new ObjectChoiceField("First Drop-down List",choices,iSetTo));
								
								
							} catch (Exception e) {
								Dialog.alert("exception : "+e.getMessage());
							}

						} else {
							screen.getMyField().setText("I'm not workong bitch...");
							Dialog.alert("Server is not responding");
						}
					}
				});
			} 
			catch (IOException e) 
			{
				System.err.println("Caught IOException: " + e.getMessage());
			}
		}
	}
}    
