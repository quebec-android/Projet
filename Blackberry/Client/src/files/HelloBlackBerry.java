package files;

import net.rim.device.api.ui.UiApplication;

/**
 * Simulateur 9550 v5.0
 * Connexion Wifi
 * 
 * This class extends the UiApplication class, providing a graphical user interface.
 */
public class HelloBlackBerry extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
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
