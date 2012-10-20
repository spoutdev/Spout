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

import java.util.LinkedList;
import java.util.List;

import org.spout.api.component.components.WidgetComponent;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.gui.render.TextPart;
import org.spout.api.map.DefaultedKey;

public class LabelComponent extends WidgetComponent {
	private static final DefaultedKey<String> KEY_TEXT = new DefaultedKey<String>() {
		
		@Override
		public String getDefaultValue() {
			return "(your text here)";
		}
		
		@Override
		public String getKeyString() {
			return "button-text";
		}
		
	};
	
	@Override
	public List<RenderPart> getRenderParts() {
		List<RenderPart> ret = new LinkedList<RenderPart>();
		TextPart text = new TextPart();
		text.setSource(getOwner().getGeometry());
		text.setSprite(getOwner().getGeometry());
		text.setText(getText());
		ret.add(text);
		return ret;
	}


	public String getText() {
		return getData().get(KEY_TEXT);
	}

	public void setText(String text) {
		getData().put(KEY_TEXT, text);
		getOwner().update();
	}

}
