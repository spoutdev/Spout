package org.spout.api.component.components;

import org.spout.api.entity.EntityComponent;

public interface BlockComponent extends EntityComponent {
	/**
	 * Gets the material this component represents.
	 */
	public void getMaterial();
}
