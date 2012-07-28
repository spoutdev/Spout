/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.util;

import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Mac OS X-specific utility functions. If these are invoked when Mac OS X is not the active OS, they will do nothing (or return null)
 */
public class MacOSXUtils {

	private static final boolean isOSX;
	private static final int osVersion;
	static {
		isOSX = System.getProperty("os.name").toLowerCase().contains("mac");
		if (isOSX) {
			String[] elements = System.getProperty("os.version").split("\\.");
			int primaryVersion = Integer.parseInt(elements[0]), secondaryVersion = Integer.parseInt(elements[1]);
			osVersion = primaryVersion < 10 ? -1 : secondaryVersion;
		} else {
			osVersion = -1;
		}
	}

	public static boolean isMac() {
		return isOSX;
	}

	public static int getOSXVersion() {
		return osVersion;
	}

	private static final Method fullScreenUtilities_setWindowCanFullScreenMethod;
	static {
		Method m = null;
		try {
			Class<?> clazz = Class.forName("com.apple.eawt.FullScreenUtilities");
			try {
				m = clazz.getMethod("setWindowCanFullScreen", Window.class, boolean.class);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		fullScreenUtilities_setWindowCanFullScreenMethod = m;
	}

	public static void FullScreenUtilities_setWindowCanFullScreen(Window window, boolean val) {
		if (fullScreenUtilities_setWindowCanFullScreenMethod != null) {
			try {
				fullScreenUtilities_setWindowCanFullScreenMethod.invoke(null, window, val);
			} catch (IllegalAccessException ignore) {
				ignore.printStackTrace();
			} catch (InvocationTargetException ignore) {
				ignore.printStackTrace();
			}
		}
	}

	private static final Object application;

	static {
		Object app = null;
		try {
			Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
			try {
				app = applicationClass.getMethod("getApplication").invoke(null);
			} catch (IllegalAccessException ignore) {
			} catch (InvocationTargetException ignore) {
			} catch (NoSuchMethodException ignore) {
			}
		} catch (ClassNotFoundException ignore) {
		}
		application = app;
	}

	public static Object Application_getApplication() {
		return application;
	}


}
