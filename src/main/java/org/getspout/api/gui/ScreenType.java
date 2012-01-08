/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.getspout.api.gui;

import java.util.HashMap;
import java.util.Map;

/**
 * The various Screens that Spout knows about.
 */
public enum ScreenType {
	GAME_SCREEN(0),
	CHAT_SCREEN(1),
	CUSTOM_SCREEN(2),
	PLAYER_INVENTORY(3),
	CHEST_INVENTORY(4),
	DISPENSER_INVENTORY(5),
	FURNACE_INVENTORY(6),
	INGAME_MENU(7),
	OPTIONS_MENU(8),
	VIDEO_SETTINGS_MENU(9),
	CONTROLS_MENU(10),
	ACHIEVEMENTS_SCREEN(11),
	STATISTICS_SCREEN(12),
	WORKBENCH_INVENTORY(13),
	SIGN_SCREEN(14),
	GAME_OVER_SCREEN(15),
	SLEEP_SCREEN(16),
	UNKNOWN(-1);

	private final int code;
	private static Map<Integer, ScreenType> lookup = new HashMap<Integer, ScreenType>();

	private ScreenType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static ScreenType getType(int code) {
		return lookup.get(code);
	}

	static {
		for (ScreenType type : values()) {
			lookup.put(type.code, type);
		}
	}
}
