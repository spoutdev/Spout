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
package org.getspout.api.entity;

import org.getspout.api.math.Vector3;


/**
 * Represents a vehicle entity.
 * 
 * @author sk89q
 */
public interface Vehicle extends Entity {

	/**
	 * Gets the vehicle's velocity.
	 * 
	 * @return velocity vector
	 */
	public Vector3 getVelocity();

	/**
	 * Sets the vehicle's velocity.
	 * 
	 * @param vel velocity vector
	 */
	public void setVelocity(Vector3 vel);
}
