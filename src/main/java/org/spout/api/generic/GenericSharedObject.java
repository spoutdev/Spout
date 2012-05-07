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
