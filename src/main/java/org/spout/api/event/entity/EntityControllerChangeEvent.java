package org.spout.api.event.entity;

import org.spout.api.Source;
import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.event.HandlerList;

/**
 * Called when an entity is changing controllers.
 */
public class EntityControllerChangeEvent extends EntityEvent {
	private static HandlerList handlers = new HandlerList();

	private Source source;

	private Controller newController;

	public EntityControllerChangeEvent(Entity e, Source source, Controller newController) {
		super(e);
		this.source = source;
		this.newController = newController;
	}

	/**
	 * Gets the source of this event.
	 *
	 * @return An Source that is the source of the event.
	 */
	public Source getSource() {
		return source;
	}

	/**
	 * Sets the source of this event.
	 *
	 * @param source The source of this event.
	 */
	public void setSource(Source source) {
		this.source = source;
	}

	/**
	 * Gets the new controller of the entity.
	 *
	 * @return The new controller.
	 */
	public Controller getNewController() {
		return newController;
	}

	/**
	 * Sets the new controller of the entity.
	 *
	 * @param newController The new controller of the entity.
	 */
	public void setNetController(Controller newController) {
		this.newController = newController;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
