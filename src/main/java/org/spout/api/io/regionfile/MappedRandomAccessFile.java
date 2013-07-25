/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.io.regionfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class MappedRandomAccessFile {

	private final File filePath;
	private final String permissions;
	private long pos = 0;
	private final ArrayList<MappedByteBuffer> pages = new ArrayList<MappedByteBuffer>();
	private final int PAGE_SHIFT;
	private final int PAGE_SIZE;
	private final long PAGE_MASK;
	
	private RandomAccessFile file;

	public MappedRandomAccessFile(File filePath, String permissions) throws FileNotFoundException {
		this(filePath, permissions, 17);
	}

	public MappedRandomAccessFile(File filePath, String permissions, int pageShift) throws FileNotFoundException {
		this.file = new RandomAccessFile(filePath, permissions);
		this.PAGE_SHIFT = pageShift;
		PAGE_SIZE = (1 << PAGE_SHIFT);
		PAGE_MASK = PAGE_SIZE - 1;
		this.filePath = filePath;
		this.permissions = permissions;
	}

	public long length() throws IOException {
		return file.length();
	}

	public void close() throws IOException {
		for (MappedByteBuffer m : pages) {
			if (m != null) {
				m.force();
			}
		}
		file.close();
	}

	byte[] intArray = new byte[4];
	
	public void writeInt(int i) throws IOException {
		intArray[0] = (byte) (i >> 24);
		intArray[1] = (byte) (i >> 16);
		intArray[2] = (byte) (i >> 8);
		intArray[3] = (byte) (i >> 0);
		write(intArray, 0, 4);
	}

	public int readInt() throws IOException {
		readFully(intArray);
		int i = 0;
		i |= (intArray[0] & 0xFF) << 24;
		i |= (intArray[1] & 0xFF) << 16;
		i |= (intArray[2] & 0xFF) << 8;
		i |= (intArray[3] & 0xFF) << 0;
		return i;
	}

	private MappedByteBuffer getPage(int pageIndex) throws IOException {
		while (pageIndex >= pages.size()) {
			pages.add(null);
		}
		MappedByteBuffer page = pages.get(pageIndex);
		if (page == null) {
			long pagePosition = pageIndex << PAGE_SHIFT;
			boolean interrupted = false;
			boolean success = false;
			try {
				while (!success) {
					try {
						interrupted |= Thread.interrupted();
						page = file.getChannel().map(FileChannel.MapMode.READ_WRITE, pagePosition, PAGE_SIZE);
						success = true;
					} catch (ClosedByInterruptException e) {
						file = new RandomAccessFile(filePath, permissions);
					} catch (IOException e) {
						throw new IOException("Unable to refresh RandomAccessFile after interrupt, " + filePath, e);
					}
				}
			} finally {
				if (interrupted) {
					Thread.currentThread().interrupt();
				}
			}
			pages.set(pageIndex, page);
		}
		return page;

	}

	public void seek(long pos) throws IOException {
		this.pos = pos;
	}

	public void readFully(byte[] b) throws IOException {
		int pageIndex = (int)(pos >> PAGE_SHIFT);
		int offset = (int)(pos & PAGE_MASK);
		int endPageOne = Math.min(b.length + offset, PAGE_SIZE);

		MappedByteBuffer page = getPage(pageIndex);

		int j = 0;

		int length = endPageOne - offset;

		page.position(offset);

		page.get(b, j, length);
		j += length;

		while (b.length > j) {
			pageIndex++;
			page = getPage(pageIndex);
			page.position(0);
			if (b.length - j > PAGE_SIZE) {
				length = PAGE_SIZE;
			} else {
				length = b.length - j;
			}

			page.get(b, j, length);
			j += length;
		}

		pos += b.length;
	}

	public void write(byte[] b, int off, int len) throws IOException {
		int pageIndex = (int)(pos >> PAGE_SHIFT);
		int offset = (int)(pos & PAGE_MASK);
		int endPageOne = Math.min(len + offset, PAGE_SIZE);

		MappedByteBuffer page = getPage(pageIndex);

		int j = 0;
		
		int length = endPageOne - offset;
		
		page.position(offset);
		
		page.put(b, off + j, length);
		j += length;

		while (len > j) {
			pageIndex++;
			page = getPage(pageIndex);
			page.position(0);
			if (len - j > PAGE_SIZE) {
				length = PAGE_SIZE;
			} else {
				length = len - j;
			}
			page.put(b, off + j, length);
			j += length;
		}

		pos += len;
	}
}
