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
 * The GenericLabel class represents text on the users screen.
 *
 * Normally the text will overflow the widget "box" if it is smaller than
 * the text itself, however you can force the minimum size of the widget to
 * the size of the text content with the setAuto method, and use the setFixed
 * method to stop it resizing if inside a Container.
 */
public interface Label extends Widget {

	/**
	 * Gets the text of the label
	 * @return text
	 */
	public String getText();

	/**
	 * Sets the text of the label
	 * @param text to set
	 * @return label
	 */
	public Label setText(String text);

	/**
	 * Gets the color for the text
	 * @return color
	 */
	public Color getTextColor();

	/** 
	 * Sets the color for the text
	 * @param color to set
	 * @return label
	 */
	public Label setTextColor(Color color);

	/** 
	 * Determines if text expands to fill width and height
	 * @param auto
	 * @return label
	 */
	public Label setAuto(boolean auto);

	/** 
	 * True if the text will expand to fill width and height
	 * @return 
	 */
	public boolean isAuto();

	/**
	 * Does this widget automatically resize with it's contents
	 * @return 
	 */
	public boolean isResize();

	/**
	 * Tell this widget to resize with it's contents
	 * @param resize
	 * @return 
	 */
	public Label setResize(boolean resize);

	/**
	 * Actually resize the Label with the current text size
	 * @return 
	 */
	public Label doResize();

	/**
	 * Get the text alignment
	 * @return 
	 */
	public WidgetAnchor getAlign();

	/**
	 * Set the text alignment
	 * @param pos
	 * @return 
	 */
	public Widget setAlign(WidgetAnchor pos);

	/**
	 * Set the scale of the text
	 * @param scale to set
	 */
	public Label setScale(float scale);

	/**
	 * Gets the scale of the text
	 * @return scale of text
	 */
	public float getScale();
}
