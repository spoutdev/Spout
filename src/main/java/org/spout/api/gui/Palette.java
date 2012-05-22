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

import java.awt.Color;
import java.util.HashMap;



/**
 * Defines a color palette for widgets to draw. Each widget can have its own palette, 
 * but by default each one has the same instance so the colors are consistent.
 */
public class Palette {
	private HashMap<Integer, Color> colors = new HashMap<Integer, Color>();
	
	public void setColor(WidgetState state, ColorType type, Color color) {
		colors.put(getHash(state, type), color);
	}
	
	public Color getColor(WidgetState state, ColorType type) {
		return colors.get(getHash(state, type));
	}
	
	private int getHash(WidgetState state, ColorType type) {
		return (state.getId() << 4) | type.getId();
	}
	
	public enum ColorType {
		BORDER(0),
		BACKGROUND(1),
		TEXT(2),
		
		;
		private final int num;
		private ColorType(int num) {
			this.num = num;
		}
		
		public int getId() {
			return num;
		}
	}
}
