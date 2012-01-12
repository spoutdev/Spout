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
 * The GenericSlider is a bar with which a user can set a value.
 *
 * The value is a float between 0f to 1f representing how far from the left
 * the slider is.
 */
public interface Slider extends Control, Label {

	/**
	 * Gets the slider position (between 0.0f and 1.0f)
	 * @return slider position
	 */
	public float getSliderPosition();

	/**
	 * Sets the slider position. Values below 0.0f are rounded to 0, and values above 1.0f are rounded to 1
	 * @param value to set
	 * @return slider
	 */
	public Slider setSliderPosition(float value);

	@Override
	public Slider setText(String text);

	@Override
	public Slider setTextColor(Color color);

	@Override
	public Slider setAuto(boolean auto);

	@Override
	public Slider setAlign(WidgetAnchor align);
	/**
	 * Fires when this slider is dragged on the screen.
	 *
	 * This event is also sent to the screen listener, afterwards.
	 *
	 * @param event
	 */
// TODO
//	public void onSliderDrag(SliderDragEvent event);
}
