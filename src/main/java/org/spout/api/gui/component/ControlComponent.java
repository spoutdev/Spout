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
package org.spout.api.gui.component;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.spout.api.component.components.WidgetComponent;
import org.spout.api.gui.FocusReason;
import org.spout.api.gui.render.RectanglePart;
import org.spout.api.gui.render.RenderPart;

public class ControlComponent extends WidgetComponent {
	@Override
	public List<RenderPart> getRenderParts() {
		LinkedList<RenderPart> ret = new LinkedList<RenderPart>();
		if (getOwner().hasFocus()) {
			RectanglePart part = new RectanglePart();
			part.setColor(Color.BLUE);
			part.setSource(getOwner().getGeometry());
			part.setSprite(getOwner().getGeometry());
			part.setZIndex(-100);
			ret.add(part);
		}
		return ret;
	}
	
	@Override
	public void onFocus(FocusReason reason) {
		getOwner().update();
	}
	
	@Override
	public void onFocusLost() {
		getOwner().update();
	}
}
