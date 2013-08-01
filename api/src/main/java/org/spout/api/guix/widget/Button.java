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
package org.spout.api.guix.widget;

import org.spout.api.Spout;
import org.spout.api.event.player.input.PlayerClickEvent;
import org.spout.api.event.widget.WidgetClickEvent;
import org.spout.api.event.widget.button.ButtonPressEvent;
import org.spout.api.event.widget.button.ButtonReleaseEvent;
import org.spout.api.guix.Widget;

/**
 * Represents any {@link Widget} that can be pressed and released.
 */
public class Button extends Widget {
	private boolean pressed;

	@Override
	public final void onClick(WidgetClickEvent event) {
		PlayerClickEvent cevent = event.getClickEvent();
		if (cevent.isPressed()) {
			ButtonPressEvent pevent = Spout.getEventManager().callEvent(new ButtonPressEvent(this));
			if (!pevent.isCancelled()) {
				pressed = true;
				onPress();
			}
		} else if (pressed) {
			ButtonReleaseEvent revent = Spout.getEventManager().callEvent(new ButtonReleaseEvent(this));
			if (!revent.isCancelled()) {
				pressed = false;
				onRelease();
			}
		}
	}

	public void onPress() {
	}

	public void onRelease() {
	}
}
