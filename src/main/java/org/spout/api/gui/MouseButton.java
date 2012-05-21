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
package org.spout.api.gui;

import java.util.HashMap;

public class MouseButton {
	private int buttonId;
	private String buttonName;
	private static HashMap<Integer, MouseButton> buttonsById;

	public static final MouseButton LEFT_BUTTON = new MouseButton(0, "Left");
	public static final MouseButton RIGHT_BUTTON = new MouseButton(1, "Right");
	public static final MouseButton MIDDLE_BUTTON = new MouseButton(2, "Middle");

	public static MouseButton getButtonById(int id) {
		MouseButton button = buttonsById.get(id);
		if (button == null) {
			button = new MouseButton(id, "Unknown "+id);
			buttonsById.put(id, button);
		}
		return button;
	}

	public MouseButton(int buttonId, String buttonName) {
		this.buttonId = buttonId;
		this.buttonName = buttonName;
		buttonsById.put(buttonId, this);
	}

	public int getButtonId() {
		return buttonId;
	}
	public String getButtonName() {
		return buttonName;
	}
}
