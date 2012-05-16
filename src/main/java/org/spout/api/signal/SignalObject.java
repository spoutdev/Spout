package org.spout.api.signal;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Defines a common implementation for a SignalInterface. If you need to use this as a delegate because your class already extends something else, use SignalObjectDelegate.
 *
 */
public class SignalObject implements SignalInterface {
	
	private HashMap<String, Signal> signals = new HashMap<String, Signal>();
	
	protected void registerSignal(Signal signal) {
		signals.put(signal.getName(), signal);
	}
	
	protected void emit(String signal, Object ...arguments) {
		Signal signalO = signals.get(signal);
		if(signalO != null) {
			signalO.emit(arguments);
		}
	}
	
	protected void emit(Signal signal, Object ...arguments) {
		signal.emit(arguments);
	}

	@Override
	public boolean subscribe(String signal, Object receiver, Method method) {
		Signal signalO = signals.get(signal);
		if(signalO != null) {
			signalO.subscribe(receiver, method);
			return true;
		}
		return false;
	}

	@Override
	public boolean subscribe(String signal, Object receiver, String method) throws SecurityException, NoSuchMethodException {
		Signal signalO = signals.get(signal);
		if(signalO != null) {
			Method methodO;
			methodO = receiver.getClass().getMethod(method, signalO.getArgumentTypes());
			if(methodO != null) {
				signalO.subscribe(receiver, methodO);
				return true;
			}
		}
		return false;
	}

	@Override
	public void unsubscribe(String signal, Object receiver) {
		Signal signalO = signals.get(signal);
		if(signalO != null) {
			signalO.unsubscribe(receiver);
		}
	}

	@Override
	public void unsubscribe(Object receiver) {
		for(Signal signal:signals.values()) {
			signal.unsubscribe(receiver);
		}
	}

}
