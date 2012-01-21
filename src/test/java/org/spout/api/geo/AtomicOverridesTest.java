/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */

package org.spout.api.geo;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javassist.Modifier;

import org.junit.Test;
import org.spout.api.geo.discrete.atomic.AtomicPoint;
import org.spout.api.geo.discrete.atomic.AtomicVector3;

public class AtomicOverridesTest {

	@Test
	public void testOverride() {

		assertTrue(fullyOverridesSuperclass(AtomicVector3.class));
		assertTrue(fullyOverridesSuperclass(AtomicPoint.class));
		
	}
	
	public static boolean fullyOverridesSuperclass(Class<?> subClass) {

		Class<?> superClass = subClass.getSuperclass();
		
		List<Method> subClassMethods = getClassMethods(subClass);
		List<Method> superClassMethods = getAllMethods(superClass);

		Iterator<Method> i = superClassMethods.iterator();
		while (i.hasNext()) {
			Method m = i.next();
			Class<?>[] superArgs = m.getParameterTypes();
			boolean match = false;
			for (Method sm : subClassMethods) {
				if (m.getName().equals(sm.getName())) {
					Class<?>[] subArgs = m.getParameterTypes();
					if (subArgs.length != superArgs.length) {
						continue;
					}
					match = true;
					for (int x = 0; x < subArgs.length; x++) {
						if (!subArgs[x].equals(superArgs[x])) {
							match = false;
							break;
						}
					}
					if (match) {
						i.remove();
						break;
					}
				}
			}
		}
		
		if (superClassMethods.size() > 0) {
			System.out.println(subClass.getSimpleName() + " doesn't completely override " + superClass.getSimpleName() + "\n");
			System.out.println(collectionToString(superClassMethods, "\n"));
			return false;
		} else {
			System.out.println(subClass.getName() + " completely overrides " + superClass.getName());
			return true;
		}
	}
	
	public static List<Method> getClassMethods(Class<?> clazz) {
		
		Method[] methods = clazz.getMethods();
		
		ArrayList<Method> allMethods = new ArrayList<Method>();
		
		for (Method m : methods) {
			if (m.getDeclaringClass().equals(clazz)) {
				if (!Modifier.isStatic(m.getModifiers())) {
					if (!Modifier.isFinal(m.getModifiers())) {
						allMethods.add(m);
					} else {
						System.out.println(m + " is a final method");
					}
				} else {
					System.out.println(m + " is a static method");
				}
			}
		}
		
		return allMethods;
		
	}
	
	public static List<Method> getAllMethods(Class<?> clazz) {
		
		Method[] methods = clazz.getMethods();
		
		ArrayList<Method> allMethods = new ArrayList<Method>();
		
		for (Method m : methods) {
			if (!Modifier.isStatic(m.getModifiers())) {
				if (!Modifier.isFinal(m.getModifiers())) {
					allMethods.add(m);
				} else {
					System.out.println(m + " is a final method");
				}
			} else {
				System.out.println(m + " is a static method");
			}
		}
		
		return allMethods;
		
	}
	
	public static String collectionToString(Collection<?> c, String spacer) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Object o : c) {
			if (!first) {
				sb.append(spacer);
			} else {
				first = false;
			}
			sb.append(o);
		}
		return sb.toString();
	}

}
