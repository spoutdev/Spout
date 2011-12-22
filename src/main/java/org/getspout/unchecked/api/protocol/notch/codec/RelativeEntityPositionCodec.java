package org.getspout.unchecked.api.protocol.notch.codec;

import org.getspout.unchecked.api.protocol.MessageCodec;
import org.getspout.unchecked.api.protocol.notch.msg.RelativeEntityPositionMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;

public final class RelativeEntityPositionCodec extends MessageCodec<RelativeEntityPositionMessage> {
	public RelativeEntityPositionCodec() {
		super(RelativeEntityPositionMessage.class, 0x1F);
	}

	@Override
	public RelativeEntityPositionMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readInt();
		int dx = buffer.readByte();
		int dy = buffer.readByte();
		int dz = buffer.readByte();
		return new RelativeEntityPositionMessage(id, dx, dy, dz);
	}

	@Override
	public ChannelBuffer encode(RelativeEntityPositionMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(7);
		buffer.writeInt(message.getId());
		buffer.writeByte(message.getDeltaX());
		buffer.writeByte(message.getDeltaY());
		buffer.writeByte(message.getDeltaZ());
		return buffer;
	}
}
