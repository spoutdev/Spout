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

import org.spout.api.Tickable;

public interface Layout extends MouseEventHandler, Renderable, Tickable {
	/**
	 * Called whenever the size of the underlying container changes.
	 * The implementation has to set new geometry of the contained widgets when that happens
	 * In some cases, it might be useful to update the containers minimum and maximum size
	 */
	public void relayout();
	
	/**
	 * Gets all attached widgets
	 * @return all attached widgets
	 */
	public Widget[] getWidgets();
	
	/**
	 * Adds widgets to the layout. Should call relayout() after that.
	 * This method has to call widget.setLayout(this) for each widget it adds.
	 * @param widgets the widgets to add
	 */
	public void addWidgets(Widget ...widgets);
	
	/**
	 * Removes all widgets from the layout
	 */
	public void clear();
	
	/**
	 * Removes the given widgets from the layout. Should call relayout() after that.
	 * This method has to call widget.setLayout(null) for each widget it removes.
	 * @param widgets the widgets to remove
	 */
	public void removeWidgets(Widget ...widgets);
	
	/**
	 * Sets the parent property to the given container
	 * @param container the new parent
	 */
	public void setParent(Container container);
	
	/**
	 * Gets the parent property
	 * @return the parent container
	 */
	public Container getParent();
	
	public LayoutType getLayoutType();
}
