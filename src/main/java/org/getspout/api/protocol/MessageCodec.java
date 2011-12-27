package org.getspout.api.protocol;

import org.getspout.api.protocol.Message;
import org.jboss.netty.buffer.ChannelBuffer;

import java.io.IOException;

public abstract class MessageCodec<T extends Message> {
	private final Class<T> clazz;
	private final int opcode;
	private final boolean expanded;

	public MessageCodec(Class<T> clazz, int opcode) {
		this(clazz, opcode, false);
	}
		
	public MessageCodec(Class<T> clazz, int opcode, boolean expanded) {
		this.clazz = clazz;
		this.opcode = opcode;
		this.expanded = expanded;
	}

	public final Class<T> getType() {
		return clazz;
	}

	public final int getOpcode() {
		return opcode;
	}
	
	public boolean isExpanded() {
		return expanded;
	}

	public abstract ChannelBuffer encode(T message) throws IOException;

	public abstract T decode(ChannelBuffer buffer) throws IOException;
}
