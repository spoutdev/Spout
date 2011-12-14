/*
 * This file is part of Bukkit (http://bukkit.org/).
 * 
 * Bukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bukkit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Class containing file utilities
 */

public class FileUtil {

	/**
	 * This method copies one file to another location
	 * 
	 * @param inFile the source filename
	 * @param outFile the target filename
	 * @return true on success
	 */

	public static boolean copy(File inFile, File outFile) {
		if (!inFile.exists()) {
			return false;
		}

		FileChannel in = null;
		FileChannel out = null;

		try {
			in = new FileInputStream(inFile).getChannel();
			out = new FileOutputStream(outFile).getChannel();

			long pos = 0;
			long size = in.size();

			while (pos < size) {
				pos += in.transferTo(pos, 10 * 1024 * 1024, out);
			}
		} catch (IOException ioe) {
			return false;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException ioe) {
				return false;
			}
		}

		return true;

	}
}
