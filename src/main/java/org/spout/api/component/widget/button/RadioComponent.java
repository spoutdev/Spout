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

import org.spout.api.event.player.input.PlayerClickEvent;
import org.spout.api.gui.Widget;
import org.spout.api.map.DefaultedKey;
import org.spout.api.map.DefaultedKeyImpl;
import org.spout.api.signal.Signal;

/**
 * Represents a radio button.
 */
public class RadioComponent extends ButtonComponent {
	public static final Signal SIGNAL_SELECTED = new Signal("selected", Boolean.class);
	private static final DefaultedKey<Boolean> KEY_SELECTED = new DefaultedKeyImpl<Boolean>("selected", false);

	public RadioComponent() {
		super();
		registerSignal(SIGNAL_SELECTED);
		try {
			subscribe(SIGNAL_CLICKED, this, "onClick");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns true if this radio is selected.
	 * @return true if this is selected.
	 */
	public boolean isSelected() {
		return getDatatable().get(KEY_SELECTED);
	}

	/**
	 * Sets if this button is selected and deselects the other radio buttons
	 * on the screen.
	 * @param selected true if select
	 */
	public void setSelected(boolean selected) {
		getDatatable().put(KEY_SELECTED, selected);
		emit(SIGNAL_SELECTED, selected);
		if (selected) {
			for (Widget widget : getOwner().getScreen().getWidgets()) {
				RadioComponent other = widget.getExact(RadioComponent.class);
				if (other != null) {
					other.setSelected(false);
				}
			}
		}
	}

	@Override
	public void onClick(PlayerClickEvent event) {
		setSelected(!event.isPressed() && !isSelected());
	}
}
