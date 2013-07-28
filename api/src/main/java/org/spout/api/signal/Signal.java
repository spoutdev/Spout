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
package org.spout.api.signal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class Signal {
	private Class<?> argumentTypes[];
	private String name;
	private LinkedList<Subscription> subscriptions = new LinkedList<>();
	private Iterator<Subscription> currentIterator = subscriptions.iterator();

	private static class Subscription {
		public SignalInterface sender;
		public Object receiver;
		public Method method;
	}

	public Signal(String name, Class<?>... argumentTypes) {
		this.argumentTypes = argumentTypes;
		this.name = name;
	}

	public void emit(SignalInterface sender, Object... arguments) {
		synchronized (currentIterator) {
			currentIterator = subscriptions.iterator();
			while (currentIterator.hasNext()) {
				Subscription p = currentIterator.next();
				if (p.sender != sender) {
					continue;
				}
				Object call = p.receiver;
				SubscriberInterface sub = null;
				if (call instanceof SubscriberInterface) {
					sub = (SubscriberInterface) call;
					sub.setSender(sender);
				}
				Method method = p.method;
				if (sub != null) {
					sub.setSender(null);
				}
				try {
					method.invoke(call, arguments);
				} catch (		IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				} catch (Exception e) {
					System.out.println("---------------");
					System.out.println("Error while executing subscribed method to " + this);
					System.out.println("---------------");
					e.printStackTrace();
				}
			}
		}
	}

	public Class<?>[] getArgumentTypes() {
		return argumentTypes;
	}

	public String getName() {
		return name;
	}

	public void subscribe(SignalInterface sender, Object receiver, Method method) {
		synchronized (currentIterator) {
			if (Arrays.equals(method.getParameterTypes(), argumentTypes)) {
				//TODO make sure that the same object doesn't subscribe twice or more
				Subscription sub = new Subscription();
				sub.sender = sender;
				sub.receiver = receiver;
				sub.method = method;
				subscriptions.add(sub);
			}
		}
	}

	public void unsubscribe(Object receiver) {
		synchronized (currentIterator) {
			currentIterator = subscriptions.iterator();
			while (currentIterator.hasNext()) {
				Subscription next = currentIterator.next();
				if (next.receiver == receiver) {
					currentIterator.remove();
					break;
				}
			}
		}
	}
}
