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

/**
 * The Spout implementation of the default Health Bar.
 */
public class HealthBar extends AbstractWidget {

	private int icons = 10;
	private int iconOffset = 8;
	private float dangerPercent = 20f;

	/**
	 * Package-private constructor.
	 */
	HealthBar() {
		setDirty(false);
		setId(4);
		setX(427 / 2 - 91); //122
		setY(201);
		setWidth(getWidth()); // Don't know the default - ignored, but prevents warnings...
		setAnchor(WidgetAnchor.BOTTOM_CENTER);
	}

	@Override
	public int getNumBytes() {
		return super.getNumBytes() + 12;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		setMaxNumHearts(input.readInt());
		setIconOffset(input.readInt());
		setDangerPercent(input.readFloat());
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		output.writeInt(getMaxNumHearts());
		output.writeInt(getIconOffset());
		output.writeFloat(getDangerPercent());
	}

	@Override
	public WidgetType getType() {
		return WidgetType.HealthBar;
	}

	/**
	 * Gets the maximum number of hearts displayed on the HUD.
	 * 
	 * Health is scaled to fit the number of hearts appropriately.
	 * @return hearts displayed
	 */
	public int getMaxNumHearts() {
		return icons;
	}

	/**
	 * Sets the maximum number of hearts displayed on the HUD.
	 * 
	 * Health is scaled to fit the number of hearts appropriately.
	 * @param hearts to display
	 * @return this
	 */
	public HealthBar setMaxNumHearts(int hearts) {
		this.icons = hearts;
		return this;
	}

	/**
	 * Gets the number of pixels each heart is offset when drawing the next heart.
	 * @return pixel offset
	 */
	public int getIconOffset() {
		return iconOffset;
	}

	/**
	 * Sets the number of pixels each heart is offset when drawing the next heart.
	 * @param offset when drawing hearts
	 * @return this
	 */
	public HealthBar setIconOffset(int offset) {
		iconOffset = offset;
		return this;
	}

	/**
	 * Gets the percent of health a player needs to fall to or below in order for the hearts to begin blinking.
	 * 
	 * Valid percents are between zero and one hundred, inclusive.
	 * @return danger percent
	 */
	public float getDangerPercent() {
		return dangerPercent;
	}

	/**
	 * Sets the percent of health a player needs to fall to or below in order for the hearts to begin blinking.
	 * 
	 * Valid percents are between zero and one hundred, inclusive.
	 * @param percent
	 * @return this
	 */
	public HealthBar setDangerPercent(float percent) {
		dangerPercent = percent;
		return this;
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
