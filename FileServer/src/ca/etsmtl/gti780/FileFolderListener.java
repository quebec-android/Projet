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
	
	public File getFile(String getFileName){
		File getFile = new File(_folder.toString()+"/"+getFileName);
		
		if( files.contains(getFile)){
			getFile = files.get(files.indexOf(getFile));
			return getFile;
		}
		
		return null;
	}
	
	public boolean copyfile(File newFile){
		try{
			File file = new File(_folder.toString()+"/"+newFile.toString());
			if( file.createNewFile()){
				return true;
			}
			
			return false;
		}
		catch(Exception e){
			return false;
		}
	}
	
}
