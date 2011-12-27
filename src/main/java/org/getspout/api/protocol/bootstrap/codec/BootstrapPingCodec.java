package org.getspout.api.protocol.bootstrap.codec;

import org.getspout.api.protocol.MessageCodec;
import org.getspout.api.protocol.bootstrap.msg.BootstrapPingMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class BootstrapPingCodec extends MessageCodec<BootstrapPingMessage> {
	public BootstrapPingCodec() {
		super(BootstrapPingMessage.class, 0x00);
	}

	@Override
	public BootstrapPingMessage decode(ChannelBuffer buffer) {
		int id = buffer.readInt();
		return new BootstrapPingMessage(id);
	}

	@Override
	public ChannelBuffer encode(BootstrapPingMessage message) {
		ChannelBuffer buffer = ChannelBuffers.buffer(5);
		buffer.writeInt(message.getPingId());
		return buffer;
	}
}
