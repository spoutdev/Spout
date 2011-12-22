package org.getspout.unchecked.api.protocol.notch.msg;

import org.getspout.unchecked.api.protocol.Message;

public final class DestroyEntityMessage extends Message {
	private final int id;

	public DestroyEntityMessage(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "DestroyEntityMessage{id=" + id + "}";
	}
}
