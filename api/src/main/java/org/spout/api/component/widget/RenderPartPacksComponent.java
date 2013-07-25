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
package org.spout.api.component.widget;

import java.util.ArrayList;
import java.util.List;

import org.spout.api.gui.render.RenderPartPack;

/**
 * Represents a {@link WidgetComponent} with multiple {@link RenderPartPack}s.
 */
public class RenderPartPacksComponent extends WidgetComponent {
	private final List<RenderPartPack> parts = new ArrayList<RenderPartPack>();

	@Override
	public List<RenderPartPack> getRenderPartPacks() {
		return parts;
	}

	/**
	 * Adds a render part to the component.
	 *
	 * @return size
	 */
	public int add(RenderPartPack part) {
		// Last added on top
		return add(part, parts.size());
	}

	/**
	 * Adds a render part to the component.
	 *
	 * @param part to add
	 * @param zIndex index of part
	 * @return size
	 */
	public int add(RenderPartPack part, int zIndex) {
		part.setZIndex(zIndex);
		parts.add(part);
		return parts.size() - 1;
	}

	/**
	 * Returns a part at the specified index
	 *
	 * @param index to get part from
	 * @return part at specified index
	 */
	public RenderPartPack get(int index) {
		return parts.get(index);
	}
}
