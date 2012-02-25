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
package org.spout.api.event.world;

import org.spout.api.event.HandlerList;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Region;

public class RegionLoadEvent extends WorldEvent{
	private static HandlerList handlers = new HandlerList();
	private final Region region;
	public RegionLoadEvent(World world, Region region) {
		super(world);
		this.region = region;
	}
	
	/**
	 * Returns the region that was loaded.
	 * 
	 * @return region
	 */
	public final Region getRegion() {
		return region;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
