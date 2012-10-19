package mypackage;

import java.io.IOException;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.VideoControl;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;

public class CameraScreen extends MainScreen {
	VideoControl control;
	RichTextField textField;
	HelloBlackBerryScreen screen;
	
	public CameraScreen(HelloBlackBerryScreen screen){
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
			
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MediaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
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
//	    		    
//	    		    //UiApplication.getUiApplication().pushScreen(new HelloBlackBerryScreen(rawImage));
	                screen.set_rawImage(rawImage);
	                UiApplication.getUiApplication().pushScreen(screen);
	            }
	            catch(Exception e)
	            {
	                Dialog.alert(e.toString());
	            }
	        }
	    }           
	    return handled;                
	}
}
