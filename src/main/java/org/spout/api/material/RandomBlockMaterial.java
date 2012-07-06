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
package org.spout.api.material;

import org.spout.api.geo.World;

/**
 * Represents that this block material should receive random tick updates.
 */
public interface RandomBlockMaterial {
	/**
	 * Called when a random tick update occurs to this material. 
	 * <br/><br/>
	 * Random tick updates are infrequent, and random updates that occur 
	 * to any block whose material supports them. They should be used
	 * when calculated dynamic updates would be too frequent and harm performance
	 * and cause excessive memory usage, or when updates should not be 
	 * saved to be resumed.
	 * <br/><br/>
	 * <b>Note:</b> Random tick updates may be delayed if the engine falls behind,
	 * materials should not rely on tick updates happening regularly.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	public void onRandomTick(World world, int x, int y, int z);
}
