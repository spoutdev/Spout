/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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
package org.spout.engine.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spout.api.component.Component;
import org.spout.api.component.impl.ModelHolderComponent;
import org.spout.api.entity.Entity;
import org.spout.api.entity.EntityPrefab;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.resource.Resource;

import org.spout.engine.entity.SpoutEntity;

public class ClientEntityPrefab extends Resource implements EntityPrefab {
	private String name;
	private List<Class<? extends Component>> components = new ArrayList<Class<? extends Component>>();
	private Map<String, Object> data = new HashMap<String, Object>();

	public ClientEntityPrefab(String name, List<Class<? extends Component>> components, Map<String, Object> data) {
		this.name = name;
		this.components.addAll(components);
		this.data.putAll(data);
	}

	public String getName() {
		return name;
	}

	public List<Class<? extends Component>> getComponents() {
		return components;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public Entity createEntity(Point point) {
		SpoutEntity entity = new SpoutEntity(point);
		for (Class<? extends Component> c : components) {
			entity.add(c);
		}

		if (data.containsKey("Model")) {
			entity.add(ModelHolderComponent.class).addModel((String) data.get("Model"));
		}

		return entity;
	}

	public Entity createEntity(Transform transform) {
		SpoutEntity entity = new SpoutEntity(transform);
		for (Class<? extends Component> c : components) {
			entity.add(c);
		}

		if (data.containsKey("Model")) {
			entity.add(ModelHolderComponent.class).addModel((String) data.get("Model"));
		}

		return entity;
	}
}
