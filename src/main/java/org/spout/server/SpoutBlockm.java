/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
	
	//TODO: implement all of these
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
