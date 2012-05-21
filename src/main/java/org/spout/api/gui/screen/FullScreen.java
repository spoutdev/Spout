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
package org.spout.api.gui.screen;

import java.awt.Color;
import java.awt.Rectangle;

import org.lwjgl.opengl.Display;
import org.spout.api.gui.GenericScreen;
import org.spout.api.gui.ScreenType;
import org.spout.api.gui.Widget;
import org.spout.api.plugin.Plugin;

/**
 * Defines a fullscreen
 * A fullscreen will be the last screen you can see, all screens that are below it won't be rendered.
 */
public class FullScreen extends GenericScreen {
	private Color backgroundColor = new Color(0,0,0,0);

	@Override
	public Rectangle getGeometry() {
		return new Rectangle(Display.getWidth(), Display.getHeight());
	}

	@Override
	public Widget setGeometry(Rectangle geometry) {
		return this;
	}
	
	/**
	 * Sets the background color of the screen
	 * @param color
	 * @return the instance
	 */
	public FullScreen setBackgroundColor(Color color) {
		this.backgroundColor = color;
		return this;
	}
	
	/**
	 * Gets the background color of the screen
	 * @return the background color
	 */
	public Color getBackgroudColor() {
		return backgroundColor;
	}

	/**
	 * Gets if the background is transparent
	 * @return
	 */
	public boolean isTransparent() {
		return backgroundColor.getAlpha() != 0;
	}

	@Override
	public ScreenType getScreenType() {
		return ScreenType.FULLSCREEN;
	}
}
