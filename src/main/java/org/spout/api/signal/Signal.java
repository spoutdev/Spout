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
package org.spout.api.signal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.lang3.tuple.Pair;

public class Signal {
	private Class<?> argumentTypes[];
	private String name;
	private LinkedList<Pair<Object, Method>> subscribes = new LinkedList<Pair<Object,Method>>();
	
	public Signal(String name, Class<?> ...argumentTypes) {
		this.argumentTypes = argumentTypes;
		this.name = name;
	}
	
	public void emit(Object ...arguments) {
		for (Pair<Object, Method> p:subscribes) {
			Object call = p.getLeft();
			Method method = p.getRight();
			try {
				method.invoke(call, arguments);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public Class<?>[] getArgumentTypes() {
		return argumentTypes;
	}

	public String getName() {
		return name;
	}
	
	public void subscribe(Object receiver, Method method) {
		if (Arrays.equals(method.getParameterTypes(), argumentTypes)) {
			//TODO make sure that the same object doesn't subscribe twice or more
			subscribes.add(Pair.of(receiver, method));
		}
	}
	
	public void unsubscribe(Object receiver) {
		Iterator<Pair<Object, Method>> iter = subscribes.iterator();
		while (iter.hasNext()) {
			Pair<Object, Method> next = iter.next();
			if (next.getLeft() == receiver) {
				iter.remove();
				break;
			}
		}
	}
}
