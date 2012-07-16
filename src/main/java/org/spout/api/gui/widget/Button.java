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
package org.spout.api.gui.widget;

import org.spout.api.gui.Control;

public interface Button extends Label, Control {
	/**
	 * Gets if the button is currently pushed in by a the left mouse button or the space key
	 * @return if the button is pushed in
	 */
	public boolean isDown();

	/**
	 * Gets if the button is currently checked
	 * @return if the button is checked
	 */
	public boolean isChecked();

	/**
	 * Sets if the button is currently checked
	 * @param check
	 * @return 
	 */
	public Button setChecked(boolean check);

	/**
	 * Sets if the button can be checked or not
	 * This property is disabled for PushButtons by default
	 * @param checkable
	 */
	public Button setCheckable(boolean checkable);

	/**
	 * Gets if the button can be checked
	 * @return if the button can be checked
	 */
	public boolean isCheckable();

	/**
	 * Clicks the button. If checkable, this will toggle the checked state
	 */
	public Button click();

	/**
	 * Clicks the button through holding it down for the given ticks and releasing it after that
	 * @param ticks the time to hold it in ticks
	 */
	public Button clickLong(int ticks);
}
