package org.spout.server;

import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.MaterialData;

public class SpoutBlock extends Block {
	BlockMaterial material = null;

	public SpoutBlock(World world, int x, int y, int z) {
		super(world, x, y, z);
	}
	
	public SpoutBlock(World world, int x, int y, int z, short id) {
		this(world, x, y, z);
		setBlockId(id);
	}

	public SpoutBlock(World world, int x, int y, int z, BlockMaterial material) {
		this(world, x, y, z);
		setBlockMaterial(material);
	}
	
	@Override
	public BlockMaterial setBlockMaterial(BlockMaterial material) {
		setBlockId(material.getId());
		return material;
	}

	@Override
	public short setBlockId(short id) {
		BlockMaterial newMat = MaterialData.getBlock(id);
		if(newMat != null) {
			material = newMat;
		}
		return material != null ? material.getId() : 0;
	}

	@Override
	public BlockMaterial getBlockMaterial() {
		return material;
	}

	@Override
	public short getBlockId() {
		return material.getId();
	}

	@Override
	public BlockMaterial getLiveBlockMaterial() {
		return getBlockMaterial();
	}

	@Override
	public short getLiveBlockId() {
		return getBlockId();
	}
}
