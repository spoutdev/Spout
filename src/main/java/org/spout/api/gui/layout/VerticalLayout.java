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
package org.spout.api.gui.layout;

import java.awt.Dimension;
import java.awt.Rectangle;

import org.spout.api.gui.LayoutType;
import org.spout.api.gui.Widget;

public class VerticalLayout extends ManagedLayout {

	@Override
	public void relayout() {
		//Calculate minimum size
		
		//TODO add paddings when the attribute system is in
		int minimumHeight = getMargin().getTop();
		int minimumWidth = getMargin().getLeft();
		for (Widget w:getWidgets()) {
			if (w.getMinimumSize() != null) {
				minimumHeight += w.getMinimumSize().getHeight();
				minimumWidth = (int) Math.max(minimumWidth, w.getMinimumSize().getWidth());
			}
		}
		minimumHeight += getMargin().getBottom();
		minimumWidth += getMargin().getRight();
		Rectangle size = new Rectangle(new Dimension(minimumWidth, minimumHeight));
		if (!size.equals(getParent().getMinimumSize())) {
			getParent().setMinimumSize(size);
		}
		
		int numWidgets = getWidgets().length;
		int height = getParent().getGeometry().height - getMargin().getTop() - getMargin().getBottom();
		int width = getParent().getGeometry().width - getMargin().getLeft() - getMargin().getRight();
		int remainingHeight = height;
		int heightPerWidget;
		if (height < minimumHeight || width < minimumWidth) {
			//TODO handle exception
			System.out.println("Not enough space");
			return;
		}
		int x = getMargin().getLeft();
		int y = getMargin().getRight();
		int handledWidgets = 0;
		
		//Calculate the actual layout
		for (Widget w:getWidgets()) {
			heightPerWidget = remainingHeight / (numWidgets - handledWidgets);
			
			Rectangle geometry = new Rectangle();
			geometry.x = x;
			geometry.y = y;
			if (w.getMaximumSize().width < width) {
				geometry.width = w.getMaximumSize().width;
			} else {
				geometry.width = width;
			}
			if (w.getMaximumSize().height < heightPerWidget) {
				geometry.height = w.getMaximumSize().height;
			} else {
				geometry.height = heightPerWidget;
			}
			w.setGeometry(geometry);
			
			remainingHeight -= geometry.height;
			x += geometry.height;
			
			handledWidgets ++;
		}
	}

	@Override
	public LayoutType getLayoutType() {
		return LayoutType.VERTICALLAYOUT;
	}

}
