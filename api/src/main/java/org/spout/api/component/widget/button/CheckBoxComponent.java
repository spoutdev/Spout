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
package org.spout.api.component.widget.button;

import org.spout.api.map.DefaultedKey;
import org.spout.api.map.DefaultedKeyImpl;
import org.spout.api.signal.Signal;

public class CheckBoxComponent extends ButtonComponent {
	public static final Signal SIGNAL_CHECKED = new Signal("checked", Boolean.class);
	private static final DefaultedKey<Boolean> KEY_CHECKED = new DefaultedKeyImpl<>("checked", false);

	public CheckBoxComponent() {
		super();
		registerSignal(SIGNAL_CHECKED);
		try {
			subscribe(SIGNAL_CLICKED, this, "onClick");
		} catch (SecurityException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public void onClicked() {
		setChecked(!isChecked());
		getOwner().update();
	}

	public boolean isChecked() {
		return getData().get(KEY_CHECKED);
	}

	public void setChecked(boolean checked) {
		getData().put(KEY_CHECKED, checked);
		emit(SIGNAL_CHECKED, checked);
	}
}
