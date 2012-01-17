/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.api.event.player;

import org.spout.api.event.Cancellable;
import org.spout.api.event.HandlerList;
import org.spout.api.geo.discrete.Point;
import org.spout.api.inventory.ItemStack;
import org.spout.api.player.Player;

/**
 *
 * @author zml2008
 */
public class PlayerInteractEvent extends PlayerEvent implements Cancellable {
	private final Point interactedPoint;
	private final ItemStack heldItem;
	private final Action action;
	private final boolean isAir;

	public PlayerInteractEvent(Player p, Point interactedPoint, ItemStack heldItem, Action action, boolean isAir) {
		super(p);
		this.interactedPoint = interactedPoint;
		this.heldItem = heldItem;
		this.action = action;
		this.isAir = isAir;
	}

	public ItemStack getHeldItem() {
		return heldItem;
	}

	public Point getInteractedPoint() {
		return interactedPoint;
	}

	public Action getAction() {
		return action;
	}

	public boolean isAir() {
		return isAir;
	}

	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}

	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public static enum Action {
		LEFT_CLICK,
		RIGHT_CLICK,
		COLLISION
	}
}
