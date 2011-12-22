package org.getspout.api.protocol.notch.codec;

import org.getspout.api.protocol.MessageCodec;
import org.getspout.api.protocol.notch.msg.EntityVelocityMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;

public final class EntityVelocityCodec extends MessageCodec<EntityVelocityMessage> {
	public EntityVelocityCodec() {
		super(EntityVelocityMessage.class, 0x1C);
	}

	@Override
	public EntityVelocityMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readInt();
		int vx = buffer.readUnsignedShort();
		int vy = buffer.readUnsignedShort();
		int vz = buffer.readUnsignedShort();
		return new EntityVelocityMessage(id, vx, vy, vz);
	}

	@Override
	public ChannelBuffer encode(EntityVelocityMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(10);
		buffer.writeInt(message.getId());
		buffer.writeShort(message.getVelocityX());
		buffer.writeShort(message.getVelocityY());
		buffer.writeShort(message.getVelocityZ());
		return buffer;
	}
}
