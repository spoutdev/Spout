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

/**
 * The GenericItemWidget class allows you to display a block or item as it
 * would be in the player's inventory.
 *
 * Don't forget that most items are in fact 3d, so also need a depth to draw
 * properly.
 */
public interface ItemWidget extends Widget {

	/**
	 * Sets the type id of this item widget
	 * @param id
	 * @return ItemWidget
	 */
	public ItemWidget setTypeId(int id);

	/**
	 * Gets the type id of this item widget
	 * @return type id
	 */
	public int getTypeId();

	/**
	 * Sets the data of this item widget
	 * @param data to set
	 * @return ItemWidget
	 */
	public ItemWidget setData(short data);

	/**
	 * Gets the data of this item widget, is zero by default
	 * @return data
	 */
	public short getData();

	/**
	 * Sets the z render depth for this 3-d item widget
	 * @param depth to render at
	 * @return ItemWidget
	 */
	public ItemWidget setDepth(int depth);

	/**
	 * Gets the z render depth for this 3-d item widget
	 * @return depth
	 */
	public int getDepth();

	@Override
	public ItemWidget setWidth(int width);

	@Override
	public ItemWidget setHeight(int height);
}
