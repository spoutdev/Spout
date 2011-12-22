package org.getspout.server.net.codec;

import java.io.IOException;

import org.getspout.server.msg.ActivateItemMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class ActivateItemCodec extends MessageCodec<ActivateItemMessage> {
	public ActivateItemCodec() {
		super(ActivateItemMessage.class, 0x10);
	}

	@Override
	public ActivateItemMessage decode(ChannelBuffer buffer) throws IOException {
		int slot = buffer.readUnsignedShort();
		return new ActivateItemMessage(slot);
	}

	@Override
	public ChannelBuffer encode(ActivateItemMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(6);
		buffer.writeShort(message.getSlot());
		return buffer;
	}
}
