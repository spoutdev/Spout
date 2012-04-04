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
package org.spout.engine.world;

import org.spout.api.Source;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.source.DataSource;
import org.spout.api.material.source.MaterialData;
import org.spout.api.material.source.MaterialSource;
import org.spout.api.math.Vector3;

public class SpoutBlock implements Block {

	protected int x, y, z;
	private World world;
	private Chunk chunk;
	private Source source;

	public SpoutBlock(Block source) {
		this(source.getWorld(), source.getX(), source.getY(), source.getZ(), source.getSource());
	}

	public SpoutBlock(Point position, Source source) {
		this(position.getWorld(), position.getBlockX(), position.getBlockY(), position.getBlockZ(), source);
	}

	public SpoutBlock(World world, int x, int y, int z, Source source) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.source = source == null ? world : source;
	}

	public Point getPosition() {
		return new Point(this.world, this.x, this.y, this.z);
	}

	public Chunk getChunk() {
		if (this.chunk == null || !this.chunk.isLoaded()) {
			recalculateChunk();
		}
		return this.chunk;
	}

	public World getWorld() {
		return this.chunk.getWorld();
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public Block setX(int x) {
		this.x = x;
		recalculateChunk();
		return this;
	}

	public Block setY(int y) {
		this.y = y;
		recalculateChunk();
		return this;
	}

	public Block setZ(int z) {
		this.z = z;
		recalculateChunk();
		return this;
	}

	public Block move(BlockFace offset) {
		return this.move(offset.getOffset());
	}

	public Block move(Vector3 offset) {
		return this.move((int) offset.getX(), (int) offset.getY(), (int) offset.getZ());
	}

	public Block move(int dx, int dy, int dz) {
		this.x += dx;
		this.y += dy;
		this.z += dz;
		recalculateChunk();
		return this;
	}

	@Override
	public void setMaterial(MaterialSource material) {
		this.setMaterial(material, true);
	}

	/**
	 * Sets the material
	 * @param material to set to
	 * @param update whether players nearby should be notified of the block change
	 */
	public void setMaterial(MaterialSource material, boolean update) {
		this.setMaterial(material, (short) 0, update);
	}

	public BlockMaterial getMaterial() {
		return this.getChunk().getBlockMaterial(this.x, this.y, this.z);
	}

	@Override
	public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.world.hashCode();
        hash = 53 * hash + (this.x ^ (this.x >> 16));
        hash = 53 * hash + (this.y ^ (this.y >> 16));
        hash = 53 * hash + (this.z ^ (this.z >> 16));
        return hash;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		} else if (other != null && other instanceof Block) {
			Block b = (Block) other;
			return b.getWorld() == this.getWorld() && b.getX() == this.getX() && b.getY() == this.getY() && b.getZ() == this.getZ();
		} else {
			return false;
		}
	}
	
	public SpoutBlock clone() {
		return new SpoutBlock(this);
	}

	@Override
	public void setMaterial(MaterialSource material, DataSource datasource) {
		this.setMaterial(material, datasource, true);
	}

	/**
	 * Sets the material and data
	 * @param material to set to
	 * @param datasource of the data to set to
	 * @param update whether players nearby should be notified of the block change
	 */
	public void setMaterial(MaterialSource material, DataSource datasource, boolean update) {
		this.setMaterial(material, datasource.getData(), update);
	}

	@Override
	public void setMaterial(MaterialSource material, short data) {
		this.setMaterial(material, data, true);
	}

	/**
	 * Sets the material and data
	 * @param material to set to
	 * @param data value to set to
	 * @param update whether players nearby should be notified of the block change
	 */
	public void setMaterial(MaterialSource material, short data, boolean update) {
		if (material.getMaterial() instanceof BlockMaterial) {
			this.getChunk().setBlockMaterial(this.x, y, z, (BlockMaterial) material.getMaterial(), data, update, this.source);
		} else {
			throw new IllegalArgumentException("Can't set a block to a non-block material!");
		}
	}

	@Override
	public void setData(DataSource datasource) {
		this.setData(datasource, true);
	}

	/**
	 * Sets the data
	 * @param datasource of the data to set to
	 * @param update whether players nearby should be notified of the block change
	 */
	public void setData(DataSource datasource, boolean update) {
		this.setData(datasource.getData(), update);
	}

	@Override
	public void setData(short data) {
		this.setData(data);
	}

	/**
	 * Sets the data
	 * @param data value to set to
	 * @param update whether players nearby should be notified of the block change
	 */
	public void setData(short data, boolean update) {
		this.getChunk().setBlockData(this.x, this.y, this.z, data, update, this.source);
	}

	@Override
	public short getData() {
		return this.getChunk().getBlockData(this.x, this.y, this.z);
	}

	@Override
	public MaterialData createData() {
		return this.getMaterial().createData(this.getData());
	}

	@Override
	public Source getSource() {
		return this.source;
	}

	@Override
	public void setSource(Source source) {
		this.source = source;
	}

	@Override
	public void setBlock(MaterialSource block) {
		this.setBlock(block, true);
	}

	@Override
	public void setBlock(MaterialSource blocksource, boolean update) {
		this.setMaterial(blocksource.getMaterial(), blocksource.getData(), update);
	}
	
	private void recalculateChunk() {
		int cx = this.x >> Chunk.CHUNK_SIZE_BITS;
		int cy = this.y >> Chunk.CHUNK_SIZE_BITS;
		int cz = this.z >> Chunk.CHUNK_SIZE_BITS;
		this.chunk = this.world.getChunk(cx, cy, cz, true);
	}
}
