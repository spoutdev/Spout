package org.spout.api.entity;

public interface ComponentBase {
	
	/**
	 * Adds a new Component to the Entity.  If the entity already contains a component of that type, then a new component is not 
	 * constructed, and the one already attached is returned
	 * 
	 * @param component Type of component to add
	 * @return The component created, or the one already attached
	 */
	public EntityComponent addComponent(Class<? extends EntityComponent> component);
	
	/**
	 * Removes a component from the list
	 * @param component Type of component to remove
	 * @return True if a component is removed, false if not.  False is also returned if the component doesn't exist.
	 */
	public boolean removeComponent(Class<? extends EntityComponent> component);
	
	/**
	 * Returns an instance of the component attached to the object
	 * @param component the type of component to get
	 * @return The component instance, or NULL if it doesn't exist
	 */
	public EntityComponent getComponent(Class<? extends EntityComponent> component);
	
	/**
	 * Returns True if the type provided is attached or false if not.
	 * @param component
	 * @return
	 */
	public boolean hasComponent(Class<? extends EntityComponent> component);
	
}
