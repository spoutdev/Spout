/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.gui.component;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.spout.api.Spout;
import org.spout.api.gui.component.LabelComponent;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.math.Rectangle;
import org.spout.engine.gui.SpoutRenderPart;
import org.spout.engine.resources.ClientFont;
import org.spout.engine.SpoutClient;

public class SpoutLabelComponent extends LabelComponent {
	private ClientFont font;
	private float x = 0;
	private float y = 0;
	private Color color = Color.black;

	public void setColor(Color color) {
		this.color = color;
	}
	
	public void setFont(ClientFont font) {
		this.font = font;
	}
	
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public List<RenderPart> getRenderParts() {
		List<RenderPart> ret = new LinkedList<RenderPart>();
		
		float w = font.getWidth();
		float h = font.getHeight();

		float xCursor = x;
		float yCursor = y;
		
		float screenWidth = ((SpoutClient)Spout.getEngine()).getResolution().getX();
		float screenHeight = ((SpoutClient)Spout.getEngine()).getResolution().getY();
		
		for (int i=0 ; i<getText().length() ; i++) {
			char c = getText().charAt(i);
			if (c==' ') {
				xCursor += font.getSpaceWidth()/screenWidth;
			} else if (c=='\n') {
				xCursor = x;
				yCursor -= font.getCharHeight()/screenHeight;
			} else {
				java.awt.Rectangle r = font.getPixelBounds(c);

				RenderPart text = new SpoutRenderPart(font.getMaterial(),
						 new Rectangle(r.x/w, 0f, r.width/w, 1f),
						 new Rectangle(xCursor, yCursor, (float)r.width/screenWidth, h/screenHeight));
				
				text.setColor(color);
				
				xCursor += (float)font.getAdvance(c)/screenWidth;
				
				ret.add(text);
			}
		}
		
		return ret;
	}
}
