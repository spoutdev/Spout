package org.getspout.api.util.map;

/**
 * A simplistic map that supports (byte, short, byte) keys, using a trove int *
 * hashmap in the backend.
 *
 */

public class TByteShortByteKeyedMap {

	public static final int key(int x, int y, int z) {
		return (x & 0xFF) << 24 | (z & 0xFF) << 16 | y & 0xFFFF;
	}

	public static byte getXFromKey(int key) {
		return (byte) (key >> 24);
	}

	public static short getYFromKey(int key) {
		return (short) key;
	}

	public static byte getZFromKey(int key) {
		return (byte) (key >> 16);
	}

}
