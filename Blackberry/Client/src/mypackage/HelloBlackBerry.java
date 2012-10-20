package mypackage;

import net.rim.device.api.ui.UiApplication;

/**
 * This class extends the UiApplication class, providing a graphical user interface.
 */
public class HelloBlackBerry extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            
     */
    public static void main( String[] args ) {
        HelloBlackBerry theApp = new HelloBlackBerry();
        theApp.enterEventDispatcher();
    }

    /**
     * Creates a new HelloBlackBerry object
     */
    public HelloBlackBerry() {
        pushScreen( new HelloBlackBerryScreen() );
    }
}
