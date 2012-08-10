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
package org.spout.api.protocol;

import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.util.concurrent.OptimisticReadWriteLock;

/**
 * A store for storing EntityProtocols with fast array based lookup. Each entity
 * entity type will have a protocol store which contains methods for
 * creating the relevant network messages.
 */
public class EntityProtocolStore {

	private OptimisticReadWriteLock lock = new OptimisticReadWriteLock();
	private AtomicReference<EntityProtocol[]> entityProtocols = new AtomicReference<EntityProtocol[]>();

	public EntityProtocol getEntityProtocol(int id) {
		while (true) {
			int seq = lock.readLock();

			EntityProtocol[] protocols = entityProtocols.get();
			EntityProtocol protocol;
			if (protocols == null || id < 0 || id >= protocols.length) {
				protocol = null;
			} else {
				protocol = protocols[id];
			}
			if (lock.readUnlock(seq)) {
				return protocol;
			}
		}
	}

	public void setEntityProtocol(int id, EntityProtocol protocol) {
		if (id < 0) {
			throw new IllegalArgumentException("Entity protocols ids must be positive");
		}
		int seq = lock.writeLock();
		try {
			EntityProtocol[] protocols = entityProtocols.get();
			if (protocols == null) {
				protocols = new EntityProtocol[id + 1];
				entityProtocols.set(protocols);
			}
			if (id >= protocols.length) {
				EntityProtocol[] newProtocols = new EntityProtocol[Math.max(protocols.length * 3 / 2, id + 1)];
				System.arraycopy(protocols, 0, newProtocols, 0, protocols.length);
				protocols = newProtocols;
				entityProtocols.set(protocols);
			}
			if (protocols[id] != null) {
				throw new IllegalStateException("Entity protocol id " + id + " used more than once");
			}

			protocols[id] = protocol;
		} finally {
			lock.writeUnlock(seq);
		}
	}

}
