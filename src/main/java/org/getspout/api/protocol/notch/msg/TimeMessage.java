package org.getspout.api.protocol.notch.msg;

import org.getspout.api.protocol.Message;

public final class TimeMessage extends Message {
	private final long time;

	public TimeMessage(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	@Override
	public String toString() {
		return "TimeMessage{time=" + time + "}";
	}
}
