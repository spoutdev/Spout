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
package org.spout.api.gui.attribute;

import java.util.HashMap;
import java.util.Set;

public class AttributeUnit {

	public static AttributeUnit PIXELS = new AttributeUnit(0, "px", "pxs", "pixels", "pixel");
	public static AttributeUnit CENTIMETERS = new AttributeUnit(1, "cm", "centimeters");
	public static AttributeUnit PERCENT = new AttributeUnit(2, "%", "percent");
	public static AttributeUnit NONE = new AttributeUnit(3, "", "none"); //can be used for complex attributes like colors

	private int id;
	private String[] text;
	
	private static HashMap<Integer, AttributeUnit> idStore = new HashMap<Integer, AttributeUnit>();
	private static HashMap<String, AttributeUnit> textStore = new HashMap<String, AttributeUnit>();
	
	public static Set<String> getAllTexts() {
		return textStore.keySet();
	}
	
	public static AttributeUnit getByText(String text) {
		return textStore.get(text);
	}
	
	public static AttributeUnit getById(int id) {
		return idStore.get(id);
	}
	
	public AttributeUnit(int id, String ...text) {
		this.id = id;
		this.text = text;
		idStore.put(id, this);
		for(String txt:text) {
			textStore.put(txt, this);
		}
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String[] getText() {
		return text;
	}
	
	public void setText(String[] text) {
		this.text = text;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof AttributeUnit) {
			return id == ((AttributeUnit) other).id;
		}
		return false;
	}
}
