package mypackage;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;

public class HelloBlackBerryScreen extends MainScreen {
	HelloBlackBerryScreen me;
	RichTextField textField;
	byte[] _rawImage = null;
	
	public HelloBlackBerryScreen(byte[] image) {
	       setTitle("CopierColler");
	       _rawImage = image;
	       
	       me = this;
	       //if(_rawImage != null ){ //TODO mode dev, a changer
	    	   textField = new RichTextField("Retreive IP from picture\n\n\n\n\n\n\n");
			   add(textField);
	    	   ButtonField monBouton = new ButtonField("get IP");
	    	   add(monBouton);
	    	   monBouton.setChangeListener(new FieldChangeListener() {
	    	       public void fieldChanged(Field field, int context) {
	    	    	   ConnectionThread ct = new ConnectionThread(me);
	    	    	   ct.start();
	    	        }
	    	   });
			//} else{
	    	   RichTextField aSUPPRIMER;
	    	   
				aSUPPRIMER = new RichTextField("Select take image source in menu\n");
			    add(aSUPPRIMER);
			    ButtonField bSUPPRIMER = new ButtonField("take picture");
		    	  add(bSUPPRIMER);
		    	  bSUPPRIMER.setChangeListener(new FieldChangeListener() {
		    	       public void fieldChanged(Field field, int context) {
		    	    	   launchCameraScreen();
		    	        }
		    	   });
			   // ButtonField monBouton = new ButtonField("take picture");
	    	   //add(monBouton);
//	    	   monBouton.setChangeListener(new FieldChangeListener() {
//	    	       public void fieldChanged(Field field, int context) {
//	    	    	   launchCameraScreen();
//	    	        }
//	    	   });
			//}
    }
	
	private MenuItem pictureItem = new MenuItem("Take source picture",20,10){
		public void run(){
			launchCameraScreen();
		}
	};
	
	protected void makeMenu(Menu menu, int instance){
		if( instance == Menu.INSTANCE_DEFAULT ){
			String property = System.getProperty("supports.video.capture");
			if(property != null && property.equals("true")){
				menu.add(pictureItem);
			}
		}

		super.makeMenu(menu, instance);
	}
	
	protected void launchCameraScreen() {
		CameraScreen screen = new CameraScreen(this);
	    UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
		UiApplication.getUiApplication().pushScreen(screen);
	}

	public RichTextField getTextField() {
		return textField;
	}

	public void setTextField(RichTextField textField) {
		this.textField = textField;
	}

	public byte[] get_rawImage() {
		return _rawImage;
	}

	public void set_rawImage(byte[] _rawImage) {
		this._rawImage = _rawImage;
		String value = new String(_rawImage);
        textField.setText(value);
        System.out.println(value);
	}
	
	
}   
