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
package org.spout.api.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.spout.api.Spout;
import org.spout.api.component.components.DatatableComponent;
import org.spout.api.tickable.Tickable;

public abstract class Component implements Tickable {
	
	private static ConcurrentHashMap<Class<? extends Component>, Set<Class<? extends Component>>> dependencies = new ConcurrentHashMap<Class<? extends Component>, Set<Class<? extends Component>>>();
	
	private ComponentOwner owner;

	public Component() {
	}

	/**
	 * Attaches to a component owner.
	 * @param owner the component owner to attach to
	 * @return true if successful
	 */
	public boolean attachTo(ComponentOwner owner) {
		this.owner = owner;
		return true;
	}

	/**
	 * Gets the component holder that is holding this component.
	 * @return the component holder
	 */
	public ComponentOwner getOwner() {
		return owner;
	}

	/**
	 * Called when this component is attached to a holder.
	 */
	public void onAttached() {
	}

	/**
	 * Called when this component is detached from a holder.
	 */
	public void onDetached() {
	}

	/**
	 * Specifies whether or not this component can be detached,
	 * after it has already been attached to a holder.
	 * @return true if it can be detached
	 */
	public boolean isDetachable() {
		return true;
	}

	/**
	 * Called when the holder is set to be synchronized.
	 * <p/>
	 * This method is READ ONLY. You cannot update in this method.
	 */
	public void onSync() {
	}

	@Override
	public boolean canTick() {
		return true;
	}

	@Override
	public final void tick(float dt) {
		if (canTick()) {
			onTick(dt);
		}
	}

	@Override
	public void onTick(float dt) {
	}

	/**
	 * Gets the datatable component attached to the holder.
	 * This component exists in every holder.
	 * @return the datatable component
	 */
	public final DatatableComponent getData() {
		return getOwner().getData();
	}

	/**
	 * Registers a dependency.
	 * 
	 * @param clazz the class that depends on the dependency
	 * @param depend the dependency
	 */
	public static void addDependency(Class<? extends Component> clazz, Class<? extends Component> depend) {
		Set<Class<? extends Component>> set = dependencies.get(depend);
		if (set == null) {
			set = new HashSet<Class<? extends Component>>();
			Set<Class<? extends Component>> old = dependencies.putIfAbsent(depend, set);
			if (old != null) {
				set = old;
			}
		}
		set.add(clazz);
	}
	
	/**
	 * Sorts a list of Components based on dependencies.
	 * 
	 * @param components
	 * @return the sorted list
	 */
	public static List<? extends Component> dependSort(Collection<Component> components) {
		
		LinkedHashSet<Class<? extends Component>> pending = new LinkedHashSet<Class<? extends Component>>();
		
		for (Component c : components) {
			pending.add(c.getClass());
		}
		
		List<Component> sorted = new ArrayList<Component>(components.size());
		
		while (!pending.isEmpty()) {
			boolean updated = false;
			Iterator<Class<? extends Component>> itr = pending.iterator();
			while (itr.hasNext()) {
				boolean hit = false;
				Class<? extends Component> componentClass = itr.next();
				Set<Class<? extends Component>> depends = dependencies.get(componentClass);
				if (depends != null) {
					for (Class<? extends Component> d : depends) {
						if (pending.contains(d)) {
							hit = true;
							break;
						}
					}
				}
				if (!hit) {
					itr.remove();
					for (Component c : components) {
						if (componentClass.equals(c.getClass())) {
							sorted.add(c);
							updated = true;
						}
					}
				}
			}
			if (!updated && !pending.isEmpty()) {
				Spout.getLogger().info("Unable to properly sort array according to dependencies");
				itr = pending.iterator();
				while (itr.hasNext()) {
					Class<? extends Component> componentClass = itr.next();
					itr.remove();
					for (Component c : components) {
						if (componentClass.equals(c.getClass())) {
							sorted.add(c);
						}
					}
				}
			}
		}
		
		return sorted;
	}
		
}
