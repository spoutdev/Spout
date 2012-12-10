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
package org.spout.api.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.spout.api.component.Component;
import org.spout.api.gui.component.ControlComponent;
import org.spout.api.plugin.Plugin;
import org.spout.api.tickable.BasicTickable;

public class Screen extends BasicTickable implements Container {
	private HashMap<Widget, Plugin> widgets = new LinkedHashMap<Widget, Plugin>();
	private Widget focussedWidget = null;
	private boolean takesInput = true;
	private boolean grabsMouse = true;
	private int focus = 0;

	@Override
	public List<Widget> getWidgets() {
		return Collections.unmodifiableList(new ArrayList<Widget>(widgets.keySet()));
	}

	@Override
	public void attachWidget(Plugin plugin, Widget widget) {
		widget.setScreen(this);
		widgets.put(widget, plugin);
	}

	@Override
	public void removeWidget(Widget widget) {
		widget.setScreen(null);
		widgets.remove(widget);
	}

	@Override
	public void removeWidgets(Widget... widgets) {
		for (Widget widget : widgets) {
			removeWidget(widget);
		}
	}

	@Override
	public void removeWidgets() {
		Iterator<Widget> i = getWidgets().iterator();
		while (i.hasNext()) {
			i.next().setScreen(null);
			i.remove();
		}
	}

	@Override
	public void removeWidgets(Plugin plugin) {
		Iterator<Widget> i = getWidgets().iterator();
		while (i.hasNext()) {
			Widget widget = i.next();
			if (widgets.get(widget).equals(plugin)) {
				widget.setScreen(null);
				i.remove();
			}
		}
	}

	@Override
	public void onTick(float dt) {
		for (Widget w : widgets.keySet()) {
			w.tick(dt);
		}
	}

	@Override
	public boolean canTick() {
		return true;
	}

	public Widget getFocusedWidget() {
		return getWidgets().get(focus);
	}

	public void setFocus(int focus, FocusReason reason) {
		System.out.println("Setting focus");
		// Focus hasn't changed
		if (this.focus == focus) {
			return;
		}

		// Make sure the focus is within range
		List<Widget> widgets = getWidgets();
		int size = widgets.size();
		if (focus < 0 || focus >= size) {
			throw new IllegalArgumentException("Focus must be between 0 and " + (size - 1));
		}

		// Verify the new focus has a ControlComponent
		if (!widgets.get(focus).canFocus()) {
			throw new IllegalStateException("Can only focus controls, add a ControlComponent to your widget!");
		}

		System.out.println("Focus: " + focus);

		// Notify old widget of lost focus; notify new widget with focus gained
		getFocusedWidget().onFocusLost();
		this.focus = focus;
		getFocusedWidget().onFocus(reason);

	}

	public void setFocus(int focus) {
		setFocus(focus, FocusReason.PROGRAMMED);
	}

	public void setFocus(Widget widget, FocusReason reason) {
		List<Widget> widgets = getWidgets();
		if (!widgets.contains(widget)) {
			throw new IllegalArgumentException("Cannot focus Widget on Screen because specified Widget is not attached.");
		}
		setFocus(widgets.indexOf(widget), reason);
	}

	public void setFocus(Widget widget) {
		setFocus(widget, FocusReason.PROGRAMMED);
	}

	public void nextFocus(FocusReason reason) {
		List<Widget> widgets = getWidgets();
		int size = widgets.size();
		int newFocus = focus + 1;
		while (newFocus != focus) {
			if (newFocus >= size) {
				newFocus = 0;
			}
			Widget widget = widgets.get(newFocus);
			if (widget.canFocus()) {
				setFocus(newFocus, reason);
				break;
			}
			newFocus++;
		}
	}

	public void nextFocus() {
		nextFocus(FocusReason.PROGRAMMED);
	}

	public void previousFocus(FocusReason reason) {
		List<Widget> widgets = getWidgets();
		int size = widgets.size();
		int newFocus = focus - 1;
		while (newFocus != focus) {
			if (newFocus < 0) {
				newFocus = size - 1;
			}
			Widget widget = widgets.get(newFocus);
			if (widget.canFocus()) {
				setFocus(newFocus, reason);
				break;
			}
			newFocus--;
		}
	}

	public void previousFocus() {
		previousFocus(FocusReason.PROGRAMMED);
	}

	public boolean grabsMouse() {
		return grabsMouse;
	}

	public void setGrabsMouse(boolean grabsMouse) {
		this.grabsMouse = grabsMouse;
	}

	/**
	 * @returns if this screen should receive mouse and keyboard input. Default is true
	 */
	public boolean takesInput() {
		return takesInput;
	}

	public void setTakesInput(boolean takesInput) {
		this.takesInput = takesInput;
	}
}
