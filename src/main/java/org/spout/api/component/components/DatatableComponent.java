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
package org.spout.api.component.components;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.spout.api.component.Component;
import org.spout.api.datatable.ManagedHashMap;
import org.spout.api.datatable.SerializableMap;
import org.spout.api.map.DefaultedKey;

public final class DatatableComponent extends Component implements SerializableMap {
	private final ManagedHashMap dataMap;

	public DatatableComponent(byte[] data) {
		this.dataMap = new ManagedHashMap();
		if (data != null && data.length > 0) {
			try {
				this.dataMap.deserialize(data);
			} catch (IOException e) {
				throw new RuntimeException("Invalid DatatableComponent byte array", e);
			}
		}
	}

	public DatatableComponent() {
		this(null);
	}

	public ManagedHashMap getBaseMap() {
		return dataMap;
	}

	@Override
	public boolean isDetachable() {
		return false;
	}

	@Override
	public <T extends Serializable> T put(DefaultedKey<T> key, T value) {
		return dataMap.put(key, value);
	}
	
	@Override
	public <T extends Serializable> T putIfAbsent(DefaultedKey<T> key, T value) {
		return dataMap.putIfAbsent(key, value);
	}

	@Override
	public Serializable get(Object key) {
		return dataMap.get(key);
	}

	@Override
	public <T extends Serializable> T get(DefaultedKey<T> key) {
		return dataMap.get(key);
	}

	@Override
	public <T extends Serializable> T get(Object key, T defaultValue) {
		return dataMap.get(key, defaultValue);
	}

	@Override
	public boolean containsKey(Object key) {
		return dataMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return dataMap.containsValue(value);
	}

	@Override
	public int size() {
		return this.dataMap.size();
	}

	@Override
	public boolean isEmpty() {
		return this.dataMap.isEmpty();
	}

	@Override
	public Serializable remove(Object key) {
		return this.dataMap.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Serializable> m) {
		this.dataMap.putAll(m);
	}

	@Override
	public void clear() {
		this.dataMap.clear();
	}

	@Override
	public Set<String> keySet() {
		return this.dataMap.keySet();
	}

	@Override
	public Collection<Serializable> values() {
		return this.dataMap.values();
	}

	@Override
	public Set<java.util.Map.Entry<String, Serializable>> entrySet() {
		return this.dataMap.entrySet();
	}

	@Override
	public Serializable put(String key, Serializable value) {
		return this.dataMap.put(key, value);
	}
	
	@Override
	public Serializable putIfAbsent(String key, Serializable value) {
		return this.dataMap.put(key, value);
	}

	@Override
	public byte[] serialize() {
		return dataMap.serialize();
	}

	@Override
	public void deserialize(byte[] data) throws IOException {
		dataMap.deserialize(data);
	}

	@Override
	public void deserialize(byte[] data, boolean wipe) throws IOException {
		dataMap.deserialize(data, wipe);
	}

	@Override
	public SerializableMap deepCopy() {
		return dataMap.deepCopy();
	}

	@Override
	public <T> T get(String key, Class<T> clazz) {
		return dataMap.get(key, clazz);
	}
}
