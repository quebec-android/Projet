package ca.etsmtl.gti780;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Our implementatation of the FolderListener interface 
 *
 */
public class FileFolderListener implements FolderListener {
	private static List<File> files = new ArrayList<File>();
	private File _folder;
	
	public FileFolderListener(File folder){
		_folder = folder;
		for(File file: _folder.listFiles()){
			files.add(file);
		}
	}
	
	@Override
	public void folderHasChanged() {
		files = new ArrayList<File>();
		for(File file: _folder.listFiles()){
			files.add(file);
		}
	}
	
	public List<File> getFiles(){
		return files;
	}
	
	/**
	 * Find a file in the listened folder 
	 * @param getFileName
	 * @return
	 */
	public File getFile(String getFileName){
		File getFile = new File(_folder.toString()+"/"+getFileName);
		
		if( files.contains(getFile)){
			getFile = files.get(files.indexOf(getFile));
			return getFile;
		}
		
		return null;
	}
	
	/**
	 * Creer un nouveau fichier ou ecrase l'ancien et ajoute le contenu dans celui-ci
	 * @param newFile
	 * @param content
	 * @return
	 * 
	 * TODO
	 * enlever le "__" qui permet de ne pas ecraser le fichier existant sur le localhost
	 */
	public boolean copyfile(String newFile, InputStream content){
		try{
			
	        FileOutputStream fos = new FileOutputStream(new File(_folder.toString()+"/__"+newFile));

	        byte[] buffer = new byte[4096];
	        int length; 
	        while((length = content.read(buffer)) > 0) {
	            fos.write(buffer, 0, length);
	        }
	        fos.close();
	        return true;
		}
		catch(Exception e){
			return false;
		}
	}

	/**
	 * 								GETTERS AND SETTERS
	 */
	
	
	public File get_folder() {
		return _folder;
	}

	public void set_folder(File _folder) {
		this._folder = _folder;
	}
	
}
