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
package org.spout.api.gui.component;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.spout.api.component.type.WidgetComponent;
import org.spout.api.gui.FocusReason;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.gui.render.RenderPartPack;
import org.spout.api.map.DefaultedKey;
import org.spout.api.map.DefaultedKeyImpl;
import org.spout.api.math.Rectangle;
import org.spout.api.render.SpoutRenderMaterials;

public class ControlComponent extends WidgetComponent {
	private static final DefaultedKey<Integer> KEY_TAB_INDEX = new DefaultedKeyImpl<Integer>("tabIndex", 0);
	
	@Override
	public List<RenderPartPack> getRenderPartPacks() {
		LinkedList<RenderPartPack> ret = new LinkedList<RenderPartPack>();
		if (getOwner().isFocused()) {
			RenderPartPack pack = new RenderPartPack(SpoutRenderMaterials.GUI_COLOR);
			RenderPart part = new RenderPart();
			part.setColor(Color.BLUE);
			part.setSource(new Rectangle(0, 0, 0, 0));
			part.setSprite(new Rectangle(0,0,0.1f,0.1f));
			part.setZIndex(-100);
			
			pack.add(part);
			ret.add(pack);
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
	
	public int getTabIndex() {
		return getData().get(KEY_TAB_INDEX);
	}
	
	public void setTabIndex(int newIndex) {
		getData().put(KEY_TAB_INDEX, newIndex);
	}
	
}
