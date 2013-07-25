/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.gui;

import org.spout.api.component.ComponentOwner;
import org.spout.api.datatable.ManagedMap;
import org.spout.api.geo.discrete.Transform2D;
import org.spout.api.math.Rectangle;
import org.spout.api.tickable.Tickable;

/**
 * Represents an element on a {@link Screen}.
 */
public interface Widget extends Tickable, ComponentOwner, Focusable, RenderPartContainer {
	/**
	 * Invokes a render update in the next frame
	 */
	public abstract void update();

	/**
	 * Returns the screen the widget is on.
	 *
	 * @return screen widget is on
	 */
	public abstract Screen getScreen();

	/**
	 * Sets the screen
	 *
	 * @param screen to set
	 */
	public abstract void setScreen(Screen screen);

	/**
	 * Returns the geometry of this widget.
	 *
	 * @return transform of widget
	 */
	public abstract Transform2D getTransform();

	/**
	 * Returns the bounding box of this widget.
	 *
	 * @return bounding box
	 */
	public abstract Rectangle getBounds();

	/**
	 * Sets the bounding box of this widget
	 *
	 * @param bounds of widget
	 */
	public abstract void setBounds(Rectangle bounds);

	/**
	 * Gets the {@link ManagedMap} which a Widget always has.
	 *
	 * @return ManagedMap
	 */
	@Override
	public ManagedMap getData();
}
