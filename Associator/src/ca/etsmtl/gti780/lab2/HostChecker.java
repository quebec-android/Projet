package ca.etsmtl.gti780.lab2;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Classe statique utilitaire pour vérifier si un hote est actif.
 * 
 * @author Luc Trudeau
 * 
 */
public final class HostChecker {

	/**
	 * Valide qu'un hote est actif. Un hote est défini comme
	 * actif s'il répond à une requête HTTP.
	 * 
	 * @param host
	 *            Adresse IP de l'hote à vérifier.
	 * @return Si l'hote est actif.
	 */
	public static boolean isHostActive(String host) {
		final String urlStr = "http://" + host + "/";
		try {
			final URL url = new URL(urlStr);
			final URLConnection urlConnection = url
					.openConnection();
			urlConnection.connect();
			return true;
		} catch (ConnectException e) {
			return false;
		} catch (IOException e1) {
			return false;
		}
	}

	/**
	 * Instancier cette classe c'est mal!
	 */
	private HostChecker() {
	}

}