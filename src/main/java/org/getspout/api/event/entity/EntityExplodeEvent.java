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
package org.getspout.api.event.entity;

import java.util.List;

import org.getspout.api.block.Block;
import org.getspout.api.event.Cancellable;
import org.getspout.api.event.HandlerList;
import org.getspout.api.util.Location;

/**
 * Called when an entity explodes.
 */
public class EntityExplodeEvent extends EntityEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();

	private List<Block> blocks;

	private Location epicenter;

	private float yield;

	private boolean incendiary;

	public List<Block> getBlocks() {
		return blocks;
	}

	public void setBlocks(List<Block> blocks) {
		this.blocks = blocks;
	}

	public Location getEpicenter() {
		return epicenter;
	}

	public void setEpicenter(Location epicenter) {
		this.epicenter = epicenter;
	}

	public boolean isIncendiary() {
		return incendiary;
	}

	public void setIncendiary(boolean incendiary) {
		this.incendiary = incendiary;
	}

	public float getYield() {
		return yield;
	}

	public void setYield(float yield) {
		this.yield = yield;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
