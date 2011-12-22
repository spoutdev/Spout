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
package org.getspout.unchecked.api.event.player;

import org.getspout.unchecked.api.event.Cancellable;
import org.getspout.unchecked.api.event.HandlerList;
import org.getspout.unchecked.api.gui.ScreenType;
import org.getspout.unchecked.api.keyboard.Keyboard;

public class PlayerKeyEvent extends PlayerEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();

	private Keyboard key;

	private boolean pressed;

	private ScreenType screenType;

	public Keyboard getKey() {
		return key;
	}

	public void setKey(Keyboard key) {
		this.key = key;
	}

	public ScreenType getScreenType() {
		return screenType;
	}

	public void setScreenType(ScreenType screenType) {
		this.screenType = screenType;
	}

	public boolean isPressed() {
		return pressed;
	}

	public boolean isReleased() {
		return !pressed;
	}

	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}