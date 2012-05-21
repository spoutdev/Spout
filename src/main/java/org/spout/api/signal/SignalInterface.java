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

import java.lang.reflect.Method;

public interface SignalInterface {
	
	/**
	 * Subscribes the given receiver to the given signal. The given method will be called whenever the signal is emitted
	 * @param signal the signal to subscribe to
	 * @param receiver the subscriber
	 * @param method the method to call when the signal is emitted
	 */
	public boolean subscribe(String signal, Object receiver, Method method);
	
	/**
	 * Subscribes the given receiver to the given signal. The given method will be called whenever the signal is emitted
	 * @param signal the signal to subscribe to
	 * @param receiver the subscriber
	 * @param method the method to call when the signal is emitted
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	public boolean subscribe(String signal, Object receiver, String method) throws SecurityException, NoSuchMethodException;
	
	/**
	 * Unsubscribes the given receiver from the given signal
	 * @param signal the signal to unsubscribe from
	 * @param receiver the subscriber
	 */
	public void unsubscribe(String signal, Object receiver);
	
	/**
	 * Unsubscribes the given receiver from all subscribed signals
	 * @param receiver the subscriber
	 */
	public void unsubscribe(Object receiver);
}
