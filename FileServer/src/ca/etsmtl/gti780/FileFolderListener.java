package ca.etsmtl.gti780;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFolderListener implements FolderListener {
	private static final List<File> files = new ArrayList<File>();
	private File _folder;
	
	public FileFolderListener(File folder){
		_folder = folder;
		for(File file: _folder.listFiles()){
			files.add(file);
		}
	}
	
	@Override
	public void folderHasChanged() {
		for(File file: _folder.listFiles()){
			if(!files.contains(file)){
				files.add(file);
			}
		}
	}
	
	public List<File> getFiles(){
		return files;
	}
}
