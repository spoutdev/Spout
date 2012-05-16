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
