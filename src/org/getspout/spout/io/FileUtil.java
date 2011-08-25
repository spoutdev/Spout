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
package org.getspout.spout.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class FileUtil {
	
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
	
	public static String getFileName(String Url) {
		int slashIndex = Url.lastIndexOf('/');
		int dotIndex = Url.lastIndexOf('.', slashIndex);
		if (dotIndex == -1 || dotIndex < slashIndex) {
			return Url.substring(slashIndex + 1).replaceAll("%20", " ");
		}
		return Url.substring(slashIndex + 1, dotIndex).replaceAll("%20", " ");
	}
	
}
