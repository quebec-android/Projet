package ca.etsmtl.gti780;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Le WatchDog surveil un dossier afin d'informer le FolderListener s'il y a des
 * changements.
 * 
 * @author Luc Trudeau
 * 
 */
public class WatchDog implements Closeable {
	
	private static final int SLEEP_MILIS = 1000;

	private boolean running = true;

	private final File folder;

	private final FolderListener folderListener;

	/**
	 * Initialise le WatchDog, appellez watch() pour débuter la surveillance.
	 * 
	 * @param folder
	 *            Dossier surveiller par le WatchDog.
	 * @param folderListener
	 *            Observateur qui sera informé des changements apportés à
	 *            folder;
	 * @throws IllegalArgumentException
	 *             Lancé si folder n'existe pas
	 */
	public WatchDog(File folder, FolderListener folderListener) {
		if (!folder.exists()) {
			throw new IllegalArgumentException("Folder " + folder.toString()
					+ " does not exists");
		}
		this.folder = folder;
		this.folderListener = folderListener;
	}

	/**
	 * Débute la surveillance sur le dossier folder. Cette méthode est
	 * asynchrone et crée un nouveau thread qui doit être arrêter à l'aide de la
	 * méthode close.
	 */
	public void watch() {
		thread.start();
	}

	/**
	 * Cette méthode arrête la surveillance sur le dossier folder et par le fait
	 * même le thread dédié à cette tâche. Cette méthode est asynchrone, la
	 * méthode join permet d'attendre jusqu'à ce que cette classe soit bien
	 * fermée.
	 * 
	 * @throws IOException
	 *             Cette exception n'est pas lancée mais est requise par
	 *             l'interface Closeable.
	 */
	@Override
	public void close() throws IOException {
		running = false;
	}

	/**
	 * Cette méthode bloc jusqu'à ce que le thread de surveillance meurt.
	 */
	public void join() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private final Thread thread = new Thread() {

		public void run() {
			int oldFileSize = folder.list().length;
			while (running) {
				try {
					TimeUnit.MILLISECONDS.sleep(SLEEP_MILIS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				int fileSize = folder.list().length;
				System.out.println(fileSize);
				if (oldFileSize != fileSize) {
					oldFileSize = fileSize;
					folderListener.folderHasChanged();
				}
			}
		}
	};
}
