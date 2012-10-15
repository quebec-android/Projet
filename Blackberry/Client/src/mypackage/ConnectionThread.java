package mypackage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.HttpConnection;

import net.rim.device.api.io.transport.ConnectionDescriptor;
import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RichTextField;

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

class ConnectionThread extends Thread
{
	RichTextField textField;

	public ConnectionThread(RichTextField textField) {
		this.textField = textField;
	}

	public void run()
	{
		ConnectionFactory connFact = new ConnectionFactory();
		ConnectionDescriptor connDesc;
		connDesc = connFact.getConnection("http://localhost:8080/FileServer/FileServerServlet?action=getFile");
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

								InputStream is = httpConn.openInputStream();
								ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
								int ch;
								while ((ch = is.read()) != -1){
									bytestream.write(ch);
								}
								response = new String(bytestream.toByteArray());
								bytestream.close();
								Dialog.alert("Requete ok");

								textField.setText("Here is the response : \n"+response);
							} catch (IOException e) {
								Dialog.alert("exception : "+e.getMessage());
							}

						} else {
							textField.setText("I'm not workong bitch...");
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
