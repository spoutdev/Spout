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

import org.spout.api.gui.Widget;
import org.spout.api.gui.WidgetType;

public class RadioButton extends AbstractButton {
	private int group = 0;

	public RadioButton() {
		this("");
	}

	public RadioButton(String text) {
		super(text);
		setCheckable(true);
	}
	
	/**
	 * Sets the radio button group of this radio button.
	 * Only one radio button in the same group can be checked.
	 * Radio buttons in other layouts won't be taken into consideration while calculating
	 * @param group the group of this radio button
	 * @return the instance
	 */
	public RadioButton setGroup(int group) {
		this.group = group;
		return this;
	}
	
	/**
	 * Gets the group
	 * @see setGroup(int)
	 * @return
	 */
	public int getGroup() {
		return group;
	}

	@Override
	public Button setChecked(boolean check) {
		if (check) {
			for (Widget widget:getParent().getWidgets()) {
				if (widget instanceof RadioButton) {
					RadioButton r = (RadioButton) widget;
					if (r.getGroup() == getGroup()) {
						r.setChecked(false);
					}
				}
			}
		}
		return super.setChecked(check);
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

	@Override
	public WidgetType getWidgetType() {
		return WidgetType.RADIOBUTTON;
	}

}
