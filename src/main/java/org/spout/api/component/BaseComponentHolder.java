/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import org.spout.api.Spout;
import org.spout.api.component.impl.DatatableComponent;

public class BaseComponentHolder implements ComponentHolder {
	/**
	 * Map of class name, component
	 */
	private final BiMap<Class<? extends Component>, Component> components = HashBiMap.create();
	private final DatatableComponent data;

	public BaseComponentHolder() {
		data = add(DatatableComponent.class);
	}

	/**
	 * For use de-serializing a list of components all at once,
	 * without having to worry about dependencies
	 */
	protected void add(Class<? extends Component>... components) {
		HashSet<Component> added = new HashSet<Component>();
		synchronized (components) {
			for (Class<? extends Component> type : components) {
				if (!this.components.containsKey(type)) {
					added.add(add(type, false));
				}
			}
		}
		for (Component type : added) {
			type.onAttached();
		}
	}

	@Override
	public <T extends Component> T add(Class<T> type) {
		return add(type, true);
	}

	/**
	 * Adds a component to the map
	 * @param type to add
	 * @param attach whether to call the component onAttached
	 * @return instantiated component
	 */
	protected <T extends Component> T add(Class<T> type, boolean attach) {
		return add(type, type, attach);
	}

	/**
	 * Adds a component to the map
	 * @param key the component class used as the lookup key
	 * @param type of component to instantiate
	 * @param attach whether to call the component onAttached
	 * @return instantiated component
	 */
	@SuppressWarnings("unchecked")
	protected final <T extends Component> T add(Class<T> key, Class<? extends Component> type, boolean attach) {
		if (type == null || key == null) {
			return null;
		}

		synchronized (components) {
			T component = (T) components.get(key);

			if (component != null) {
				return component;
			}

			try {
				component = (T) type.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			if (component != null) {
				try {
					if (component.attachTo(this)) {
						components.put(key, component);
						if (attach) {
							try {
								component.onAttached();
							} catch (Exception e) {
								// Remove the component from the component map if onAttached can't be
								// called, pass exception to next catch block.
								components.remove(key);
								throw e;
							}
						}
					}
				} catch (Exception e) {
					Spout.getEngine().getLogger().log(Level.SEVERE, "Error while attaching component " + type + ": ", e);
				}
			}
			return component;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Component> T detach(Class<? extends Component> type) {
		Preconditions.checkNotNull(type);
		synchronized (components) {
			T component = (T) get(type);

			if (component != null && component.isDetachable()) {
				components.inverse().remove(component);
				try {
					component.onDetached();
				} catch (Exception e) {
					Spout.getEngine().getLogger().log(Level.SEVERE, "Error detaching component " + type + " from holder: ", e);
				}
			}

			return component;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Component> T get(Class<T> type) {
		Preconditions.checkNotNull(type);
		if (type == null) {
			return null;
		}
		Component component = components.get(type);

		if (component == null) {
			component = findComponent(type);
		}
		return (T) component;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Component> T getExact(Class<T> type) {
		Preconditions.checkNotNull(type);
		synchronized (components) {
			return (T) components.get(type);
		}
	}

	@Override
	@Deprecated
	public boolean has(Class<? extends Component> type) {
		return get(type) != null;
	}

	@Override
	@Deprecated
	public boolean hasExact(Class<? extends Component> type) {
		return getExact(type) != null;
	}

	@Override
	public Collection<Component> values() {
		synchronized (components) {
			return new ArrayList<Component>(components.values());
		}
	}

	@Override
	public DatatableComponent getData() {
		return data;
	}

	@SuppressWarnings("unchecked")
	private <T extends Component> T findComponent(Class<T> type) {
		Preconditions.checkNotNull(type);
		synchronized (components) {
			for (Component component : values()) {
				if (type.isAssignableFrom(component.getClass())) {
					return (T) component;
				}
			}
		}

		return null;
	}
}
