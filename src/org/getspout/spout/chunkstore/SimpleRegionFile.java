package org.getspout.spout.chunkstore;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class SimpleRegionFile {
	
	private final RandomAccessFile file;
	private final int[] dataStart = new int[1024];
	private final int[] dataActualLength = new int[1024];
	private final int[] dataLength = new int[1024];
	private final ArrayList<Boolean> inuse = new ArrayList<Boolean>();
	private final int segmentSize;
	private final int segmentMask;
	private final int rx;
	private final int rz;
	
	public SimpleRegionFile(File f, int rx, int rz) {
		this(f, rx, rz, 10);
	}
	
	public SimpleRegionFile(File f, int rx, int rz, int defaultSegmentSize) {
		
		this.rx = rx;
		this.rz = rz;
		
		try {
			this.file = new RandomAccessFile(f, "rw");
			
			if (file.length() < 4096*3) {
				
				for (int i = 0; i < 1024*3; i++) {
					file.writeInt(0);
				}
				file.seek(4096 * 2);
				file.writeInt(defaultSegmentSize);
			}

			file.seek(4096 * 2);

			this.segmentSize = file.readInt();
			this.segmentMask = (1 << segmentSize) - 1;
			
			int reservedSegments = this.sizeToSegments(4096 * 3);
			
			for (int i = 0; i < reservedSegments; i++) {
				while (inuse.size() <= i) {
					inuse.add(false);
				}
				inuse.set(i, true);
			}
			
			file.seek(0);
			
			for (int i = 0; i < 1024; i++) {
				dataStart[i] = file.readInt();
			}
			
			for (int i = 0; i < 1024; i++) {
				dataActualLength[i] = file.readInt();
				dataLength[i] = sizeToSegments(dataActualLength[i]);
				setInUse(i, true);
			}
			
			extendFile();
			
		} catch (IOException fnfe) {
			throw new RuntimeException(fnfe);
		} 
		
	}
	
	public DataOutputStream getOutputStream(int x, int z) {
		int index = getChunkIndex(x, z);
		return new DataOutputStream(new DeflaterOutputStream(new SimpleChunkBuffer(this, index)));
	}
	
	public DataInputStream getInputStream(int x, int z) throws IOException {
		int index = getChunkIndex(x, z);
		int actualLength = dataActualLength[index];
		if (actualLength == 0) {
			return null;
		}
		byte[] data = new byte[actualLength];

		file.seek(dataStart[index] << segmentSize);
		file.readFully(data);
		return new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(data)));
	}
	
	void write(int index, byte[] buffer, int size) throws IOException {

		int oldStart = setInUse(index, false);
		int start = findSpace(oldStart, size);
		file.seek(start << segmentSize);
		file.write(buffer, 0, size);
		dataStart[index] = start;
		dataActualLength[index] = size;
		dataLength[index] = sizeToSegments(size);
		setInUse(index, true);
		saveFAT();
	}
	
	public void close() {
		try {
			file.seek(4096*2);
			file.close();
		} catch (IOException ioe) {
			throw new RuntimeException("Unable to close file", ioe);
		}
	}
	
	private int setInUse(int index, boolean used) {
		
		if (dataActualLength[index] == 0) {
			return dataStart[index];
		}

		int start = dataStart[index];
		int end = start + dataLength[index];
		
		for(int i = start; i < end; i++) {
			while(i > inuse.size() - 1) {
				inuse.add(false);
			}
			Boolean old = inuse.set(i, used);
			if (old != null && old == used) {
				if (old) {
					throw new IllegalStateException("Attempting to overwrite an in-use segment");
				} else {
					throw new IllegalStateException("Attempting to delete empty segment");
				}
			}
		}
		
		return dataStart[index];
	}
	
	private void extendFile() throws IOException {
		
		long extend = (-file.length()) & segmentMask;
		
		file.seek(file.length());
		
		while ((extend--) > 0) {
			file.write(0);
		}
		
	}
	
	private int findSpace(int oldStart, int size) {
		
		int segments = sizeToSegments(size);
		
		boolean oldFree = true;
		for (int i = oldStart; i < inuse.size() && i < oldStart + segments; i++) {
			if (inuse.get(i)) {
				oldFree = false;
				break;
			}
		}
		
		if (oldFree) {
			return oldStart;
		}
		
		int start = 0;
		int end = 0;
		
		while (end < inuse.size()) {
			if (inuse.get(end)) {
				end++;
				start = end;
			} else {
				end++;
			}
			if (end - start >= segments) {
				return start;
			}
		}
		
		return start;
	}
	
	private int sizeToSegments(int size) {
		if (size <= 0) {
			return 1;
		} else {
			return ((size - 1) >> segmentSize) + 1;
		}
	}
	
	private Integer getChunkIndex(int x, int z) {
		
		if (rx != (x >> 5) || rz != (z >> 5)) {
			throw new RuntimeException(x + ", " + z + " not in region " + rx + ", " + rz);
		}
		
		x = x & 0x1F;
		z = z & 0x1F;
		
		return (x << 5) + z;
	}
	
	private void saveFAT() throws IOException {
		file.seek(0);
		for (int i = 0; i < 1024; i++) {
			file.writeInt(dataStart[i]);
		}
		
		for (int i = 0; i < 1024; i++) {
			file.writeInt(dataActualLength[i]);
		}
		
	}
	
}
