/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
package org.spout.api.gui.component;

import java.util.List;

import org.spout.api.gui.render.RenderPart;
import org.spout.api.keyboard.KeyEvent;
import org.spout.api.keyboard.Keyboard;
import org.spout.api.math.IntVector2;
import org.spout.api.signal.Signal;

public class ButtonComponent extends LabelComponent {
	public static final Signal SIGNAL_CLICKED = new Signal("clicked");
	private boolean down = false;
	
	public ButtonComponent () {
		registerSignal(SIGNAL_CLICKED);
	}
	
	@Override
	public void onAttached() {
		getOwner().add(ControlComponent.class);
	}
	
	@Override
	public List<RenderPart> getRenderParts() {
		return super.getRenderParts(); // TODO add text, rectangle for clicked stuff
	}
	
	@Override
	public void onClicked(IntVector2 position, boolean mouseDown) {
		if (mouseDown) {
			down = true;
		} else {
			// TODO detect if mouse button was released outside of the button
			emit(SIGNAL_CLICKED);
			down = false;
		}
	}
	
	@Override
	public void onKey(KeyEvent event) {
		if (event.getKey() == Keyboard.KEY_SPACE) {
			if (event.isPressed()) {
				down = true;
				getOwner().update();
			} else {
				if (isDown()) {
					emit(SIGNAL_CLICKED);
					down = true;
					getOwner().update();
				}
			}
		}
		if (event.getKey() == Keyboard.KEY_ESCAPE) {
			down = false;
			getOwner().update();
		}
	}
	
	public boolean isDown() {
		return down;
	}
}
