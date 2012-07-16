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

/**
 * A container contains a layout, which is capable to hold as many widgets as you want.
 * Containers can be simple widgets that just contain other widgets, or more advanced ones that offer scrolling and stuff like that.
 * Also, a screen is by definition a container.
 * To implement your own container, you should not only implement the get/set methods defined in this interface, but also redirect method calls to 
 *  - onMouseDown
 *  - onMouseUp
 *  - onMouseMove
 *  - render
 */
public interface Container extends Widget, MouseEventHandler {
	/**
	 * Gets the layout of this container
	 * @return the layout
	 */
	public Layout getLayout();

	/**
	 * Sets the layout of this container
	 * @warning when setting the layout to null, this can lead to unexpected behavior. Also, you will loose all widgets of the old layout, and have to re-add them if needed.
	 * @param layout
	 * @return
	 */
	public Container setLayout(Layout layout);
}
