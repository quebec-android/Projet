package mypackage;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.microedition.io.HttpConnection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.rim.device.api.io.transport.ConnectionDescriptor;
import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.xml.parsers.DocumentBuilder;
import net.rim.device.api.xml.parsers.DocumentBuilderFactory;

/**
 * MainClass
 * Contains all the usefull information like IPs...
 * Allows to manage the differents elements on the screen 
 *
 */
public class HelloBlackBerryScreen extends MainScreen {
	HelloBlackBerryScreen me;
	ButtonField sourceBouton;
	ButtonField destBouton;
	ObjectChoiceField listFile;
	byte[] _rawImage = null;
	
	String ipSource = null;
	String ipDest = null;

	static final String BOUNDARY = "-----------------V2ymHFg03ehbqqZCaJO6jy";


	public HelloBlackBerryScreen() {
	       setTitle("CopierColler");
	       
	       me = this;
	       
	       sourceBouton = new ButtonField("PC source");
	       add(sourceBouton);
	       sourceBouton.setChangeListener(new FieldChangeListener() {
	    	   public void fieldChanged(Field field, int context) {
	    		   launchCameraScreen(1);
	    	   }
	       });
    	   
	       destBouton = new ButtonField("PC destination");
	       add(destBouton);
	       destBouton.setChangeListener(new FieldChangeListener() {
	    	   public void fieldChanged(Field field, int context) {
	    		   launchCameraScreen(2);
	    	   }
	       });
	       
    }
	
	/**
	 * Override method
	 * Display an alert when the app is closing
	 */
	public boolean onClose()
    {
       Dialog.alert("Thanks for using our app :) ");   
       System.exit(0);
       return true;
    }
	
	/**
	 * Display the last message before closing the app
	 * @param msg
	 */
	protected void finish(String msg) {
		this.deleteAll();
		RichTextField text = new RichTextField(msg);
		add(text);
	}
	
	/**
	 * Delete the buttons when the IP have been found
	 */
	protected void updateScreen() {
		if (ipSource!=null && ipDest!=null) {
			this.deleteAll();
			ConnectionThread ct = new ConnectionThread(me,1);
			ct.start();
		}
	}
	
	/**
	 * Create a list with all the available files
	 * When you choose a file, it sends a get request to copy the file
	 * @param choices
	 */
	protected void createListFile(String[] choices) {
		int iSetTo = 0;
		listFile = new ObjectChoiceField("",choices,iSetTo)	{
	            
	            protected void fieldChangeNotify(int context)
	            {
	        		if (this.getSelectedIndex()!=0) {
	        			ConnectionThread ct = new ConnectionThread(me,this.getChoice(this.getSelectedIndex()).toString());
	        			ct.start();
	        		}
	            }
	    };
	    this.add(listFile);
	    listFile.setPadding(listFile.getPaddingTop(), listFile.getPaddingRight(), listFile.getPaddingBottom(), -170);
	}
	
	/**
	 * Launch Camera Screen 
	 * 
	 * @param source : indicates if tracking the IP source or the IP destination
	 */
	protected void launchCameraScreen(int source) {
		CameraScreen screen = new CameraScreen(this, source);
	}

	/**
	 * Send a post request to get the IP associated to a picture
	 * @param source
	 */
	public void associate(int source) {
		String bypass = "localhost:8080";
		String url = "http://"+bypass+"/CodeServer/CodeServerServlet";
		//String url = "http://"+ipSource+"/CodeServer/CodeServerServlet";
		Hashtable params = new Hashtable();
		
		ConnectionFactory conFactory = new ConnectionFactory();
		ConnectionDescriptor conDesc = null;
		try{
			conDesc = conFactory.getConnection(url);
		}catch(Exception e){
			System.out.println(e.toString()+":"+e.getMessage());
		}

		if(null != conDesc){
			HttpConnection connection;
			try{
				HttpMultipartRequest req = new HttpMultipartRequest(url,params,"upload_field","codebarre.jpg", "image/jpeg", _rawImage);
				
				connection = (HttpConnection)conDesc.getConnection();
				
				//set the header property
				connection.setRequestMethod(HttpConnection.POST);
				connection.setRequestProperty("Connection", "keep-alive"); // close the connection after success sending request and receiving response from the server
				connection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+req.getBoundaryString());

				//now it is time to write the post data into OutputStream
				OutputStream out = connection.openOutputStream();
				out.write(req.postBytes);
				out.flush();
				out.close();

				int responseCode = connection.getResponseCode(); 
				if(responseCode == HttpConnection.HTTP_OK){	
					//parser le XML pour trouver l'IP
					Document doc;
					DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
					docBuilder.isValidating();
					doc = docBuilder.parse(connection.openInputStream());
					doc.getDocumentElement().normalize();
					
					NodeList list = doc.getElementsByTagName("ip");
					if (list.getLength() == 1) {
						Element element1 = (Element) list.item(0);
						NodeList fstNm = element1.getChildNodes();
						if (source == 1) {
							ipSource = (fstNm.item(0)).getNodeValue();
							myDialAlert("IP source = "+ipSource);
						} else {
							ipDest = (fstNm.item(0)).getNodeValue();
							myDialAlert("IP destination = "+ipDest);
						}
					} else {
						myDialAlert("Error while sending the picture.");
					}
				} else {
					myDialAlert("Server error code "+responseCode+" : "+connection.getResponseMessage());
				}
				//don’t forget to close the connection
				connection.close();

			}catch(Exception e){
				myDialAlert("Error while sending the picture.");
			}
		}
	}

	/**
	 * Display alert with messages
	 * Avoid a runtime exception
	 * @param msg
	 */
	public void myDialAlert(final String msg){
		try {
    		UiApplication.getUiApplication().invokeLater(new Runnable()
    		{
    			public void run() 
    		    {
    			  Dialog.alert(msg);
    			}	
    			
    		});
    	} catch (Exception e) {
    		//do nothing
    	}
	}
	
	/**
	 * 							GETTERS AND SETTERS
	 * 
	 */
	
	public byte[] get_rawImage() {
		return _rawImage;
	}

	public void set_rawImage(final byte[] _rawImage, int source) {
		this._rawImage = _rawImage;
		associate(source);
		updateScreen();
	}
	
	public ButtonField getSourceBouton() {
		return sourceBouton;
	}

	public void setSourceBouton(ButtonField sourceBouton) {
		this.sourceBouton = sourceBouton;
	}

	public ButtonField getDestBouton() {
		return destBouton;
	}

	public void setDestBouton(ButtonField destBouton) {
		this.destBouton = destBouton;
	}
	
	public ObjectChoiceField getListFile() {
		return listFile;
	}

	public void setListFile(ObjectChoiceField listFile) {
		this.listFile = listFile;
	}

	public String getIpSource() {
		return ipSource;
	}

	public void setIpSource(String ipSource) {
		this.ipSource = ipSource;
	}

	public String getIpDest() {
		return ipDest;
	}

	public void setIpDest(String ipDest) {
		this.ipDest = ipDest;
	}
}   
