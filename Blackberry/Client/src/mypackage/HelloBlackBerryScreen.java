package mypackage;

import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;

public class HelloBlackBerryScreen extends MainScreen {
	RichTextField textField;
	
	public HelloBlackBerryScreen() {
	       setTitle("CopierColler");
	       textField = new RichTextField("Trying to make HTTP connection... \n");
	       add(textField);
	       ConnectionThread ct = new ConnectionThread(textField);
	       ct.start();
    }
}
