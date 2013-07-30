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
package org.spout.api.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.component.widget.ControlComponent;
import org.spout.api.math.IntVector2;
import org.spout.api.math.Rectangle;
import org.spout.api.plugin.Plugin;
import org.spout.api.tickable.BasicTickable;
import org.spout.math.vector.Vector2;

public class Screen extends BasicTickable implements Container {
	private final HashMap<Widget, Plugin> widgets = new LinkedHashMap<>();
	private Widget focusedWidget = null;
	private boolean takesInput = true;
	private boolean grabsMouse = true;

	@Override
	public List<Widget> getWidgets() {
		return Collections.unmodifiableList(new ArrayList<>(widgets.keySet()));
	}

	@Override
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

	@Override
	public Widget getWidgetAt(IntVector2 pos) {
		return getWidgetAt(pos.getX(), pos.getY());
	}

	private int toPixelsX(float pcent) {
		return (int) (pcent * ((Client) Spout.getEngine()).getResolution().getX());
	}

	private int toPixelsY(float pcent) {
		return (int) (pcent * ((Client) Spout.getEngine()).getResolution().getY());
	}

	@Override
	public void attachWidget(Plugin plugin, Widget widget) {
		widget.setScreen(this);
		synchronized (widgets) {
			focusedWidget = widget; // newly attached widgets gets the focus
			widgets.put(widget, plugin);
		}
	}

	@Override
	public void removeWidget(Widget widget) {
		synchronized (widgets) {
			widgets.remove(widget);
		}
		cleanupWidget(widget);
	}

	@Override
	public void removeWidgets(Widget... widgets) {
		for (Widget widget : widgets) {
			removeWidget(widget);
		}
	}

	@Override
	public void removeWidgets() {
		synchronized (widgets) {
			Iterator<Widget> i = widgets.keySet().iterator();
			while (i.hasNext()) {
				cleanupWidget(i.next());
				i.remove();
			}
		}
	}

	private void cleanupWidget(Widget widget) {
		widget.setScreen(null);
		if (widget == focusedWidget) {
			focusedWidget = null;
			widget.onBlur();
		}
	}

	@Override
	public void removeWidgets(Plugin plugin) {
		Iterator<Widget> i = getWidgets().iterator();
		while (i.hasNext()) {
			Widget widget = i.next();
			synchronized (widgets) {
				if (widgets.get(widget).equals(plugin)) {
					i.remove();
					cleanupWidget(widget);
				}
			}
		}
	}

	@Override
	public void onTick(float dt) {
		synchronized (widgets) {
			for (Widget w : widgets.keySet()) {
				w.tick(dt);
			}
		}
	}

	@Override
	public boolean canTick() {
		return true;
	}

	/**
	 * Returns the current widget that has focus.
	 *
	 * @return widget with focus
	 */
	public Widget getFocusedWidget() {
		return focusedWidget;
	}

	/**
	 * Sets the focus of the screen.
	 *
	 * @param newFocus widget to focus
	 */
	public void setFocus(Widget newFocus) {
		setFocus(newFocus, FocusReason.PROGRAMMED);
	}

	/**
	 * Sets the focus of the screen.
	 *
	 * @param newFocus focus to set
	 * @param reason for focusing
	 */
	public void setFocus(Widget newFocus, FocusReason reason) {
		if (newFocus == null) {
			throw new IllegalArgumentException("You cannot set the focus to null.");
		}

		final boolean containsFocussedWidget;
		synchronized (widgets) {
			containsFocussedWidget = widgets.containsKey(newFocus);
		}

		if (containsFocussedWidget && newFocus.canFocus()) {
			if (focusedWidget != newFocus) {
				Widget oldFocus = focusedWidget;
				focusedWidget = newFocus;
				if (oldFocus != null) {
					oldFocus.onBlur();
				}
				newFocus.onFocus(reason);
			}
		}
	}

	/**
	 * Shifts the focus to the next element.
	 *
	 * @param reason for shift
	 */
	public void nextFocus(FocusReason reason) {
		int current = 0;
		if (getFocusedWidget() != null) {
			current = getFocusedWidget().get(ControlComponent.class).getTabIndex();
		}

		Widget lowest = null;
		int lowestTab = Integer.MAX_VALUE;
		synchronized (widgets) {
			for (Widget w : widgets.keySet()) {
				if (w.get(ControlComponent.class) != null) {
					int ti = w.get(ControlComponent.class).getTabIndex();
					if (ti < lowestTab && ti > current) {
						lowest = w;
						lowestTab = ti;
					}
				}
			}
		}
		setFocus(lowest, reason);
	}

	/**
	 * Shifts the focus to the previous element.
	 *
	 * @param reason for shift
	 */
	public void previousFocus(FocusReason reason) {
		int current = 0;
		if (getFocusedWidget() != null) {
			current = getFocusedWidget().get(ControlComponent.class).getTabIndex();
		}

		Widget highest = null;
		int highestTab = Integer.MIN_VALUE;
		synchronized (widgets) {
			for (Widget w : widgets.keySet()) {
				if (w.get(ControlComponent.class) != null) {
					int ti = w.get(ControlComponent.class).getTabIndex();
					if (ti > highestTab && ti < current) {
						highest = w;
						highestTab = ti;
					}
				}
			}
		}
		setFocus(highest, reason);
	}

	/**
	 * Returns true if this screen grabs the mouse when it's on the top.
	 *
	 * @return if screen should grab the mouse
	 */
	public boolean grabsMouse() {
		return grabsMouse;
	}

	/**
	 * Sets if the mouse should be grabbed when this screen is on top.
	 *
	 * @param grabsMouse true if should grab mouse.
	 */
	public void setGrabsMouse(boolean grabsMouse) {
		this.grabsMouse = grabsMouse;
	}

	/**
	 * Returns true if this screen receives input.
	 *
	 * @return if this screen should receive mouse and keyboard input. Default is true
	 */
	public boolean takesInput() {
		return takesInput;
	}

	/**
	 * Sets if this screen should receive input.
	 *
	 * @param takesInput receives input
	 */
	public void setTakesInput(boolean takesInput) {
		this.takesInput = takesInput;
	}
}
