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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.spout.api.gui.render.RenderPart;
import org.spout.api.gui.render.RenderPartPack;
import org.spout.api.math.Rectangle;
import org.spout.api.render.RenderMaterial;

/**
 * Represents a {@link WidgetComponent} with one render part.
 */
public class RenderPartComponent extends WidgetComponent {
	private RenderPartPack pack = new RenderPartPack();
	private RenderPart rect = new RenderPart();

	public RenderPartComponent() {
		pack.add(rect);
	}

	@Override
	public List<RenderPartPack> getRenderPartPacks() {
		List<RenderPartPack> parts = new ArrayList<>();
		parts.add(pack);
		return parts;
	}

	/**
	 * Sets the bounds of the source of the render part. This is commonly used for sprite sheets and should be left at zero for simple colored rectangles.
	 *
	 * @param source of part
	 */
	public void setSource(Rectangle source) {
		rect.setSource(source);
	}

	/**
	 * Returns the bounds of the source of the render part. This is commonly used for sprite sheets and should be left at zero for simple colored rectangles.
	 *
	 * @return source of part
	 */
	public Rectangle getSource() {
		return rect.getSprite();
	}

	/**
	 * Sets the bounds of the actual sprite of the render material. This is used for specifying the actual visible size of the render part.
	 *
	 * @param sprite of render part
	 */
	public void setSprite(Rectangle sprite) {
		rect.setSprite(sprite);
	}

	/**
	 * Returns the bounds of the actual sprite of the render material. This is used for specifying the actual visible size of the render part.
	 *
	 * @return sprite of render part
	 */
	public Rectangle getSprite() {
		return rect.getSprite();
	}

	/**
	 * Sets the color of this part.
	 *
	 * @param color of part
	 */
	public void setColor(Color color) {
		rect.setColor(color);
	}

	/**
	 * Returns the color of this part.
	 *
	 * @return color of part
	 */
	public Color getColor() {
		return rect.getColor();
	}

	public RenderMaterial getRenderMaterial() {
		return pack.getRenderMaterial();
	}

	public void setRenderMaterial(RenderMaterial material) {
		pack.setRenderMaterial(material);
	}
}
