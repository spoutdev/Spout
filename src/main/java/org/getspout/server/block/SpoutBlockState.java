package org.getspout.server.block;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

import org.getspout.commons.metadata.MetadataValue;
import org.getspout.commons.plugin.Plugin;
import org.getspout.server.SpoutChunk;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.SpoutPlayer;

/**
 * Represents a state a block could be in as well as any tile entities.
 */
public class SpoutBlockState implements BlockState {
	private final SpoutWorld world;
	private final SpoutChunk chunk;
	private final int x;
	private final int y;
	private final int z;
	protected int type;
	protected MaterialData data;
	protected byte light;

	public SpoutBlockState(SpoutBlock block) {
		world = block.getWorld();
		x = block.getX();
		y = block.getY();
		z = block.getZ();
		type = block.getTypeId();
		light = block.getLightLevel();
		chunk = block.getChunk();
		makeData(block.getData());
	}

	// Basic getters

	public SpoutWorld getWorld() {
		return world;
	}

	public SpoutChunk getChunk() {
		return chunk;
	}

	public SpoutBlock getBlock() {
		return world.getBlockAt(x, y, z);
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

	// Type and data

	public MaterialData getData() {
		return data;
	}

	public void setData(MaterialData data) {
		this.data = data;
	}

	final public Material getType() {
		return Material.getMaterial(type);
	}

	final public int getTypeId() {
		return type;
	}

	final public void setType(Material type) {
		setTypeId(type.getId());
	}

	final public boolean setTypeId(int type) {
		this.type = type;
		makeData((byte) 0);
		return true;
	}

	final public byte getLightLevel() {
		return light;
	}

	final public byte getRawData() {
		return getData().getData();
	}

	// Update

	public boolean update() {
		return update(false);
	}

	public boolean update(boolean force) {
		Block block = getBlock();

		if (block.getTypeId() != type) {
			if (force) {
				block.setTypeId(type);
			} else {
				return false;
			}
		}

		block.setData(getRawData());
		return true;
	}

	public void update(SpoutPlayer player) {}

	// Internal mechanisms

	private void makeData(byte data) {
		Material mat = Material.getMaterial(type);
		if (mat == null || mat.getData() == null) {
			this.data = new MaterialData(type, data);
		} else {
			this.data = mat.getNewData(data);
		}
	}

	public SpoutBlockState shallowClone() {
		return getBlock().getState();
	}

	public void destroy() {
		throw new IllegalStateException("Cannot destroy a generic BlockState");
	}
    
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        getBlock().setMetadata(metadataKey, newMetadataValue);
    }

    public List<MetadataValue> getMetadata(String metadataKey) {
        return getBlock().getMetadata(metadataKey);
    }

    public boolean hasMetadata(String metadataKey) {
        return getBlock().hasMetadata(metadataKey);
    }

    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        getBlock().removeMetadata(metadataKey, owningPlugin);
    }
}
