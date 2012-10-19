package mypackage;

import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;

public class HelloBlackBerryScreen extends MainScreen {
	HelloBlackBerryScreen me;
	RichTextField info;
	ButtonField sourceBouton;
	ButtonField fileBouton;
	ButtonField destBouton;
	byte[] _rawImage = null;
	
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
	       
	       fileBouton = new ButtonField("Choisir les fichiers");
    	   add(fileBouton);
    	   fileBouton.setChangeListener(new FieldChangeListener() {
    	       public void fieldChanged(Field field, int context) {
    	    	   ConnectionThread ct = new ConnectionThread(me);
    	    	   ct.start();
    	        }
    	   });
    	   
	       destBouton = new ButtonField("PC destination");
	       add(destBouton);
	       destBouton.setChangeListener(new FieldChangeListener() {
	    	   public void fieldChanged(Field field, int context) {
	    		   launchCameraScreen();
	    	   }
	       });
	       
	       //TODO on pourra le virer après
	       info = new RichTextField("message : ");
	       add(info);
    }
	
	protected void launchCameraScreen() {
		CameraScreen screen = new CameraScreen(this);
	    UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
		UiApplication.getUiApplication().pushScreen(screen);
	}

	public byte[] get_rawImage() {
		return _rawImage;
	}

	public void set_rawImage(byte[] _rawImage) {
		this._rawImage = _rawImage;
		//associate();
	}
	
	public void associate() {
	    String _url = "http://localhost:8080/CodeServer/CodeServerServlet";
	    byte[] _data = "".getBytes();
	    HttpConnection _httpConnection;
	    OutputStream os;
	    InputStream is;
	    try {
	    	_httpConnection = (HttpConnection)Connector.open(_url);
            _httpConnection.setRequestMethod(HttpConnection.POST);
//            _httpConnection.setRequestProperty("User-Agent","Profile/MIDP-2.0 Configuration/CLDC-1.0");
//            _httpConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
//            _httpConnection.setRequestProperty("Content-Length",new Integer(_rawImage.length).toString());
            os = _httpConnection.openOutputStream();
            os.write(_rawImage);
            
            int rc = _httpConnection.getResponseCode();
            if(rc == HttpConnection.HTTP_OK) {
                  is = _httpConnection.openInputStream();
                  is.read(_data);
            } else {
              _data = null;
            }
       } catch(Exception e) {
       }
       info.setText(new String(_data));
	}

	public RichTextField getInfo() {
		return info;
	}

	public void setInfo(RichTextField info) {
		this.info = info;
	}

	public ButtonField getSourceBouton() {
		return sourceBouton;
	}

	public void setSourceBouton(ButtonField sourceBouton) {
		this.sourceBouton = sourceBouton;
	}

	public ButtonField getFileBouton() {
		return fileBouton;
	}

	public void setFileBouton(ButtonField fileBouton) {
		this.fileBouton = fileBouton;
	}

	public ButtonField getDestBouton() {
		return destBouton;
	}

	public void setDestBouton(ButtonField destBouton) {
		this.destBouton = destBouton;
	}
	
	
}   
