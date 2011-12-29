package org.getspout.api.protocol.bootstrap.codec;

import org.getspout.api.protocol.MessageCodec;
import org.getspout.api.protocol.bootstrap.ChannelBufferUtils;
import org.getspout.api.protocol.bootstrap.msg.BootstrapHandshakeMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class BootstrapHandshakeCodec extends MessageCodec<BootstrapHandshakeMessage> {
	public BootstrapHandshakeCodec() {
		super(BootstrapHandshakeMessage.class, 0x02);
	}

	@Override
	public BootstrapHandshakeMessage decode(ChannelBuffer buffer) {
		String identifier = ChannelBufferUtils.readString(buffer);
		return new BootstrapHandshakeMessage(identifier);
	}

	@Override
	public ChannelBuffer encode(BootstrapHandshakeMessage message) {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		ChannelBufferUtils.writeString(buffer, message.getIdentifier());
		return buffer;
	}
}
