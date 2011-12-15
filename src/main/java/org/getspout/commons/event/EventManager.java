package org.getspout.commons.event;

import org.getspout.commons.plugin.Plugin;

public interface EventManager {
	/**
	 * Calls an event with the given details
	 *
	 * @param event Event details
	 * @return Called event
	 */
	public <T extends Event> T callEvent(T event);

	/**
	 * Registers all the events in the given listener class
	 *
	 * @param listener Listener to register
	 * @param owner	Plugin to register
	 */
	public void registerEvents(Listener listener, Object owner);

	/**
	 * Registers the specified executor to the given event class
	 *
	 * @param event	Event type to register
	 * @param priority Priority to register this event at
	 * @param executor EventExecutor to register
	 * @param owner	Plugin to register
	 */
	public void registerEvent(Class<? extends Event> event, Order priority, EventExecutor executor, Object owner);
}
