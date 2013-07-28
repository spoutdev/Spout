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

import java.lang.reflect.Method;
import java.util.HashMap;

import org.spout.api.tickable.BasicTickable;

/**
 * Defines a common implementation for a SignalInterface. If you need to use this as a delegate because your class already extends something else, use SignalObjectDelegate.
 */
public class SignalObject extends BasicTickable implements SignalInterface {
	private HashMap<String, Signal> signals = new HashMap<>();

	protected void registerSignal(Signal signal) {
		signals.put(signal.getName(), signal);
	}

	protected void emit(String signal, Object... arguments) {
		Signal signalO = signals.get(signal);
		if (signalO != null) {
			signalO.emit(this, arguments);
		}
	}

	protected void emit(Signal signal, Object... arguments) {
		signal.emit(this, arguments);
	}

	@Override
	public boolean subscribe(String signal, Object receiver, Method method) {
		Signal signalO = signals.get(signal);
		if (signalO != null) {
			signalO.subscribe(this, receiver, method);
			return true;
		}
		return false;
	}

	@Override
	public boolean subscribe(String signal, Object receiver, String method) throws SecurityException, NoSuchMethodException {
		Signal signalO = signals.get(signal);
		if (signalO != null) {
			Method methodO;
			methodO = receiver.getClass().getMethod(method, signalO.getArgumentTypes());
			if (methodO != null) {
				signalO.subscribe(this, receiver, methodO);
				return true;
			}
		}
		return false;
	}

	@Override
	public void unsubscribe(String signal, Object receiver) {
		Signal signalO = signals.get(signal);
		if (signalO != null) {
			signalO.unsubscribe(receiver);
		}
	}

	@Override
	public void unsubscribe(Object receiver) {
		for (Signal signal : signals.values()) {
			signal.unsubscribe(receiver);
		}
	}

	@Override
	public boolean subscribe(Signal signal, Object receiver, Method method) {
		signal.subscribe(this, receiver, method);
		return true;
	}

	@Override
	public boolean subscribe(Signal signal, Object receiver, String method) throws SecurityException, NoSuchMethodException {
		Method methodO;
		methodO = receiver.getClass().getMethod(method, signal.getArgumentTypes());
		if (methodO != null) {
			signal.subscribe(this, receiver, methodO);
			return true;
		}
		return false;
	}

	@Override
	public void onTick(float dt) {
	}

	@Override
	public boolean canTick() {
		return true;
	}
}
