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
