package org.getspout.unchecked.server.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import org.getspout.unchecked.server.msg.PlayEffectMessage;

public final class PlayEffectCodec extends MessageCodec<PlayEffectMessage> {
	public PlayEffectCodec() {
		super(PlayEffectMessage.class, 0x3d);
	}

	@Override
	public PlayEffectMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readInt();
		int x = buffer.readInt();
		int y = buffer.readUnsignedByte();
		int z = buffer.readInt();
		int data = buffer.readInt();
		return new PlayEffectMessage(id, x, y, z, data);
	}

	@Override
	public ChannelBuffer encode(PlayEffectMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(17);
		buffer.writeInt(message.getId());
		buffer.writeInt(message.getX());
		buffer.writeByte(message.getY());
		buffer.writeInt(message.getZ());
		buffer.writeInt(message.getData());
		return buffer;
	}
}
