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

import org.spout.api.gui.Widget;
import org.spout.api.map.DefaultedKey;
import org.spout.api.signal.Signal;

public class RadioComponent extends ButtonComponent {
	public static final Signal SIGNAL_SELECTED = new Signal("selected", Boolean.class);
	
	private static final DefaultedKey<Boolean> KEY_SELECTED = new DefaultedKey<Boolean>() {
		@Override
		public Boolean getDefaultValue() {
			return false;
		}
		
		@Override
		public String getKeyString() {
			return "selected";
		}
	};
	
	public RadioComponent() {
		super();
		registerSignal(SIGNAL_SELECTED);
		try {
			subscribe(SIGNAL_CLICKED, this, "onClicked");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isSelected() {
		return getData().get(KEY_SELECTED);
	}
	
	public void setSelected(boolean selected) {
		getData().put(KEY_SELECTED, selected);
		emit(SIGNAL_SELECTED, selected);
		if (selected) {
			for (Widget widget:getOwner().getContainer().getWidgets()) {
				if (widget.hasExact(RadioComponent.class)) {
					RadioComponent other = widget.get(RadioComponent.class);
					other.setSelected(false);
				}
			}
		}
	}
	
	public void onClicked() {
		boolean selected = isSelected();
		if (!selected) {
			setSelected(true);
		}
	}
}
