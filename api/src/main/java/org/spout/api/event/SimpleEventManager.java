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
package org.spout.api.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.spout.api.Spout;
import org.spout.api.exception.EventException;
import org.spout.api.exception.IllegalPluginAccessException;

/**
 * A simple implementation of the {@link EventManager} that handles all {@link Event}s for the engine.
 */
public class SimpleEventManager implements EventManager {
	private static final Logger LOGGER = Logger.getLogger(SimpleEventManager.class.getCanonicalName());

	@Override
	public <T extends Event> void callDelayedEvent(final T event) {
		Spout.getEngine().getScheduler().scheduleSyncDelayedTask(null, new Runnable() {
			@Override
			public void run() {
				callEvent(event);
			}
		});
	}

	@Override
	public <T extends Event> T callEvent(T event) {
		HandlerList handlers = event.getHandlers();
		ListenerRegistration[] listeners = handlers.getRegisteredListeners();

		if (listeners != null) {
			for (ListenerRegistration listener : listeners) {
				try {
					if (!event.isCancelled() || listener.getOrder().ignoresCancelled()) {
						listener.getExecutor().execute(event);
					}
				} catch (Throwable ex) {
					LOGGER.log(Level.SEVERE, "Could not pass event " + event.getEventName() + " to " + listener.getOwner().getClass().getName(), ex);
				}
			}
			event.setHasBeenCalled(true);
		}
		return event;
	}

	@Override
	public void registerEvents(Listener listener, Object owner) {
		for (Map.Entry<Class<? extends Event>, Set<ListenerRegistration>> entry : createRegisteredListeners(listener, owner).entrySet()) {
			Class<? extends Event> delegatedClass = getRegistrationClass(entry.getKey());
			if (!entry.getKey().equals(delegatedClass)) {
				LOGGER.severe("Plugin attempted to register delegated event class " + entry.getKey() + ". It should be using " + delegatedClass + "!");
				continue;
			}
			getEventListeners(delegatedClass).registerAll(entry.getValue());
		}
	}

	@Override
	public void registerEvent(Class<? extends Event> event, Order priority, EventExecutor executor, Object owner) {
		getEventListeners(event).register(new ListenerRegistration(executor, priority, owner));
	}

	/**
	 * Returns the specified event type's HandlerList
	 *
	 * @param type EventType to lookup
	 * @return HandlerList The list of registered handlers for the event.
	 */
	private HandlerList getEventListeners(Class<? extends Event> type) {
		try {
			Method method = getRegistrationClass(type).getDeclaredMethod("getHandlerList");
			method.setAccessible(true);
			return (HandlerList) method.invoke(null);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new IllegalPluginAccessException(e.toString());
		}
	}

	private Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) {
		try {
			clazz.getDeclaredMethod("getHandlerList");
			return clazz;
		} catch (NoSuchMethodException e) {
			if (clazz.getSuperclass() == null || clazz.getSuperclass().equals(Event.class) || !Event.class.isAssignableFrom(clazz.getSuperclass())) {
				throw new IllegalPluginAccessException("Unable to find handler list for event " + clazz.getName());
			}

			return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
		}
	}

	public Map<Class<? extends Event>, Set<ListenerRegistration>> createRegisteredListeners(final Listener listener, Object plugin) {
		Map<Class<? extends Event>, Set<ListenerRegistration>> ret = new HashMap<>();
		List<Method> methods = new ArrayList<>();
		Class<?> listenerClass = listener.getClass();
		while (listenerClass != null && !listenerClass.equals(Object.class) && !listenerClass.equals(Listener.class)) {
			try {
				methods.addAll(Arrays.asList(listenerClass.getDeclaredMethods()));
			} catch (NoClassDefFoundError e) {
				LOGGER.severe("Plugin " + plugin.getClass().getSimpleName() + " is attempting to register event " + e.getMessage() + ", which does not exist. Ignoring events registered in " + listenerClass);
				return ret;
			}
			listenerClass = listenerClass.getSuperclass();
		}
		for (final Method method : methods) {
			final EventHandler eh = method.getAnnotation(EventHandler.class);
			if (eh == null) {
				continue;
			}

			if (method.getParameterTypes().length < 1) {
				LOGGER.severe("No method arguments used for event type registered");
				continue;
			}
			final Class<?> checkClass = method.getParameterTypes()[0];
			Class<? extends Event> eventClass;
			if (!Event.class.isAssignableFrom(checkClass) || method.getParameterTypes().length != 1) {
				LOGGER.severe("Wrong method arguments used for event type registered");
				continue;
			}

			eventClass = checkClass.asSubclass(Event.class);

			method.setAccessible(true);
			Set<ListenerRegistration> eventSet = ret.get(eventClass);
			if (eventSet == null) {
				eventSet = new HashSet<>();
				ret.put(eventClass, eventSet);
			}
			eventSet.add(new ListenerRegistration(new EventExecutor() {
				@Override
				public void execute(Event event) throws EventException {
					try {
						if (!checkClass.isAssignableFrom(event.getClass())) {
							throw new EventException("Wrong event type passed to registered method");
						}
						method.invoke(listener, event);
					} catch (InvocationTargetException e) {
						if (e.getCause() instanceof EventException) {
							throw (EventException) e.getCause();
						}

						throw new EventException(e.getCause());
					} catch (Throwable t) {
						throw new EventException(t);
					}
				}
			}, eh.order(), plugin));
		}
		return ret;
	}
}
