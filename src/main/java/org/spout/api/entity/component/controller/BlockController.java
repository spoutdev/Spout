/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
package org.spout.api.entity.component.controller;

import org.spout.api.entity.component.Controller;
import org.spout.api.entity.component.controller.type.ControllerType;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.BlockMaterial;

/**
 * Represents a controller represented by a block.
 */
public abstract class BlockController extends Controller {
	protected final BlockMaterial blockMaterial;

	public BlockController(ControllerType type, BlockMaterial blockMaterial) {
		super(type);
		this.blockMaterial = blockMaterial;
	}

	/**
	 * Returns the BlockMaterial associated with the controller
	 *
	 * @return block material
	 */
	public BlockMaterial getMaterial() {
		return blockMaterial;
	}

	/**
	 * Gets the block of this block controller
	 * @return the block
	 */
	public Block getBlock() {
		return this.getParent().getWorld().getBlock(this.getParent().getPosition());
	}
}
