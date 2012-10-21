/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.resources;

import java.util.*;

import org.spout.api.component.components.EntityComponent;
import org.spout.api.resource.Resource;

public class ClientEntityPrefab extends Resource {
	private String name;
	private List<Class<? extends EntityComponent>> components = new ArrayList<Class<? extends EntityComponent>>();
	private Map<String, Object> datas = new HashMap<String, Object>();
	
	public ClientEntityPrefab(String name, List<Class<? extends EntityComponent>> components, Map<String, Object> datas) {
		this.name = name;
		this.components.addAll(components);
		this.datas.putAll(datas);
	}
	
	public String getName() {
		return name;
	}
	
	public List<Class<? extends EntityComponent>> getComponents() {
		return components;
	}
	
	public Map<String, Object> getDatas() {
		return datas;
	}
}
