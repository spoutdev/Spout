package org.getspout.api.protocol.notch.codec;

import org.getspout.api.protocol.MessageCodec;
import org.getspout.api.protocol.notch.msg.DestroyEntityMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;

public final class DestroyEntityCodec extends MessageCodec<DestroyEntityMessage> {
	public DestroyEntityCodec() {
		super(DestroyEntityMessage.class, 0x1D);
	}

	@Override
	public DestroyEntityMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readInt();
		return new DestroyEntityMessage(id);
	}

	@Override
	public ChannelBuffer encode(DestroyEntityMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(4);
		buffer.writeInt(message.getId());
		return buffer;
	}
}
