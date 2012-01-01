package org.getspout.api.material.block;

import org.getspout.api.math.Vector3;

/**
 * Indicates the face of a Block
 */
public enum BlockFace {
	TOP(0, 1, 0),
	BOTTOM(0, -1, 0),
	NORTH(-1, 0, 0),
	SOUTH(1, 0, 0),
	EAST(0, 0, -1),
	WEST(0, 0, 1),
	THIS(0, 0, 0);
	
	private Vector3 offset;
	
	private BlockFace(int dx, int dy, int dz) {
		offset = new Vector3(dx, dy, dz);
	}
	
	public Vector3 getOffset() {
		return offset;
	}
	
}
