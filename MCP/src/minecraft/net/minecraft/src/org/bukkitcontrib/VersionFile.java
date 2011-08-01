package org.getspout.Spout;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.getspout.Spout.io.FileUtil;

public class VersionFile {
	private String version;
	
	public VersionFile(String version) {
		this.version = version;
	}
	
	public boolean create() {
		File vFile = new File(FileUtil.getCacheDirectory(), "version");
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(vFile));
			out.write(this.version);
			out.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
