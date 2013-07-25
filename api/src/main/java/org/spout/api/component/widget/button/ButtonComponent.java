/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.component.widget.button;

import java.awt.Color;
import java.util.List;

import org.spout.api.component.widget.ControlComponent;
import org.spout.api.component.widget.LabelComponent;
import org.spout.api.event.player.input.PlayerClickEvent;
import org.spout.api.event.player.input.PlayerKeyEvent;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.gui.render.RenderPartPack;
import org.spout.api.input.Keyboard;
import org.spout.api.math.Rectangle;
import org.spout.api.render.SpoutRenderMaterials;
import org.spout.api.signal.Signal;

/**
 * Represents a button.
 */
public class ButtonComponent extends LabelComponent {
	public static final Signal SIGNAL_CLICKED = new Signal("clicked");
	private boolean down = false;

	public ButtonComponent() {
		registerSignal(SIGNAL_CLICKED);
	}

	@Override
	public void onAttached() {
		getOwner().add(ControlComponent.class);
	}

	@Override
	public List<RenderPartPack> getRenderPartPacks() {
		List<RenderPartPack> ret = super.getRenderPartPacks();

		RenderPartPack bgPack = new RenderPartPack(SpoutRenderMaterials.GUI_COLOR);

		RenderPart part = new RenderPart();
		part.setSource(new Rectangle(0, 0, 0.1f, 0.1f));
		part.setSprite(new Rectangle(0, 0, 1f, 1f));
		part.setColor(isDown() ? Color.blue : Color.red);
		part.setZIndex(5);
		bgPack.add(part);

		ret.add(bgPack);
		return ret;
	}

	@Override
	public void onClick(PlayerClickEvent event) {
		if (event.isPressed()) {
			down = true;
		} else {
			// TODO detect if mouse button was released outside of the button
			emit(SIGNAL_CLICKED);
			down = false;
		}
	}

	@Override
	public void onKey(PlayerKeyEvent event) {
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
