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
		for(Pair<Object, Method> p:subscribes) {
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
		if(Arrays.equals(method.getParameterTypes(), argumentTypes)) {
			//TODO make sure that the same object doesn't subscribe twice or more
			subscribes.add(Pair.of(receiver, method));
		}
	}
	
	public void unsubscribe(Object receiver) {
		Iterator<Pair<Object, Method>> iter = subscribes.iterator();
		while(iter.hasNext()) {
			Pair<Object, Method> next = iter.next();
			if(next.getLeft() == receiver) {
				iter.remove();
				break;
			}
		}
	}
}
