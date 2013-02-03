/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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

import java.util.List;

import org.spout.api.component.ComponentHolder;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.math.Rectangle;
import org.spout.api.tickable.Tickable;

public interface Widget extends Tickable, ComponentHolder {
	
	/**
	 * Returns a sorted list of render parts that consists of all render parts of the components
	 * @return a list of render parts
	 */
	public abstract List<RenderPart> getRenderParts();

	/**
	 * Invokes a render update in the next frame
	 */
	public abstract void update();

	public abstract void setScreen(Screen screen);

	public abstract Screen getScreen();

	public abstract boolean canFocus();
	
	public abstract boolean isFocused();

	public abstract void onFocusLost();

	public abstract void onFocus(FocusReason reason);

	public abstract Rectangle getTranslatedGeometry();

	public abstract Rectangle getGeometry();

	public abstract void setGeometry(Rectangle geometry);
}
