/*
 * This file is part of Spout (http://wiki.getspout.org/).
 * 
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.commons.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class FileUtil {
	private static final HashMap<String, String> fileNameCache = new HashMap<String, String>();
	public static long getCRC(File file, byte[] buffer) {

		FileInputStream in = null;
		
		try {
			in = new FileInputStream(file);
			return getCRC(in, buffer);
		} catch (FileNotFoundException e) {
			return 0;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	public static long getCRC(URL url, byte[] buffer) {

		InputStream in = null;
		
		try {
			URLConnection urlConnection = url.openConnection();
			
			in = urlConnection.getInputStream();
			return getCRC(in, buffer);
		} catch (IOException e) {
			return 0;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		
	}

	public static long getCRC(InputStream in, byte[] buffer) {
		if (in == null) {
			return 0;
		}
		
		long hash = 1;
		
		int read = 0;
		int i;
		while (read >= 0) {
			try {
				read = in.read(buffer);
				for (i=0; i < read; i++) {
					hash += (hash << 5) + (long)buffer[i];
				}
			} catch (IOException ioe) {
				return 0;
			}
		}

		return hash;
		
	}
	
	public static String getFileName(String url) {
		if (fileNameCache.containsKey(url)) {
			return fileNameCache.get(url);
		}
		int end = url.lastIndexOf('?');
		int lastDot = url.lastIndexOf('.');
		int slash = url.lastIndexOf('/');
		int forwardSlash = url.lastIndexOf("\\");
		slash = slash > forwardSlash ? slash : forwardSlash;
		end = end == -1 || lastDot > end ? url.length() : end;
		String result = url.substring(slash + 1, end).replaceAll("%20", " ");
		fileNameCache.put(url, result);
		return result;
	}
	
	public static boolean stringToFile(Collection<String> strings, File file) {
		BufferedWriter bw;

		try {
			bw = new BufferedWriter(new FileWriter(file));
		} catch (FileNotFoundException fnfe ) {
			return false;
		} catch (IOException ioe) {
			return false;
		}

		try {
			for( String line : strings ) {
				bw.write(line);
				bw.newLine();
			}
			return true;
		} catch (IOException ioe) {
			return false;
		} finally {
			try {
				bw.close();
			} catch (IOException ioe2) {}
		}

	}
	
	public static Collection<String> fileToString(File file) {
		
		BufferedReader br;

		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException fnfe ) {
			return null;
		} 

		String line;

		try {
			Collection<String> strings = new LinkedList<String>();
			while( (line=br.readLine()) != null ) {
				strings.add(line);
			}
			return strings;
		} catch (IOException ioe) {
			return null;
		} catch (NumberFormatException nfe) {
			return null;
		} finally {
			try {
				br.close();
			} catch (IOException ioe) {}
		}
	}
	
	private static final Collection<String> emptyCollection = new ArrayList<String>();
	
	public static boolean createFile(File file) {
		if (file == null) {
			return false;
		}
		File dir = file.getParentFile();
		if (dir != null) {
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					return false;
				}
			} else {
				if (!dir.isDirectory()) {
					return false;
				}
			}
		}
		return FileUtil.stringToFile(emptyCollection, file);
	}
	
}
