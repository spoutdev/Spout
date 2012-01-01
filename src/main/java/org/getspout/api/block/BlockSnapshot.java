package org.getspout.api.block;

import java.util.Collections;
import java.util.Map;

import org.getspout.api.geo.cuboid.Block;
import org.getspout.api.io.nbt.Tag;
import org.getspout.api.material.Material;

/**
 * Represents an immutable snapshot of the state of a block
 */
public class BlockSnapshot {
	
	private final Block block;
	private final Material material;
	private final Map<String, Tag> auxData;
	
	public BlockSnapshot(Block block, Material material, Map<String, Tag> auxData) {
		this.block = block;
		this.material = material;
		this.auxData = Tag.cloneMap(auxData);
	}
	
	/**
	 * Gets which block corresponding to the snapshot
	 * 
	 * @return the block
	 */
	public Block getBlock() {
		return block;
	}
	
	/**
	 * Gets the block's material at the time of the snapshot
	 * 
	 * @return the material
	 */
	public Material getMaterial() {
		return material;
	}
	
	/**
	 * Gets the auxiliary data associated with the block at the time of the snapshot
	 * 
	 * @return the auxiliary data, or null if there was no auxiliary data
	 */
	public Map<String, Tag> getAuxData() {
		if (auxData == null) {
			return null;
		} else {
			return Collections.unmodifiableMap(auxData);
		}
	}
	
}
