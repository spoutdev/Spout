package org.spout.api.gui;

import java.awt.Rectangle;

import org.spout.api.plugin.Plugin;

public interface Widget {
	/**
	 * Gets the applied geometry of the widget
	 * @return the geometry
	 */
	public Rectangle getGeometry();
	
	/**
	 * Overrides the applied geometry of the widget
	 * @warning this may be overwritten when the widget is inside a layout
	 * @param geometry the geometry
	 * @return the instance for chained calls
	 */
	public Widget setGeometry(Rectangle geometry);
	
	/**
	 * Gets the minimum size of the widget.
	 * This property is taken into consideration by layouts.
	 * Default is null, but may be different for other widgets
	 * @return the minimum size
	 */
	public Rectangle getMinimumSize();
	
	/**
	 * Gets the maximum size of the widget.
	 * This property is taken into consideration by layouts.
	 * Default is null, but may be different for other widgets
	 * @return the minimum size
	 */
	public Rectangle getMaximumSize();
	
	/**
	 * Sets the minimum size of the widget.
	 * If there is a minimum size defined, layout code will not let the widget become smaller than this.
	 * @warning setting a minimum size that is bigger than the maximum size leads to undefined behavior
	 * @param minimum the minimum size
	 * @return the instance for chained calls
	 */
	public Widget setMinimumSize(Rectangle minimum);
	
	/**
	 * Sets the maximum size of the widget.
	 * If there is a maximum size defined, layout code will not let the widget become greater than this.
	 * @warning setting a maximum size that is smaller than the minimum size leads to undefined behavior
	 * @param maximum the maximum size
	 * @return the instance for chained calls
	 */
	public Widget setMaximumSize(Rectangle maximum);

	/**
	 * Sets the layout
	 * @param layout
	 * @return the instance for chained calls
	 */
	public Widget setLayout(Layout layout);
	
	/**
	 * Gets the layout
	 * @return the layout
	 */
	public Layout getLayout();

	/**
	 * Renders the widget.
	 * The OpenGL drawing position is already transformed to the widgets coordinates
	 */
	public void render();
	
	/**
	 * Gets the plugin that created the widget
	 * @return the plugin
	 */
	public Plugin getPlugin();
}
