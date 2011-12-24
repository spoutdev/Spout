package org.getspout.unchecked.server.net.codec;

import org.getspout.server.util.ChannelBufferUtils;
import org.getspout.unchecked.server.msg.HandshakeMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class HandshakeCodec extends MessageCodec<HandshakeMessage> {
	public HandshakeCodec() {
		super(HandshakeMessage.class, 0x02);
	}

	@Override
	public HandshakeMessage decode(ChannelBuffer buffer) {
		String identifier = ChannelBufferUtils.readString(buffer);
		return new HandshakeMessage(identifier);
	}

	@Override
	public ChannelBuffer encode(HandshakeMessage message) {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		ChannelBufferUtils.writeString(buffer, message.getIdentifier());
		return buffer;
	}
}
