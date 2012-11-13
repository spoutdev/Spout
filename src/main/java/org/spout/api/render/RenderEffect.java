package org.spout.api.render;

public interface RenderEffect {

	/**
	 * Called right before rendering
	 */
	public abstract void preRender();

	/**
	 * Called right after rendering
	 */
	public abstract void postRender();

}
