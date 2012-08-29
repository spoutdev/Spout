package org.spout.api.component;

<<<<<<< HEAD
import com.alta189.annotations.RequireDefault;
import org.spout.api.entity.components.DatatableComponent;
=======
import org.spout.api.component.components.DatatableComponent;
>>>>>>> Made World a component holder, added World/Block component (will replace
import org.spout.api.tickable.Tickable;

@RequireDefault
public interface Component extends Tickable {
	
	/**
	 * Attaches to a component holder.
	 * @param holder The component holder to attach to
	 */
	public void attachTo(ComponentHolder<?> holder);
	
	/**
	 * Gets the component holder holding this component.
	 * @return the component holder
	 */
	public ComponentHolder<?> getHolder();

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
