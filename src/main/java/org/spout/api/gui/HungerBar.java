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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.spout.api.ClientOnly;

public class HungerBar extends AbstractWidget {
	private int icons = 10;
	private int iconOffset = 8;

	/**
	 * Package-private constructor.
	 */
	HungerBar() {
		setDirty(false);
		setId(5);
		setX(427 / 2 + 82); // 295
		setY(201);
		setWidth(getWidth()); // Don't know the default - ignored, but prevents warnings...
		setAnchor(WidgetAnchor.BOTTOM_CENTER);
	}

	@Override
	public int getNumBytes() {
		return super.getNumBytes() + 8;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		setNumOfIcons(input.readInt());
		setIconOffset(input.readInt());
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		output.writeInt(getNumOfIcons());
		output.writeInt(getIconOffset());
	}

	/**
	 * Gets the maximum number of food icons to display on the HUD.
	 * 
	 * Hunger bar is scaled to fit the number of icons appropriately.
	 * 
	 * @return icons displayed
	 */
	public int getNumOfIcons() {
		return icons;
	}

	/**
	 * Sets the maximum number of food icons to display on the HUD.
	 * 
	 * Hunger bar is scaled to fit the number of icons appropriately.
	 * 
	 * @param icons to display
	 * @return this
	 */
	public HungerBar setNumOfIcons(int icons) {
		this.icons = icons;
		return this;
	}

	/**
	 * Gets the number of pixels each icon is offset when drawing the next icon.
	 * 
	 * @return pixel offset
	 */
	public int getIconOffset() {
		return iconOffset;
	}

	/**
	 * Sets the number of pixels each icon is offset when drawing the next icon.
	 * 
	 * @param iconOffset when drawing icons
	 * @return this
	 */
	public HungerBar setIconOffset(int iconOffset) {
		this.iconOffset = iconOffset;
		return this;
	}

	public WidgetType getType() {
		return WidgetType.HungerBar;
	}

	@Override
	public int getVersion() {
		return super.getVersion() + 1;
	}

	@Override
	@ClientOnly
	public void render() {
//		Spoutcraft.getClient().getRenderDelegate().render(this);
	}
}
