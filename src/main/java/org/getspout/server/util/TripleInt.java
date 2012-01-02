package org.getspout.server.util;

/**
 * Stores a 3 int triple.
 * 
 * Objects of this type can be used in HashMaps.
 * 
 * TripleInt.NULL can be added to HashMaps that don't support null objects/
 */
public class TripleInt {
	
	public final static TripleInt NULL = new TripleInt(0, 0, 0);

	public final int x;
	public final int y;
	public final int z;
	
	public TripleInt(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	@Override
	public int hashCode() {
		int hash = x;
		hash += (hash << 11) + y;
		hash += (hash << 9) + z;
		return hash;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof TripleInt)) {
			return false;
		} else {
			TripleInt other = (TripleInt)o;
			
			if (other == NULL) {
				return this == NULL;
			} else {
				return other.x == x && other.y == y && other.z == z;
			}
		}
	}
	
}
