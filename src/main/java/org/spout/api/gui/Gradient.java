/*
 * This file is part of SpoutAPI (http://wwwi.getspout.org/).
 * 
 * Spout API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spout.api.gui;

import org.spout.api.util.Color;

/**
 * The GenericGradient represents a vertical gradient only.
 *
 * You can specify the same colour for the top and bottom in order to get a
 * solid block of colour, and can set the alpha-level of the Color in order
 * to make it translucent.
 */
public interface Gradient extends Widget {

	/**
	 * Gets the top colour of the gradient to render.
	 * @return color
	 */
	public Color getTopColor();

	/**
	 * Sets the top colour of the gradient to render.
	 * @param color
	 * @return gradient
	 */
	public Gradient setTopColor(Color color);

	/**
	 * Gets the bottom colour of the gradient to render.
	 * @return color
	 */
	public Color getBottomColor();

	/**
	 * Sets the bottom colour of the gradient to render.
	 * @param color
	 * @return gradient
	 */
	public Gradient setBottomColor(Color color);

	/**
	 * Set both top and bottom gradient color in one call.
	 * @param color
	 * @return gradient
	 */
	public Gradient setColor(Color color);

	/**
	 * Set the direction the gradient is drawn.
	 * Default is VERTICAL, if using HORIZONTAL then read top as left and bottom as right.
	 * @param axis the orientation to draw in
	 * @return
	 */
	public Gradient setOrientation(Orientation axis);

	/**
	 * Get the direction the gradient is drawn.
	 * Default is VERTICAL, if using HORIZONTAL then read top as left and bottom as right.
	 * @return the orientation being used
	 */
	public Orientation getOrientation();
}
