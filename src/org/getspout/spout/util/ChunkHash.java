package org.getspout.spout.util;

public class ChunkHash {
	
	public static long hash(byte[] a) {
		long h = 1;
		for(byte b : a) {
			h += (h<<5) + (long)b;
		}
		return h;
	}

}