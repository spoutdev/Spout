package org.spout.api.gui.screen;

import java.awt.Color;
import java.awt.Rectangle;

import org.lwjgl.opengl.Display;
import org.spout.api.gui.GenericScreen;
import org.spout.api.gui.Widget;
import org.spout.api.plugin.Plugin;

/**
 * Defines a fullscreen
 * A fullscreen will be the last screen you can see, all screens that are below it won't be rendered.
 */
public class FullScreen extends GenericScreen {
	private Color backgroundColor = new Color(0,0,0,0);

	public FullScreen(Plugin plugin) {
		super(plugin);
	}

	@Override
	public Rectangle getGeometry() {
		return new Rectangle(Display.getWidth(), Display.getHeight());
	}

	@Override
	public Widget setGeometry(Rectangle geometry) {
		return this;
	}
	
	/**
	 * Sets the background color of the screen
	 * @param color
	 * @return the instance
	 */
	public FullScreen setBackgroundColor(Color color) {
		this.backgroundColor = color;
		return this;
	}
	
	/**
	 * Gets the background color of the screen
	 * @return the background color
	 */
	public Color getBackgroudColor() {
		return backgroundColor;
	}

	/**
	 * Gets if the background is transparent
	 * @return
	 */
	public boolean isTransparent() {
		return backgroundColor.getAlpha() != 0;
	}
}
