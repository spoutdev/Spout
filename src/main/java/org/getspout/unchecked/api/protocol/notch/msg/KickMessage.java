package org.getspout.unchecked.api.protocol.notch.msg;

import org.getspout.unchecked.api.protocol.Message;

public final class KickMessage extends Message {
	private final String reason;

	public KickMessage(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	@Override
	public String toString() {
		return "KickMessage{reason=" + reason + "}";
	}
}
