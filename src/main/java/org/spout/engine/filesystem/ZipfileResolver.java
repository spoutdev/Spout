package org.spout.engine.filesystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipfileResolver extends FilepathResolver {

	public ZipfileResolver() {
		super(FileSystem.resourceFolder.getPath());
		
	}

	
	@Override
	public boolean existsInPath(String file, String path) {
		boolean has = false;
		ZipFile f = null;
		try {
			f = new ZipFile(directory + path);
			ZipEntry entry = f.getEntry(file);
			has = entry != null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			if(f != null)
				try {
					f.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			
		}
		return has;
	}


	@Override
	public InputStream getStream(String file, String path) {
		ZipFile f = null;
		FileInputStream stream = null;
		try {
			f = new ZipFile(directory + path);
			ZipEntry entry = f.getEntry(file);
			InputStream s = f.getInputStream(entry);
			return s; //TODO close the jar.
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			if(f != null)
				try {
					f.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			
		}
		return stream;
	}

}
