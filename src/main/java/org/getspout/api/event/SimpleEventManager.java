package org.getspout.api.event;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.getspout.api.Spout;
import org.getspout.api.plugin.IllegalPluginAccessException;

public class SimpleEventManager implements EventManager {

	public <T extends Event> T callEvent(T event) {
		HandlerList handlers = event.getHandlers();
		handlers.bake();
		ListenerRegistration[][] listeners = handlers.getRegisteredListeners();

		if (listeners != null) {
			for (ListenerRegistration[] listener : listeners) {
				for (ListenerRegistration registration : listener) {
					try {
						if (!event.isCancelled() || registration.getOrder().ignoresCancelled()) {
							registration.getExecutor().execute(event);
						}
					} catch (Throwable ex) {
						Spout.getGame().getLogger().log(Level.SEVERE, "Could not pass event " + event.getEventName() + " to " + registration.getOwner().getClass().getName(), ex);
					}
				}
			}
		}
		return event;
	}

	public void registerEvents(Listener listener, Object owner) {
		for (Map.Entry<Class<? extends Event>, Set<ListenerRegistration>> entry : createRegisteredListeners(listener, owner).entrySet()) {
			Class<? extends Event> delegatedClass = getRegistrationClass(entry.getKey());
			if (!entry.getKey().equals(delegatedClass)) {
				Spout.getGame().getLogger().severe("Plugin attempted to register delegated event class " + entry.getKey() + ". It should be using " + delegatedClass + "!");
				continue;
			}
			getEventListeners(delegatedClass).registerAll(entry.getValue());
		}
	}

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
		} catch (Exception e) {
			throw new IllegalPluginAccessException(e.toString());
		}
	}

	private Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) {
		try {
			clazz.getDeclaredMethod("getHandlerList");
			return clazz;
		} catch (NoSuchMethodException e) {
			if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Event.class) && clazz.getSuperclass().isAssignableFrom(Event.class)) {
				return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
			} else {
				throw new IllegalPluginAccessException("Unable to find handler list for event " + clazz.getName());
			}
		}
	}

	public Map<Class<? extends Event>, Set<ListenerRegistration>> createRegisteredListeners(final Listener listener, Object plugin) {
		Map<Class<? extends Event>, Set<ListenerRegistration>> ret = new HashMap<Class<? extends Event>, Set<ListenerRegistration>>();
		Method[] methods;
		try {
			methods = listener.getClass().getDeclaredMethods();
		} catch (NoClassDefFoundError e) {
			Spout.getGame().getLogger().severe("Plugin " + plugin.getClass().getSimpleName() + " is attempting to register event " + e.getMessage() + ", which does not exist. Ignoring events registered in " + listener.getClass());
			return ret;
		}
		for (final Method method : methods) {
			final EventHandler eh = method.getAnnotation(EventHandler.class);
			if (eh == null) {
				continue;
			}
			final Class<?> checkClass = method.getParameterTypes()[0];
			if (!checkClass.isAssignableFrom(eh.event()) || method.getParameterTypes().length != 1) {
				Spout.getGame().getLogger().severe("Wrong method arguments used for event type registered");
				continue;
			}
			method.setAccessible(true);
			Set<ListenerRegistration> eventSet = ret.get(eh.event());
			if (eventSet == null) {
				eventSet = new HashSet<ListenerRegistration>();
				ret.put(eh.event(), eventSet);
			}
			eventSet.add(new ListenerRegistration(new EventExecutor() {

				public void execute(Event event) throws EventException {
					try {
						if (!checkClass.isAssignableFrom(event.getClass())) {
							throw new EventException("Wrong event type passed to registered method");
						}
						method.invoke(listener, event);
					} catch (Throwable t) {
						throw new EventException(t);
					}
				}

			}, eh.priority(), plugin));
		}
		return ret;
	}

}
