package org.spout.api.component.components;

import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.protocol.EntityProtocol;
import org.spout.api.protocol.EntityProtocolStore;
import org.spout.api.util.StringMap;

public class NetworkComponent extends EntityComponent {

	public static final int UNREGISTERED_ID = -1;
	private static final StringMap protocolMap = new StringMap(null, new MemoryStore<Integer>(), 0, 256, "controllerTypeProtocols");

	private int id = UNREGISTERED_ID;
	private final EntityProtocolStore protocolStore = new EntityProtocolStore();

	public NetworkComponent() {
	}

	/**
	 * @return id of the entity.
	 */
	public int getId() {
		return id;
	}

	void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns the {@link EntityProtocol} for the given protocol id for this type of entity
	 * @param protocolId The protocol id (retrieved using {@link #getProtocolId(String)})
	 * @return The entity protocol for the specified id.
	 */
	public EntityProtocol getEntityProtocol(int protocolId) {
		return protocolStore.getEntityProtocol(protocolId);
	}

	/**
	 * Registers {@code protocol} with this ControllerType's EntityProtocolStore
	 * @param protocolId The protocol id (retrieved using {@link #getProtocolId(String)})
	 * @param protocol The protocol to set
	 */
	public void setEntityProtocol(int protocolId, EntityProtocol protocol) {
		protocolStore.setEntityProtocol(protocolId, protocol);
	}

	/**
	 * @param protocolName The name of the protocol class to get an id for
	 * @return The id for the specified protocol class
	 */
	public static int getProtocolId(String protocolName) {
		return protocolMap.register(protocolName);
	}
}
