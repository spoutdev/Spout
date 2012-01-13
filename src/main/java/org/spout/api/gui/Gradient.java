/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
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
	 * Set both top and bottom gradient color in one call.
	 * @param top
	 * @param bottom
	 * @return gradient
	 */
	public Gradient setColor(Color top, Color bottom);

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
