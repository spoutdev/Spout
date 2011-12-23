/*
 * This file is part of SpoutAPI (http://getspout.org/).
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
package org.getspout.api.plugin.security;

public interface Secure {
	
	/**
	 * Returns true if it is locked
	 * 
	 * @return locked
	 */
	public boolean isLocked();

	/**
	 * Locks when given the correct key.
	 * Returns true if it was previously locked
	 * 
	 * @param key
	 * @return true if it was locked
	 */
	public boolean lock(double key);

	/**
	 * Unlocks when given the correct key
	 * 
	 * @param key
	 */
	public void unlock(double key);

}
