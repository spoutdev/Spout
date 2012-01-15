package org.spout.server;

import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Blockm;
import org.spout.api.material.BlockMaterial;

public class SpoutBlockm extends Blockm{
	BlockMaterial material = null;

	public SpoutBlockm(World world, int x, int y, int z) {
		super(world, x, y, z);
	}
	
	public SpoutBlockm(World world, int x, int y, int z, short id) {
		this(world, x, y, z);
		setBlockId(id);
	}

	public SpoutBlockm(World world, int x, int y, int z, BlockMaterial material) {
		this(world, x, y, z);
		setBlockMaterial(material);
	}
	
	@Override
	public BlockMaterial setBlockMaterial(BlockMaterial material) {
		throw new UnsupportedOperationException("Operation is not supported");
	}

	@Override
	public short setBlockId(short id) {
		throw new UnsupportedOperationException("Operation is not supported");
	}

	@Override
	public BlockMaterial getBlockMaterial() {
		throw new UnsupportedOperationException("Operation is not supported");
	}

	@Override
	public short getBlockId() {
		throw new UnsupportedOperationException("Operation is not supported");
	}

	@Override
	public BlockMaterial getLiveBlockMaterial() {
		throw new UnsupportedOperationException("Operation is not supported");
	}

	@Override
	public short getLiveBlockId() {
		throw new UnsupportedOperationException("Operation is not supported");
	}
}
