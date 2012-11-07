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
package org.spout.api.component.components;

import org.spout.api.entity.Player;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.Vector3;

/**
 * A component allowing a player to interact with a block
 */
public class HitBlockComponent extends EntityComponent {
	private Player player;
	private float range = 8f;
	
	@Override
	public void onAttached() {
		if (!(getOwner() instanceof Player)) {
			throw new IllegalStateException("May only attach this component to players!");
		}
		player = (Player) getOwner();
	}
	
	/**
	 * Return the block in front of you eyes if there
	 * is one in range.
	 * @return block
	 */
	public Block getTargetBlock() {
		/* It use a voxel fast traversal algorithm
		 * from http://www.cse.yorku.ca/~amana/research/grid.pdf
		 */
		Vector3 origin = player.getTransform().getPosition();
		Vector3 direction = player.getTransform().getTransform().forwardVector().multiply(-1);
		
		int X = origin.getFloorX();
		int Y = origin.getFloorY();
		int Z = origin.getFloorZ();
		
		int stepX = direction.getX() > 0 ? 1 : -1;
		int stepY = direction.getY() > 0 ? 1 : -1;
		int stepZ = direction.getZ() > 0 ? 1 : -1;

		float dx = direction.getX();
		float dy = direction.getY();
		float dz = direction.getZ();
		
		float tDeltaX = (dx == 0f) ? Float.MAX_VALUE : Math.abs(1f / dx);
		float tDeltaY = (dy == 0f) ? Float.MAX_VALUE : Math.abs(1f / dy);
		float tDeltaZ = (dz == 0f) ? Float.MAX_VALUE : Math.abs(1f / dz);
		
		float tMaxX = (dx == 0f) ? Float.MAX_VALUE : (Math.abs(X + (stepX > 0 ? 1 : 0) - origin.getX()) / dx);
		float tMaxY = (dy == 0f) ? Float.MAX_VALUE : (Math.abs(Y + (stepY > 0 ? 1 : 0) - origin.getY()) / dy);
		float tMaxZ = (dz == 0f) ? Float.MAX_VALUE : (Math.abs(Z + (stepZ > 0 ? 1 : 0) - origin.getZ()) / dz);
		
		Block block = null;
		BlockFace face = null;
		while (Math.min(Math.min(tMaxX, tMaxY), tMaxZ) <= range) {
			if (tMaxX < tMaxY) {
				if (tMaxX < tMaxZ) {
					X += stepX;
					tMaxX += tDeltaX;
					face = stepX > 0 ? BlockFace.EAST : BlockFace.WEST;
				} else {
					Z += stepZ;
					tMaxZ += tDeltaZ;
					face = stepZ > 0 ? BlockFace.NORTH : BlockFace.SOUTH;
				}
			} else {
				if (tMaxY < tMaxZ) {
					Y += stepY;
					tMaxY += tDeltaY;
					face = stepY > 0 ? BlockFace.TOP : BlockFace.BOTTOM;
				} else {
					Z += stepZ;
					tMaxZ += tDeltaZ;
					face = stepZ > 0 ? BlockFace.NORTH : BlockFace.SOUTH;
				}
			}
			
			block = player.getWorld().getBlock(X, Y, Z);
			if (block.getMaterial().isPlacementObstacle()) {
				break;
			}
		}
		
		if (face != null) {
			System.out.println("Hited face: " + face.toString());
		}
		
		return block;
	}
	
	/**
	 * The max distance value you want the targeted block to be.
	 * @param range
	 */
	public void setRange(float range) {
		this.range = range;
	}
	
	public float getRange() {
		return range;
	}
}
