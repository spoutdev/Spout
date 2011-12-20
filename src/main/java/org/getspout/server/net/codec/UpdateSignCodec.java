package org.getspout.server.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import org.getspout.server.msg.UpdateSignMessage;
import org.getspout.server.util.ChannelBufferUtils;

public final class UpdateSignCodec extends MessageCodec<UpdateSignMessage> {
	public UpdateSignCodec() {
		super(UpdateSignMessage.class, 0x82);
	}

	@Override
	public UpdateSignMessage decode(ChannelBuffer buffer) throws IOException {
		int x = buffer.readInt();
		int y = buffer.readShort();
		int z = buffer.readInt();
		String[] message = new String[4];
		for (int i = 0; i < message.length; i++) {
			message[i] = ChannelBufferUtils.readString(buffer);
		}
		return new UpdateSignMessage(x, y, z, message);
	}

	@Override
	public ChannelBuffer encode(UpdateSignMessage message) throws IOException {
		String[] lines = message.getMessage();

		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeInt(message.getX());
		buffer.writeShort(message.getY());
		buffer.writeInt(message.getZ());
		for (String line : lines) {
			ChannelBufferUtils.writeString(buffer, line);
		}
		return buffer;
	}
}
