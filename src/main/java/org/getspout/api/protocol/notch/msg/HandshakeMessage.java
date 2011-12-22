package org.getspout.api.protocol.notch.msg;

import org.getspout.api.protocol.Message;

public final class HandshakeMessage extends Message {
	private final String identifier;

	public HandshakeMessage(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	@Override
	public String toString() {
		return "HandshakeMessage{identifier=" + identifier + "}";
	}
}
