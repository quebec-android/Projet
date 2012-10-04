package ca.etsmtl.gti780;

/**
 * Interface à implémenter pour observer les changements à un dossier.
 * 
 * @author Luc Trudeau
 * 
 */
public interface FolderListener {

	/**
	 * Cette méthode sera appelée lorsqu'un changement est détecté à l'intérieur
	 * du dossier observé.
	 */
	void folderHasChanged();
}
