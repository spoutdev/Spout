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
package org.spout.api.gui.widget;

import java.awt.Rectangle;

import org.spout.api.gui.Layout;
import org.spout.api.gui.Screen;
import org.spout.api.gui.Widget;
import org.spout.api.signal.SignalSubscriberObject;

public abstract class AbstractWidget extends SignalSubscriberObject implements Widget {
	
	private Rectangle geometry = new Rectangle(), minimumSize = null, maximumSize = null;
	private Layout layout = null;
	private Screen screen = null;

	@Override
	public Rectangle getGeometry() {
		return new Rectangle(geometry);
	}

	@Override
	public Widget setGeometry(Rectangle geometry) {
		this.geometry = new Rectangle(geometry);
		return this;
	}

	@Override
	public Rectangle getMinimumSize() {
		return new Rectangle(minimumSize);
	}

	@Override
	public Rectangle getMaximumSize() {
		return new Rectangle(maximumSize);
	}

	@Override
	public Widget setMinimumSize(Rectangle minimum) {
		this.minimumSize = new Rectangle(minimum);
		return this;
	}

	@Override
	public Widget setMaximumSize(Rectangle maximum) {
		this.maximumSize = new Rectangle(maximum);
		return this;
	}

	@Override
	public Widget setParent(Layout layout) {
		this.layout = layout;
		if (layout != null) {
			setScreen(getParent().getParent().getScreen());	//works even when the container IS a screen, because GenericScreen returns itself in that case.
		}
		return this;
	}

	@Override
	public Layout getParent() {
		return layout;
	}

	@Override
	public Screen getScreen() {
		return screen;
	}

	@Override
	public Widget setScreen(Screen screen) {
		this.screen = screen;
		return this;
	}

	@Override
	public void onTick(float dt) {
		
	}
}
