package org.getspout.api.protocol.bootstrap.codec;

import org.getspout.api.protocol.MessageCodec;
import org.getspout.api.protocol.bootstrap.ChannelBufferUtils;
import org.getspout.api.protocol.bootstrap.msg.BootstrapIdentificationMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class BootstrapIdentificationCodec extends MessageCodec<BootstrapIdentificationMessage> {
	public BootstrapIdentificationCodec() {
		super(BootstrapIdentificationMessage.class, 0x01);
	}

	@Override
	public BootstrapIdentificationMessage decode(ChannelBuffer buffer) {
		int version = buffer.readInt();
		String name = ChannelBufferUtils.readString(buffer);
		long seed = buffer.readLong();
		int mode = buffer.readInt();
		int dimension = buffer.readByte();
		int difficulty = buffer.readByte();
		int worldHeight = ChannelBufferUtils.getExpandedHeight(buffer.readByte());
		int maxPlayers = buffer.readByte();
		return new BootstrapIdentificationMessage(version, name, seed, mode, dimension, difficulty, worldHeight, maxPlayers);
	}

	@Override
	public ChannelBuffer encode(BootstrapIdentificationMessage message) {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeInt(message.getId());
		ChannelBufferUtils.writeString(buffer, message.getName());
		buffer.writeLong(message.getSeed());
		buffer.writeInt(message.getGameMode());
		buffer.writeByte(message.getDimension());
		buffer.writeByte(message.getDifficulty());
		buffer.writeByte(ChannelBufferUtils.getShifts(message.getWorldHeight()) - 1);
		buffer.writeByte(message.getMaxPlayers());
		return buffer;
	}
}
