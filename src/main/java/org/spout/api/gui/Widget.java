/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.gui;

import java.awt.Rectangle;

import org.spout.api.Tickable;
import org.spout.api.plugin.Plugin;

public interface Widget extends Renderable, Tickable {
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
	public Widget setParent(Layout layout);
	
	/**
	 * Gets the layout
	 * @return the layout
	 */
	public Layout getParent();
	
	/**
	 * Gets the screen the widget is on
	 * @return the screen
	 */
	public Screen getScreen();
	
	/**
	 * Sets the screen the widget is on
	 * @param screen the screen to set
	 * @return the instance for chained calls
	 */
	public Widget setScreen(Screen screen);
	
	/**
	 * Gets the type of the widget
	 * @return the widget type
	 */
	public WidgetType getWidgetType();
}
