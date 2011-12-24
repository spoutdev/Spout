/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.event;

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
	 * @param owner Plugin to register
	 */
	public void registerEvents(Listener listener, Object owner);

	/**
	 * Registers the specified executor to the given event class
	 *
	 * @param event Event type to register
	 * @param priority Priority to register this event at
	 * @param executor EventExecutor to register
	 * @param owner Plugin to register
	 */
	public void registerEvent(Class<? extends Event> event, Order priority, EventExecutor executor, Object owner);
}
