package org.spout.api.protocol;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.entity.Controller;
import org.spout.api.util.concurrent.OptimisticReadWriteLock;

/**
 * A store for storing EntityProtocols with fast array based lookup. Each entity
 * controller type will have a protocol store which contains methods for
 * creating the relevant network messages.
 */
public class EntityProtocolStore {

	private OptimisticReadWriteLock lock = new OptimisticReadWriteLock();
	private ConcurrentMap<Class<? extends Controller>, AtomicReference<EntityProtocol[]>> entityProtocols = new ConcurrentHashMap<Class<? extends Controller>, AtomicReference<EntityProtocol[]>>();

	public EntityProtocol getEntityProtocol(Class<? extends Controller> controller, int id) {
		while (true) {
			int seq = lock.readLock();
			AtomicReference<EntityProtocol[]> protocolArray = entityProtocols.get(controller);

			EntityProtocol[] protocols = protocolArray == null ? null : protocolArray.get();
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

	public void setEntityProtocol(Class<? extends Controller> controller, int id, EntityProtocol protocol) {
		if (id < 0) {
			throw new IllegalArgumentException("Entity protocols ids must be positive");
		}
		int seq = lock.writeLock();
		try {
			AtomicReference<EntityProtocol[]> protocolArray = entityProtocols.get(controller);
			if (protocolArray == null) {
				protocolArray = new AtomicReference<EntityProtocol[]>();
				entityProtocols.put(controller, protocolArray);
			}
			EntityProtocol[] protocols = protocolArray.get();
			if (protocols == null) {
				protocols = new EntityProtocol[id + 1];
				protocolArray.set(protocols);
			}
			if (id >= protocols.length) {
				EntityProtocol[] newProtocols = new EntityProtocol[Math.max(protocols.length * 3 / 2, id + 1)];
				System.arraycopy(protocols, 0, newProtocols, 0, protocols.length);
				protocols = newProtocols;
				protocolArray.set(protocols);
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
