/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.io.regionfile;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class CachedRandomAccessFileTest {
	private static String filename = "regionfile.dat";

	@Test
	public void test() throws IOException {
		File file = new File(filename);
		if (file.exists()) {
			file.delete();
		}

		System.out.println("File: " + file.getAbsolutePath());

		System.out.println("Opening file");
		CachedRandomAccessFile craf = new CachedRandomAccessFile(file, "rw", 8);

		byte[] buf = new byte[64];
		byte[] read = new byte[64];

		for (int i = 0; i < buf.length; i++) {
			buf[i] = (byte)i;
		}

		craf.seek(64);

		System.out.println("Writing ramp to 64 - 127");
		craf.write(buf, 0, buf.length);

		System.out.println("Writing ramp to 128 - 191");
		craf.write(buf, 0, buf.length);

		craf.seek(64);

		System.out.println("Checking ramp from 64 - 127");
		craf.readFully(read);
		assertArray(buf, read);

		System.out.println("Checking ramp from 128 - 191");
		craf.readFully(read);
		assertArray(buf, read);

		System.out.println("Closing file");
		craf.close();

		System.out.println("Opening file");
		craf = new CachedRandomAccessFile(file, "rw", 8);

		craf.seek(0);

		System.out.println("Checking zeros from 0 - 63");
		craf.readFully(read);
		assertArray(read, (byte)0);

		System.out.println("Checking ramp from 64 - 127");
		craf.readFully(read);
		assertArray(buf, read);

		System.out.println("Checking ramp from 128 - 191");
		craf.readFully(read);
		assertArray(buf, read);

		System.out.println("Closing file");
		craf.close();

		System.out.println("Opening file");
		craf = new CachedRandomAccessFile(file, "rw", 8);

		craf.seek(0);

		System.out.println("Reading from end of first page");
		craf.seek(252);
		craf.readInt();

		craf.seek(64);

		System.out.println("Checking ramp from 64 - 127");
		craf.readFully(read);
		assertArray(buf, read);

		System.out.println("Checking ramp from 128 - 191");
		craf.readFully(read);
		assertArray(buf, read);

		craf.seek(252);
		craf.writeInt(0x1234567);

		craf.seek(252);
		int r = craf.readInt();

		assertTrue("Integer read mismatch", r == 0x1234567);

		craf.close();

		if (file.exists()) {
			file.delete();
		}
	}

	private void assertArray(byte[] array, byte b) {
		for (int i = 0; i < array.length; i++) {
			assertTrue("Array not all " + b, array[i] == b);
		}
	}

	private void assertArray(byte[] array, byte[] expect) {
		for (int i = 0; i < array.length; i++) {
			assertTrue("Array does not match at position " + i, array[i] == expect[i]);
		}
	}
}
