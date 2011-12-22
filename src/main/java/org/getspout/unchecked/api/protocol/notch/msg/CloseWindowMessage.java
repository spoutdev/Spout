package org.getspout.unchecked.api.protocol.notch.msg;

import org.getspout.unchecked.api.protocol.Message;

public final class CloseWindowMessage extends Message {
	private final int id;

	public CloseWindowMessage(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "CloseWindowMessage{id=" + id + "}";
	}
}
