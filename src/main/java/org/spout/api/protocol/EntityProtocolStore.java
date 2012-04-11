package org.spout.api.protocol;

import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.util.concurrent.OptimisticReadWriteLock;

/**
 * A store for storing EntityProtocols with fast array based lookup. Each entity
 * controller type will have a protocol store which contains methods for
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
			} else {
				protocols[id] = protocol;
			}
		} finally {
			lock.writeUnlock(seq);
		}
	}

}
