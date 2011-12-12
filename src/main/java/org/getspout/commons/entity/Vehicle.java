/*
 * This file is part of SpoutcraftAPI (http://wiki.getspout.org/).
 * 
 * SpoutcraftAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutcraftAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.commons.entity;

import org.getspout.commons.util.Vector;

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
	public Vector getVelocity();

	/**
	 * Sets the vehicle's velocity.
	 * 
	 * @param vel velocity vector
	 */
	public void setVelocity(Vector vel);
}
