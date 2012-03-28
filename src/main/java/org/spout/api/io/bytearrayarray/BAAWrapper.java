/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.api.io.bytearrayarray;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.io.regionfile.SimpleRegionFile;

public class BAAWrapper {
	
	private final static ByteArrayArray openInProgress = BAAOpenInProgress.getInstance();

	private AtomicReference<ByteArrayArray> baaRef = new AtomicReference<ByteArrayArray>(null);
	
	private final File file;
	private final int segmentSize;
	private final int entries;
	private final int timeout;
	
	public BAAWrapper(File file, int segmentSize, int entries, int timeout) {
		this.file = file;
		this.segmentSize = segmentSize;
		this.entries = entries;
		this.timeout = timeout;
	}
	

	/**
	 * This method should be called periodically in order to see if the ByteArrayArray has timed out.  It always returns immediately.<br>
	 * <br>
	 * It will only close the array if no block OutputStreams are open and the last access occurred more than the timeout previously
	 */
	public void timeoutCheck() {
		ByteArrayArray baa = baaRef.get();
		if (baa != null) {
			try {
				baa.closeIfTimedOut();
			} catch (IOException ioe) {
			}
			if (baa.isClosed()) {
				baaRef.compareAndSet(baa, null);
			}
		}
	}
	
	/**
	 * Gets the DataOutputStream corresponding to a given block.<br>
	 * <br>
	 * WARNING: This block will be locked until the stream is closed
	 * 
	 * @param i the block index
	 * @return the DataOutputStream
	 */
	public DataOutputStream getBlockOutputStream(int i) {
		while (true) {
			ByteArrayArray baa = getByteArrayArray();
			if (baa == null) {
				return null;
			}
			DataOutputStream out;
			try {
				out = baa.getOutputStream(i);
			} catch (BAAClosedException e) {
				continue;
			} catch (IOException e) {
				return null;
			}
			return out;
		}
	}
	
	/**
	 * Gets the DataInputStream corresponding to a given Chunk.<br>
	 * <br>
	 * The stream is based on a snapshot of the array.
	 * 
	 * @param i the block index
	 * @return the DataInputStream
	 */
	public DataInputStream getBlockInputStream(int i) {
		while (true) {
			ByteArrayArray baa = getByteArrayArray();
			if (baa == null) {
				return null;
			}
			DataInputStream in;
			try {
				in = baa.getInputStream(i);
			} catch (BAAClosedException e) {
				continue;
			} catch (IOException e) {
				return null;
			}
			return in;
		}
	}
	
	private ByteArrayArray getByteArrayArray() {
		int count = 0;
		while (true) {
			ByteArrayArray baa = baaRef.get();
			
			// If the baa exists and isn't closed return it
			if (baa != null) {
				if (baa.isClosed()) {
					baaRef.compareAndSet(baa, null);
					continue;
				} else {
					return baa;
				}
			// Some other thread is trying to open the file
			// Spinning lock, then yield and then sleep
			} else if (!baaRef.compareAndSet(null, openInProgress)) {
				count++;
				if (count > 10 ){
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {

					}
				} else if (count > 0) {
					Thread.yield();
				}
			// Successfully claimed the right to open a new file
			// Attempt to open the file.  If an IOException is throw return null
			} else {
				baa = null;
				try {
					try {
						baa = new SimpleRegionFile(file, segmentSize, entries, timeout);
					} catch (IOException e) {
						baa = null;
					}
					return baa;
				} finally {
					if (!baaRef.compareAndSet(openInProgress, baa)) {
						throw new IllegalStateException("chunkStore variable changed outside locking scheme");
					}				
				}
			}
		}
	}
	
}
