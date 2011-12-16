/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 * 
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.util;

import org.getspout.api.World;
import org.getspout.api.block.Block;

public interface FixedLocation extends FixedVector {

	/**
	 * Gets the yaw of this location
	 * 
	 * @return Yaw
	 */
	public double getYaw();

	/**
	 * Gets the pitch of this location
	 * 
	 * @return Pitch
	 */
	public double getPitch();

	/**
	 * Gets the world that this location resides in
	 * 
	 * @return World that contains this location
	 */
	public World getWorld();

	/**
	 * Gets a Vector pointing in the direction that this Location is facing
	 * 
	 * @return Vector
	 */
	public Vector getDirection();

	/**
	 * Creates a vector with the properties of this location
	 * @return vector
	 */
	public Vector toVector();
	
	/**
	 * Gets the block at this location
	 * 
	 * @return block
	 */
	public Block getBlock();
}
