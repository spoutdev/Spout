package org.getspout.api.protocol;

import org.getspout.api.protocol.Message;
import org.jboss.netty.buffer.ChannelBuffer;

import java.io.IOException;

public abstract class MessageCodec<T extends Message> {
	private final Class<T> clazz;
	private final int opcode;

	public MessageCodec(Class<T> clazz, int opcode) {
		this.clazz = clazz;
		this.opcode = opcode;
	}

	public final Class<T> getType() {
		return clazz;
	}

	public final int getOpcode() {
		return opcode;
	}

	public abstract ChannelBuffer encode(T message) throws IOException;

	public abstract T decode(ChannelBuffer buffer) throws IOException;
}
