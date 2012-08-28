package org.spout.api.component;

import org.spout.api.entity.components.DatatableComponent;
import org.spout.api.tickable.Tickable;

public interface Component extends Tickable {
	
	/**
	 * Sets the parent to holder
	 * @param holder
	 */
	public void attachTo(ComponentHolder holder);
	
	/**
	 * Gets the parent entity associated with this component.
	 * @return the parent entity
	 */
	public ComponentHolder getParent();

	/**
	 * Called when this component is attached to a holder
	 */
	public void onAttached();

	/**
	 * Called when this component is detached from a holder.
	 */
	public void onDetached();

	/**
	 * Called when the parent entity leaves the world.
	 */
	public void onRemoved();

	/**
	 * Called when the entity is set to be sync'd to clients.
	 * 
	 * Updates are NOT ALLOWED within this method.
	 */
	public void onSync();

	/**
	 * Returns the datatable component attached to the parent entity. This component always exists.
	 * @return The datatable component
	 */
	public DatatableComponent getDatatable();

}