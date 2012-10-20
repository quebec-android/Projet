package mypackage;

import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.VideoControl;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * 
 * Used to shot the barre code 
 *
 */
public class CameraScreen extends MainScreen {
	VideoControl control;
	RichTextField textField;
	HelloBlackBerryScreen screen;
	
	/**
	 * Push the CameraScreen
	 * Configure the camera
	 * @param screen
	 */
	public CameraScreen(HelloBlackBerryScreen screen){
		UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
		UiApplication.getUiApplication().pushScreen(this);
		this.screen = screen;
        try {
			Player player = Manager.createPlayer("capture://video?encoding=jpeg");
			player.start();
			control = (VideoControl)player.getControl("VideoControl");
			Field cameraView = (Field)control.initDisplayMode(VideoControl.USE_GUI_PRIMITIVE,"net.rim.device.api.ui.Field");
			control.setDisplayFullScreen(true);
			control.setVisible(true);
			
			add(cameraView);
			//rawImage = control.getSnapshot("encoding=jpeg&width=640&height=480&quality=normal");
			
        } catch (Exception e) {
			e.printStackTrace();
		}
        
	}
	
	/**
	 * Take the picture and save it in a byte array  
	 */
	protected boolean invokeAction(int action)
	{
	    boolean handled = super.invokeAction(action); 
	        
	    if(!handled)
	    {
	        if(action == ACTION_INVOKE)
	        {   
	            try
	            {                      
	                byte[] rawImage = control.getSnapshot(null);
	                
	                UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());			
	                if (rawImage == null) {
	                	 screen.myDialAlert("Erreur lors de la prise de photo. Veuillez recommencer.");
	                } else {
	                	screen.set_rawImage(rawImage);
	                }
	                UiApplication.getUiApplication().pushScreen(screen);
	            }
	            catch(Exception e)
	            {
	            	 screen.myDialAlert("Erreur lors de la prise de photo. Veuillez recommencer.");
	            }
	        }
	    }           
	    return handled;                
	}
}
