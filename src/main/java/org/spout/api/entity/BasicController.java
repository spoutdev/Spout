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
package org.spout.api.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.spout.api.datatable.DataMap;
import org.spout.api.datatable.DatatableMap;
import org.spout.api.datatable.GenericDatatableMap;
import org.spout.api.entity.controller.type.ControllerType;
import org.spout.api.event.player.PlayerInteractEvent.Action;
import org.spout.api.map.DefaultedMap;

public abstract class BasicController implements Controller {
	//Components
	private final HashMap<Class<? extends Component>, Component<? extends Controller>> components = new HashMap<Class<? extends Component>, Component<? extends Controller>>();
	private final ControllerType type;
	private final DatatableMap datatableMap = new GenericDatatableMap();
	private final DataMap dataMap = new DataMap(datatableMap);
	private Entity parent;

	protected BasicController(ControllerType type) {
		this.type = type;
	}

	public abstract void onAttached();

	@Override
	public final void tick(float dt) {
		if(canTick()) {
			onTick(dt);
			tickComponents(dt);
		}
	}

	@Override
	public void onTick(float dt) {
	}

	@Override
	public boolean canTick() {
		return true;
	}

	public void onDeath() {

	}

	public void finalizeTick() {

	}

	public void onInteract(Entity entity, Action type) {

	}

	public ControllerType getType() {
		return type;
	}

	public final DefaultedMap<String, Serializable> getDataMap() {
		return dataMap;
	}

	public void onSave() {

	}

	public boolean isSavable() {
		return true;
	}

	public boolean isImportant() {
		return false;
	}

	public Entity getParent() {
		return parent;
	}

	public void attachToEntity(Entity parent) {
		this.parent = parent;
	}

	@Override
	public <T extends Component> T addComponent(T component) {
		Class<? extends Component> clazz = component.getClass();
		if (hasComponent(clazz)) {
			return (T) getComponent(clazz);
		}
		components.put(clazz, component);
		component.attachToController(this);
		component.onAttached();
		return component;
	}

	@Override
	public boolean removeComponent(Class<? extends Component> aClass) {
		if (!hasComponent(aClass)) {
			return false;
		}
		getComponent(aClass).onDetached();
		components.remove(aClass);
		return true;
	}

	@Override
	public <T extends Component> T getComponent(Class<T> aClass) {
		return (T) components.get(aClass);
	}

	@Override
	public boolean hasComponent(Class<? extends Component> aClass) {
		return components.containsKey(aClass);
	}

	private final void tickComponents(float dt) {
		ArrayList<Component> coms = new ArrayList<Component>(components.values());
		Collections.sort(coms);

		for (Component component : coms) {
			if (component.canTick()) {
				component.tick(dt);
			}
			if (component.runOnce()) {
				removeComponent(component.getClass());
			}
		}
	}
}