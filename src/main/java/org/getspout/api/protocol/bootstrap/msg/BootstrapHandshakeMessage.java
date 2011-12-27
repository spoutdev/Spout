package org.getspout.api.protocol.bootstrap.msg;

import org.getspout.api.protocol.Message;

public final class BootstrapHandshakeMessage extends Message {
	private final String identifier;

	public BootstrapHandshakeMessage(String identifier) {
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
