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
package org.getspout.api.event.block;

import java.util.List;
import org.getspout.api.event.Cancellable;
import org.getspout.api.event.HandlerList;
import org.getspout.api.inventory.ItemStack;

/**
 * Called when a block is broken.
 */
public class BlockBreakEvent extends BlockEvent implements Cancellable {
	private static HandlerList handlers;

	private BreakCause cause;

	public BreakCause getCause() {
		return cause;
	}

	public void setCause(BreakCause cause) {
		this.cause = cause;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public enum BreakCause {
		/**
		 * Explosion breaks a block.
		 */
		EXPLOSION,
		/**
		 * Player breaks a block.
		 */
		PLAYER,
		/**
		 * Likely a fake event through the API.
		 */
		OTHER;

	}

}
