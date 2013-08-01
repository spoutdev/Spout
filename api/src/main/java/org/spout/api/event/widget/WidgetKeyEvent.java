/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.event.widget;

import org.spout.api.event.Cancellable;
import org.spout.api.event.HandlerList;
import org.spout.api.event.player.input.PlayerKeyEvent;
import org.spout.api.guix.Widget;

/**
 * Called when a key is pressed when a {@link Widget} has focus. Only the
 * focused widget on the input screen will receive key input.
 */
public class WidgetKeyEvent extends WidgetEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private final PlayerKeyEvent event;

	public WidgetKeyEvent(Widget widget, PlayerKeyEvent event) {
		super(widget);
		this.event = event;
	}

	/**
	 * Returns the already-called {@link PlayerKeyEvent} that caused this
	 * event.
	 *
	 * @return key event
	 */
	public PlayerKeyEvent getKeyEvent() {
		return event;
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
