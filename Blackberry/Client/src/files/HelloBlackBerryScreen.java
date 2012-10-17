package files;

import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;

public class HelloBlackBerryScreen extends MainScreen {
	RichTextField myField;
	
	public HelloBlackBerryScreen() {
	       setTitle("CopierColler");
	       myField = new RichTextField("Trying to make HTTP connection... \n");
	       add(myField);
	       ConnectionThread ct = new ConnectionThread(this);
	       ct.start();
    }

	public RichTextField getMyField() {
		return myField;
	}

	public void setMyField(RichTextField myField) {
		this.myField = myField;
	}
}
