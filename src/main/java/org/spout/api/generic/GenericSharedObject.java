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
package org.spout.api.generic;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Describes an Object that is shared from client to server over the network
 * 
 * @param <T>
 */
public abstract class GenericSharedObject<T> {
	
	private UUID uniqueId = UUID.randomUUID();
	private boolean dirty = true;
	
	public UUID getUniqueId() {
		return uniqueId;
	}
	
	/**
	 * Called when a new Packet for this instance arrives
	 * @param input the input stream with the data
	 */
	public abstract void readData(InputStream input);
	
	/**
	 * Called when the instance should be sent or updated to/on the client
	 * @param output the output stream
	 */
	public abstract void writeData(OutputStream output);

	/**
	 * Gets if the instance has been modified. If it is true, it will be updated to the clien in the next tick
	 * @return
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Sets if the instance has been modified. Obvious updates which the client can calculate itself can use setDirty(false) to save bandwidth.
	 * @param dirty
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	
}
