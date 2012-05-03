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
import org.spout.api.entity.BlockController;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.Material;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.source.DataSource;
import org.spout.api.material.source.MaterialData;
import org.spout.api.material.source.MaterialSource;
import org.spout.api.math.Vector3;
import org.spout.api.util.StringUtil;

public class SpoutBlock implements Block {
	private int x, y, z;
	private World world;
	private Source source;
	private Chunk chunk;

	public SpoutBlock(Block source) {
		this(source.getWorld(), source.getX(), source.getY(), source.getZ(), source.getSource());
		if (source instanceof SpoutBlock) {
			this.chunk = ((SpoutBlock) source).chunk;
		}
	}

	public SpoutBlock(Point position, Source source) {
		this(position.getWorld(), position.getBlockX(), position.getBlockY(), position.getBlockZ(), source);
	}
	
	public SpoutBlock(World world, int x, int y, int z, Source source) {
		this(world, x, y, z, null, source);
	}
	
	public SpoutBlock(World world, int x, int y, int z, Chunk chunk, Source source) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.source = source == null ? world : source;
		this.chunk = chunk != null && chunk.containsBlock(x, y, z) ? chunk : null;
	}

	@Override
	public Point getPosition() {
		return new Point(this.world, this.x + 0.5f, this.y + 0.5f, this.z + 0.5f);
	}

	@Override
	public Chunk getChunk() {
		if (this.chunk == null || !this.chunk.isLoaded()) {
			this.chunk = this.world.getChunkFromBlock(this.x, this.y, this.z, true);
		}
		return this.chunk;
	}

	@Override
	public World getWorld() {
		return this.world;
	}

	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public int getY() {
		return this.y;
	}

	@Override
	public int getZ() {
		return this.z;
	}

	@Override
	public Block setX(int x) {
		SpoutBlock sb = this.clone();
		sb.x = x;
		sb.chunk = null;
		return sb;
	}

	@Override
	public Block setY(int y) {
		SpoutBlock sb = this.clone();
		sb.y = y;
		sb.chunk = null;
		return sb;
	}

	@Override
	public Block setZ(int z) {
		SpoutBlock sb = this.clone();
		sb.z = z;
		sb.chunk = null;
		return sb;
	}

	@Override
	public Block translate(BlockFace offset) {
		return this.translate(offset.getOffset());
	}

	@Override
	public Block translate(Vector3 offset) {
		return this.translate((int) offset.getX(), (int) offset.getY(), (int) offset.getZ());
	}

	@Override
	public Block translate(int dx, int dy, int dz) {
		SpoutBlock sb = this.clone();
		sb.x += dx;
		sb.y += dy;
		sb.z += dz;
		sb.chunk = null;
		return sb;
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

	@Override
	public SpoutBlock clone() {
		return new SpoutBlock(this);
	}

	@Override
	public String toString() {
		return StringUtil.toNamedString(this, this.world, this.x, this.y, this.z);
	}

	@Override
	public SpoutBlock setMaterial(MaterialSource material, short data) {
		if (material.getMaterial() instanceof BlockMaterial) {
			this.getChunk().setBlockMaterial(this.x, y, z, (BlockMaterial) material.getMaterial(), data, this.source);
		} else {
			throw new IllegalArgumentException("Can't set a block to a non-block material!");
		}
		return this;
	}

	@Override
	public SpoutBlock setData(DataSource data) {
		return this.setData(data.getData());
	}

	@Override
	public SpoutBlock setData(short data) {
		this.getChunk().setBlockData(this.x, this.y, this.z, data, this.source);
		return this;
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
	public BlockMaterial getSubMaterial() {
		return this.getMaterial().getSubMaterial(this.getData());
	}

	@Override
	public Region getRegion() {
		return this.getChunk().getRegion();
	}

	@Override
	public BlockMaterial getMaterial() {
		return this.getChunk().getBlockMaterial(this.x, this.y, this.z);
	}

	@Override
	public Block setMaterial(MaterialSource material) {
		return this.setMaterial(material, material.getData());
	}

	@Override
	public Block setMaterial(MaterialSource material, DataSource data) {
		return this.setMaterial(material, data.getData());
	}

	@Override
	public byte getLight() {
		return this.getChunk().getBlockLight(this.x, this.y, this.z);
	}

	@Override
	public Block setLight(byte level) {
		this.getChunk().setBlockLight(this.x, this.y, this.z, level, this.source);
		return this;
	}

	@Override
	public byte getSkyLight() {
		return this.getChunk().getBlockSkyLight(this.x, this.y, this.z);
	}

	@Override
	public Block setSkyLight(byte level) {
		this.getChunk().setBlockSkyLight(this.x, this.y, this.z, level, this.source);
		return this;
	}

	@Override
	public BlockController getController() {
		return getRegion().getBlockController(x, y, z);
	}

	@Override
	public Block setController(BlockController controller) {
		getRegion().setBlockController(x, y, z, controller);
		return this;
	}

	@Override
	public boolean hasController() {
		return getController() != null;
	}

	@Override
	public Block update() {
		return this.update(true);
	}

	@Override
	public Block update(boolean around) {
		Chunk chunk = this.getChunk();
		chunk.updateBlockPhysics(this.x, this.y, this.z);
		if (around) {
			//South and North
			chunk.updateBlockPhysics(this.x + 1, this.y, this.z);
			chunk.updateBlockPhysics(this.x - 1, this.y, this.z);

			//West and East
			chunk.updateBlockPhysics(this.x, this.y, this.z + 1);
			chunk.updateBlockPhysics(this.x, this.y, this.z - 1);

			//Above and Below
			chunk.updateBlockPhysics(this.x, this.y + 1, this.z);
			chunk.updateBlockPhysics(this.x, this.y - 1, this.z);
		}
		return this;
	}
}
