package org.getspout.api.protocol.notch.msg;

import org.getspout.api.protocol.Message;

public final class CreateEntityMessage extends Message {
	private final int id;

	public CreateEntityMessage(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "CreateEntityMessage{id=" + id + "}";
	}
}
