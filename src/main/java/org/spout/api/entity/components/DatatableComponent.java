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
package org.spout.api.entity.components;

import java.io.Serializable;

import org.spout.api.datatable.DataMap;
import org.spout.api.datatable.DatatableMap;
import org.spout.api.datatable.GenericDatatableMap;
import org.spout.api.entity.BaseComponent;
import org.spout.api.map.DefaultedKey;

public final class DatatableComponent extends BaseComponent {
	private final DatatableMap datatableMap = new GenericDatatableMap();
	private final DataMap dataMap = new DataMap(datatableMap);
	
	public DataMap getBaseMap() {
		return dataMap;
	}
	
	public void put(String key, Serializable value) {
		dataMap.put(key, value);
	}
	
	public <T extends Serializable> T put (DefaultedKey<T> key, T value) {
		return dataMap.put(key, value);
	}
	
	public Serializable get (Object key) {
		return dataMap.get(key);
	}
	
	public <T extends Serializable> T get (DefaultedKey<T> key) {
		return dataMap.get(key);
	}
	
	public <T extends Serializable> T get (Object key, T defaultValue) {
		return dataMap.get(key, defaultValue);
	}
	
	public boolean containsKey(Object key) {
		return dataMap.containsKey(key);
	}
	
	public boolean containsKey(String key) {
		return dataMap.containsKey(key);
	}
	
	public boolean containsValue(Object value) {
		return dataMap.containsValue(value);
	}
}
