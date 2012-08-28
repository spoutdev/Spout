package org.spout.api.entity;

import org.spout.api.component.ComponentHolder;
import org.spout.api.entity.components.TransformComponent;

public interface EntityComponentHolder extends ComponentHolder<EntityComponent> {
	/**
	 * Gets a {@link Transform} {@link EntityComponent} representing the current position, scale and
	 * rotation of the entity.
	 * @return The transform component
	 */
	public TransformComponent getTransform();
}
