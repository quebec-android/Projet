package ca.etsmtl.gti780.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.etsmtl.gti780.FileFolderListener;

public class FileFolderListenerTest {
	List<File> createdFiles = new ArrayList<File>();
	
	private FileFolderListener ffl;
	
	@Before
	public void initialize() {
		File folder = new File("testFolder");
		folder.mkdir();
		createdFiles.add(folder);
		
		ffl = new FileFolderListener(folder);
	}
	
	@After
	public void tearDown() throws Exception {
		for (File deleteMe : createdFiles) {
			deleteMe.delete();
		}
		createdFiles.clear();
	}

	@Test
	public void testgetFile() throws Exception{
		File file = new File("testFolder/testFile");
		file.createNewFile();
		createdFiles.add(file);
		
		File getFile = ffl.getFile("testFile");
		assertTrue(getFile.isFile());
	}
	
	/**
	 * TODO a modifier
	 * @throws Exception
	 */
	public void testCopyFile() throws Exception{
		File newFile = new File("newFile");
		
		assertTrue(ffl.copyFile(newFile));
		
		File testFile = ffl.getFile("newFile");
		assertTrue(testFile.isFile());
		
		createdFiles.add(newFile);
	}

}
