package org.getspout.api.protocol.bootstrap.msg;

import org.getspout.api.protocol.Message;

public class BootstrapPingMessage extends Message {
	private final int pingId;

	public BootstrapPingMessage(int pingId) {
		this.pingId = pingId;
	}

	public int getPingId() {
		return pingId;
	}

	@Override
	public String toString() {
		return "PingMessage{id=" + pingId + "}";
	}
}