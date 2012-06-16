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

import java.awt.Point;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;
import org.spout.api.gui.Container;
import org.spout.api.gui.Control;
import org.spout.api.gui.Layout;
import org.spout.api.gui.MouseButton;
import org.spout.api.gui.MouseEventHandler;
import org.spout.api.gui.Widget;
import org.spout.api.tickable.Tickable;

public abstract class AbstractLayout implements Layout {
	private Container parent = null;
	protected LinkedList<Widget> attachedWidgets = new LinkedList<Widget>();

	@Override
	public Widget[] getWidgets() {
		return attachedWidgets.toArray(new Widget[0]);
	}

	@Override
	public void addWidgets(Widget... widgets) {
		for (Widget widget : widgets) {
			widget.setParent(this);
			attachedWidgets.addLast(widget);
		}
		relayout();
	}

	@Override
	public void clear() {
		for (Widget widget : attachedWidgets) {
			widget.setParent(null);
		}
		attachedWidgets.clear();
		relayout();
	}

	@Override
	public void removeWidgets(Widget... widgets) {
		for (Widget widget : widgets) {
			widget.setParent(null);
			attachedWidgets.remove(widget);
		}
		relayout();
	}

	@Override
	public void setParent(Container container) {
		parent = container;
	}

	@Override
	public Container getParent() {
		return parent;
	}

	@Override
	public void render() {
		for (Widget widget : attachedWidgets) {
			double x = widget.getGeometry().getX();
			double y = widget.getGeometry().getY();
			GL11.glTranslated(x, y, 0);
			GL11.glColor4d(1, 1, 1, 1);
			GL11.glPushMatrix();
			widget.render();
			GL11.glPopMatrix();
			GL11.glTranslated(-x, -y, 0);
		}
	}

	@Override
	public void onMouseDown(Point position, MouseButton button) {
		Widget clicked = getWidgetAt(position);
		if (canReceiveClick(clicked)) {
			((MouseEventHandler) clicked).onMouseDown(position, button);
		}
	}

	@Override
	public void onMouseMove(Point from, Point to) {
		Widget movedTo = getWidgetAt(to);
		if (canReceiveClick(movedTo)) {
			((MouseEventHandler) movedTo).onMouseMove(from, to);
		}
		Widget movedFrom = getWidgetAt(to);
		if (canReceiveClick(movedFrom)) {
			((MouseEventHandler) movedFrom).onMouseMove(from, to);
		}
	}

	@Override
	public void onMouseUp(Point position, MouseButton button) {
		Widget clicked = getWidgetAt(position);
		if (canReceiveClick(clicked)) {
			((MouseEventHandler) clicked).onMouseUp(position, button);
		}
	}
	
	protected static boolean canReceiveClick(Widget w) {
		if (w instanceof Control) {
			return ((Control) w).isEnabled();
		} else if (w instanceof MouseEventHandler) {
			return true;
		}
		return false;
	}
	
	public Widget getWidgetAt(Point position) {
		for (Widget widget : attachedWidgets) {
			if (widget.getGeometry().contains(position)) {
				return widget;
			}
		}
		return null;
	}

	@Override
	public void onTick(float dt) {
		for (Widget widget : attachedWidgets) {
			widget.onTick(dt);
		}
	}
}
