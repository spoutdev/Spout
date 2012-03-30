/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.api.event.server.data;

import org.spout.api.data.DataSubject;
import org.spout.api.data.DataValue;
import org.spout.api.event.HandlerList;
import org.spout.api.event.server.NodeBasedEvent;
import org.spout.api.geo.World;

/**
 * This event is called when DataSubject.getData*() is called.
 */
public class RetrieveDataEvent extends NodeBasedEvent {
	private final DataSubject subject;
	private DataValue result;
	private static final HandlerList handlers = new HandlerList();

	public RetrieveDataEvent(DataSubject subject, String node) {
		super(node);
		this.subject = subject;
	}

	/**
	 * Gets the subject the data is being taken from.
	 *
	 * @return subject of data
	 */
	public DataSubject getSubject() {
		return subject;
	}

	/**
	 * Returns the raw result of the event.
	 *
	 * @return object
	 */
	public DataValue getResult() {
		return result;
	}

	/**
	 * Sets the result of the event.
	 *
	 * @param result
	 */
	public void setResult(DataValue result) {
		this.result = result;
	}

	/**
	 * Sets the result of the event.
	 *
	 * @param result
	 */
	public void setResult(Object result) {
		setResult(new DataValue(result));
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
