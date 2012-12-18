/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.event;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.Test;
import org.spout.api.util.ReflectionUtils;

public class EventHandlerListTest {
	@Test
	public void test() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		List<Class<?>> classes = ReflectionUtils.getClassesForPackage("org.spout.api.event", true);
		for (Class<?> clazz : classes) {
			if (Event.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
				System.out.println("Verifying handlers in " + clazz.getSimpleName());
				
				Method m = clazz.getMethod("getHandlerList", (Class[])null);
				HandlerList list = (HandlerList) m.invoke(null, (Object[])null);
				assertTrue("Expected non null handler list", list != null);
			}
		}
	}
}
