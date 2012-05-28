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

public class AttributeValue {
	private Object value;
	
	public AttributeValue(Object value2) {
		set(value2);
	}

	public void set(Object val) {
		value = val;
	}
	
	public Object getValue() {
		return value;
	}
	
	public int getIntValue() {
		if(isInt()) {
			return (Integer) getValue();
		}
		if(isLong()) {
			return (int)(long)(Long) getValue();
		}
		return 0;
	}
	
	public boolean isInt() {
		return value instanceof Integer;
	}
	
	public long getLongValue() {
		if(isInt()) {
			return (long)(int)(Integer) getValue();
		}
		if(isLong()) {
			return (Long) getValue();
		}
		return (long) 0;
	}
	
	public boolean isLong() {
		return value instanceof Long;
	}
	
	public double getDoubleValue() {
		if(isDouble()) {
			return (Double) getValue();
		}
		if(isFloat()) {
			return (double)(float)(Float) getValue();
		}
		return 0d;
	}

	public boolean isDouble() {
		return value instanceof Double;
	}
	
	public float getFloatValue() {
		if(isFloat()) {
			return (Float) getValue();
		}
		if(isDouble()) {
			return (float)(double)(Double) getValue();
		}
		return 0f;
	}
	
	public boolean isFloat() {
		return value instanceof Float;
	}
}
