/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.guix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.exception.SpoutRuntimeException;
import org.spout.api.math.IntVector2;
import org.spout.api.math.Rectangle;
import org.spout.api.math.Vector2;
import org.spout.api.tickable.BasicTickable;

/**
 * Represents a layer on the GUI and a collection of {@link Widget}s.
 */
public class Screen extends BasicTickable {
	private final List<Widget> widgets = new ArrayList<Widget>();
	private int focusIndex = 0;
	private boolean takesInput = true, visible = true, grabsMouse = true;

	/**
	 * Returns all widgets attached to this screen.
	 *
	 * @return widgets
	 */
	public List<Widget> getWidgets() {
		return Collections.unmodifiableList(widgets);
	}

	/**
	 * Returns the widget at the specified position.
	 *
	 * @param x position
	 * @param y position
	 * @return widget
	 */
	public Widget getWidgetAt(int x, int y) {
		for (Widget w : getWidgets()) {
			Rectangle bounds = w.getBounds();
			Vector2 res = ((Client) Spout.getEngine()).getResolution();
			int startX = (int) (res.getX() / 2) + (toPixelsX(bounds.getX()) / 2);
			int startY = (int) (res.getY() / 2) + (toPixelsY(bounds.getY()) / 2);
			int endX = startX + toPixelsX(bounds.getWidth() / 2);
			int endY = startY + toPixelsY(bounds.getHeight() / 2);
			if (x >= startX && x <= endX && y >= startY && y <= endY) {
				return w;
			}
		}
		return null;
	}

	/**
	 * Returns the widget at the specified position.
	 *
	 * @param pos position
	 * @return widget
	 */
	public Widget getWidgetAt(IntVector2 pos) {
		return getWidgetAt(pos.getX(), pos.getY());
	}

	private int toPixelsX(float pcent) {
		return (int) (pcent * ((Client) Spout.getEngine()).getResolution().getX());
	}

	private int toPixelsY(float pcent) {
		return (int) (pcent * ((Client) Spout.getEngine()).getResolution().getY());
	}

	/**
	 * Attaches a widget to this screen.
	 *
	 * @param widget to attach
	 */
	public void attach(Widget widget) {
		if (widget.screen != null)
			throw new IllegalArgumentException("This widget is already attached to a screen.");
		widget.screen = this;
		widgets.add(widget);
	}

	/**
	 * Detaches the specified widget from this screen.
	 *
	 * @param widget to detach
	 */
	public void detach(Widget widget) {
		dispose(widget);
		widgets.add(widget);
	}

	/**
	 * Detaches all widgets from the screen.
	 */
	public void detachAll() {
		for (Widget widget : widgets) {
			dispose(widget);
		}
		widgets.clear();
	}

	private void dispose(Widget widget) {
		if (!equals(widget))
			throw new IllegalArgumentException("This widget is not attached to this screen");
		widget.screen = null;
	}

	/**
	 * Returns the index of focus.
	 *
	 * @return focus index
	 */
	public int getFocusIndex() {
		return focusIndex;
	}

	/**
	 * Returns the focused widget.
	 *
	 * @return widget
	 */
	public Widget getFocusedWidget() {
		if (focusIndex >= widgets.size() || focusIndex < 0)
			throw new IllegalStateException("Focus index points to non-existent widget. (" + focusIndex + ")");
		return widgets.get(focusIndex);
	}

	/**
	 * Sets the focus on the screen.
	 *
	 * @param widget to focus
	 */
	public void setFocus(Widget widget) {
		setFocus(widgets.indexOf(widget));
	}

	/**
	 * Sets the focus index.
	 *
	 * @param index of focus.
	 */
	public void setFocus(int index) {
		if (index >= widgets.size() || index < 0)
			throw new IllegalArgumentException("Specified index is not within the bounds of attached widgets.");
		widgets.get(this.focusIndex).blur();
		this.focusIndex = index;
		widgets.get(index).focus();
	}

	/**
	 * Shifts the focus to the next widget.
	 */
	public void nextFocus() {
		if (focusIndex == widgets.size() - 1) {
			setFocus(0);
			return;
		}
		setFocus(focusIndex + 1);
	}

	/**
	 * Shifts the focus to the previous focus.
	 */
	public void previousFocus() {
		if (focusIndex == 0) {
			setFocus(widgets.size() - 1);
			return;
		}
		setFocus(focusIndex - 1);
	}

	/**
	 * Returns true if input should be passed to this screen.
	 *
	 * @return true if takes input
	 */
	public boolean takesInput() {
		return takesInput;
	}

	/**
	 * Sets if input should be passed to this screen.
	 *
	 * @param takesInput if should take input
	 */
	public void setTakesInput(boolean takesInput) {
		this.takesInput = takesInput;
	}

	/**
	 * Returns true if this screen should be rendered.
	 *
	 * @return true if visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Sets if this screen is visible
	 *
	 * @param visible true if should render
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Returns true if this screen should grab the mouse.
	 *
	 * @return true if should grab mouse
	 */
	public boolean grabsMouse() {
		return grabsMouse;
	}

	/**
	 * Sets if this screen should grab the mouse.
	 *
	 * @param grabsMouse true if should grab mouse
	 */
	public void setGrabsMouse(boolean grabsMouse) {
		this.grabsMouse = grabsMouse;
	}

	@Override
	public synchronized void onTick(float dt) {
		for (Widget widget : widgets) {
			try {
				widget.tick(dt);
			} catch (Exception e) {
				throw new SpoutRuntimeException("Error updating Widget on Screen on the ScreenStack.", e);
			}
		}
	}

	@Override
	public boolean canTick() {
		return !widgets.isEmpty();
	}
}
