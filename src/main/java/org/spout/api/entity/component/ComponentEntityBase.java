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
package org.spout.api.entity.component;

import java.util.HashMap;

import org.spout.api.entity.ComponentBase;
import org.spout.api.entity.EntityComponent;
import org.spout.api.tickable.BasicTickable;

/**
 * Base Class for all Component Based tickable objects
 * 
 * A Component Based object is any object that contains a set of Components. 
 *
 */
public class ComponentEntityBase extends BasicTickable implements ComponentBase {
	private final HashMap<Class<? extends EntityComponent >, EntityComponent> components = new HashMap<Class<? extends EntityComponent>, EntityComponent>();

	@Override
	public EntityComponent addComponent(Class<? extends EntityComponent> component) {
		if(hasComponent(component)) return getComponent(component);
		
		try {
			EntityComponent ec = component.newInstance();
			components.put(component, ec);
			return ec;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new RuntimeException("Cannot Create Component!");			
	}

	@Override
	public boolean removeComponent(Class<? extends EntityComponent> component) {
		if(!hasComponent(component)) return false;
		components.remove(component);
		return true;
	}

	@Override
	public EntityComponent getComponent(Class<? extends EntityComponent> component) {
		return components.get(component);
	}

	@Override
	public boolean hasComponent(Class<? extends EntityComponent> component) {
		return components.containsKey(component);
	}
	
	
}
