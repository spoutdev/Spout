package org.getspout.unchecked.api.protocol.notch.codec;

import org.getspout.unchecked.api.protocol.MessageCodec;
import org.getspout.unchecked.api.protocol.notch.msg.CreateEntityMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;

public final class CreateEntityCodec extends MessageCodec<CreateEntityMessage> {
	public CreateEntityCodec() {
		super(CreateEntityMessage.class, 0x1E);
	}

	@Override
	public CreateEntityMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readInt();
		return new CreateEntityMessage(id);
	}

	@Override
	public ChannelBuffer encode(CreateEntityMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(4);
		buffer.writeInt(message.getId());
		return buffer;
	}
}
