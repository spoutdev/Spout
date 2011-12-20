/*
 * This file is part of Bukkit (http://bukkit.org/).
 *
 * Bukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

/**
 * Represents a boat entity.
 */
public interface Boat extends Vehicle {

	/**
	 * Gets the maximum speed of a boat. The speed is unrelated to the velocity.
	 *
	 * @return The max speed.
	 */
	public double getMaxSpeed();

	/**
	 * Sets the maximum speed of a boat. Must be nonnegative. Default is 0.4D.
	 *
	 * @param speed The max speed.
	 */
	public void setMaxSpeed(double speed);

	/**
	 * Gets the deceleration rate (newSpeed = curSpeed * rate) of occupied
	 * boats. The default is 0.2.
	 *
	 * @return
	 */
	public double getOccupiedDeceleration();

	/**
	 * Sets the deceleration rate (newSpeed = curSpeed * rate) of occupied
	 * boats. Setting this to a higher value allows for quicker acceleration.
	 * The default is 0.2.
	 *
	 * @param speed deceleration rate
	 */
	public void setOccupiedDeceleration(double rate);

	/**
	 * Gets the deceleration rate (newSpeed = curSpeed * rate) of unoccupied
	 * boats. The default is -1. Values below 0 indicate that no additional
	 * deceleration is imposed.
	 *
	 * @return
	 */
	public double getUnoccupiedDeceleration();

	/**
	 * Sets the deceleration rate (newSpeed = curSpeed * rate) of unoccupied
	 * boats. Setting this to a higher value allows for quicker deceleration of
	 * boats when a player disembarks. The default is -1. Values below 0
	 * indicate that no additional deceleration is imposed.
	 *
	 * @param rate deceleration rate
	 */
	public void setUnoccupiedDeceleration(double rate);

	/**
	 * Get whether boats can work on land.
	 *
	 * @return whether boats can work on land
	 */
	public boolean getWorkOnLand();

	/**
	 * Set whether boats can work on land.
	 *
	 * @param workOnLand whether boats can work on land
	 */
	public void setWorkOnLand(boolean workOnLand);
}
