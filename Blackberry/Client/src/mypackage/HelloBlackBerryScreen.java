package mypackage;

import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.HttpConnection;

import net.rim.blackberry.api.browser.URLEncodedPostData;
import net.rim.device.api.io.transport.ConnectionDescriptor;
import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;

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


	public HelloBlackBerryScreen() {
	       setTitle("CopierColler");
	       
	       me = this;
	       
	       sourceBouton = new ButtonField("PC source");
	       add(sourceBouton);
	       sourceBouton.setChangeListener(new FieldChangeListener() {
	    	   public void fieldChanged(Field field, int context) {
	    		   launchCameraScreen();
	    	   }
	       });
    	   
	       destBouton = new ButtonField("PC destination");
	       add(destBouton);
	       destBouton.setChangeListener(new FieldChangeListener() {
	    	   public void fieldChanged(Field field, int context) {
	    		   launchCameraScreen();
	    	   }
	       });
	       
    }
	
	/**
	 * Override method
	 * Display an alert when the app is closing
	 */
	public boolean onClose()
    {
       myDialAlert("Thanks for using our app :) ");   
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
	
	protected void launchCameraScreen() {
		CameraScreen screen = new CameraScreen(this);
	}

	/**
	 * Send a post request to get the IP associated to a picture
	 */
	public void associate() {
		//essai en get
//		ConnectionThread ct = new ConnectionThread(me,_rawImage);
// 	    ct.start();
		
		//essai en POST
		String url = "http://localhost:8080/CodeServer/CodeServerServlet";

		URLEncodedPostData postData = new URLEncodedPostData(URLEncodedPostData.DEFAULT_CHARSET, false);
		//passing q’s value and ie’s value
		postData.append("image",new String(_rawImage));

		ConnectionFactory conFactory = new ConnectionFactory();
		ConnectionDescriptor conDesc = null;
		try{
			conDesc = conFactory.getConnection(url);
		}catch(Exception e){
			System.out.println(e.toString()+":"+e.getMessage());
		}
		String response = ""; // this variable used for the server response
		// if we can get the connection descriptor from ConnectionFactory
		if(null != conDesc){
			try{
				HttpConnection connection = (HttpConnection)conDesc.getConnection();
				//set the header property
				connection.setRequestMethod(HttpConnection.POST);
				connection.setRequestProperty("Content-Length", Integer.toString(postData.size())); //body content of post data
				connection.setRequestProperty("Connection", "close"); // close the connection after success sending request and receiving response from the server
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // we set the content of this request as application/x-www-form-urlencoded, because the post data is encoded as form-urlencoded(if you print the post data string, it will be like this -> q=remoQte&ie=UTF-8).

				//now it is time to write the post data into OutputStream
				OutputStream out = connection.openOutputStream();
				out.write(postData.getBytes());
				out.flush();
				out.close();

				int responseCode = connection.getResponseCode(); //when this code is called, the post data request will be send to server, and after that we can read the response from the server if the response code is 200 (HTTP OK).
				if(responseCode == HttpConnection.HTTP_OK){
					//read the response from the server, if the response is ascii character, you can use this following code, otherwise, you must use array of byte instead of String
					InputStream in = connection.openInputStream();
					StringBuffer buf = new StringBuffer();
					int read = -1;
					while((read = in.read())!= -1)
						buf.append((char)read);
					response = buf.toString();
				}
				//info.setText(response);
				//don’t forget to close the connection
				connection.close();

			}catch(Exception e){
				System.out.println(e.toString()+":"+e.getMessage());
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
    	}
	}
	
	/**
	 * 													GETTERS AND SETTERS
	 * 
	 */
	
	/**
	 * 
	 * @return
	 */
	public byte[] get_rawImage() {
		return _rawImage;
	}

	/**
	 * 
	 * @param _rawImage
	 */
	public void set_rawImage(byte[] _rawImage) {
		this._rawImage = _rawImage;
		myDialAlert("Photo OK");
		//TODO simule la bonne association, à supprimer
		//associate();
		ipSource= " ";
		ipDest= " ";
		updateScreen();
	}
	
	/**
	 * 
	 * @return
	 */
	public ButtonField getSourceBouton() {
		return sourceBouton;
	}

	/**
	 * 
	 * @param sourceBouton
	 */
	public void setSourceBouton(ButtonField sourceBouton) {
		this.sourceBouton = sourceBouton;
	}

	/**
	 * 
	 * @return
	 */
	public ButtonField getDestBouton() {
		return destBouton;
	}

	/**
	 * 
	 * @param destBouton
	 */
	public void setDestBouton(ButtonField destBouton) {
		this.destBouton = destBouton;
	}

	/**
	 * 
	 * @return
	 */
	public ObjectChoiceField getListFile() {
		return listFile;
	}

	/**
	 * 
	 * @param listFile
	 */
	public void setListFile(ObjectChoiceField listFile) {
		this.listFile = listFile;
	}

	/**
	 * 
	 * @return
	 */
	public String getIpSource() {
		return ipSource;
	}

	/**
	 * 
	 * @param ipSource
	 */
	public void setIpSource(String ipSource) {
		this.ipSource = ipSource;
	}

	/**
	 * 
	 * @return
	 */
	public String getIpDest() {
		return ipDest;
	}

	/**
	 * 
	 * @param ipDest
	 */
	public void setIpDest(String ipDest) {
		this.ipDest = ipDest;
	}
}   
