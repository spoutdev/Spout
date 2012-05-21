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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.spout.api.generic.GenericType;
import org.spout.api.gui.widget.GenericLabel;
import org.spout.api.gui.widget.ProgressBar;
import org.spout.api.gui.widget.PushButton;
import org.spout.api.gui.widget.CheckBox;
import org.spout.api.gui.widget.RadioButton;
import org.spout.api.gui.widget.Slider;
import org.spout.api.plugin.Plugin;

public class WidgetType extends GenericType<Widget> {

	public static final WidgetType SCREEN = new WidgetType(GenericScreen.class, -1);
	public static final WidgetType LABEL = new WidgetType(GenericLabel.class, 0);
	public static final WidgetType PUSHBUTTON = new WidgetType(PushButton.class, 1);
	public static final WidgetType CHECKBOX = new WidgetType(CheckBox.class, 2);
	public static final WidgetType RADIOBUTTON = new WidgetType(RadioButton.class, 3);
	public static final WidgetType SLIDER = new WidgetType(Slider.class, 4);
	public static final WidgetType PROGRESSBAR = new WidgetType(ProgressBar.class, 5);
	
	public WidgetType(Class<? extends Widget> clazz, int id) {
		super(clazz, id);
	}
	
	/**
	 * Creates a new instance of the WidgetType. 
	 * @param plugin the plugin to pass into the widgets constructor
	 * @return a new created instance, or null if an error occured
	 */
	public Widget newInstance() {
		Class<? extends Widget> clazz = getClazz();
		Constructor<? extends Widget> constructor = null;
		try {
			constructor = clazz.getConstructor();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		}
		if (constructor != null) {
			try {
				return constructor.newInstance();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
