package org.getspout.server.net.codec;

import java.io.IOException;

import org.getspout.server.util.ChannelBufferUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import org.getspout.server.msg.RespawnMessage;

public final class RespawnCodec extends MessageCodec<RespawnMessage> {
	public RespawnCodec() {
		super(RespawnMessage.class, 0x09);
	}

	@Override
	public RespawnMessage decode(ChannelBuffer buffer) throws IOException {
		byte dimension = buffer.readByte();
		byte difficulty = buffer.readByte();
		byte mode = buffer.readByte();
		int worldHeight = ChannelBufferUtils.getExpandedHeight(buffer.readShort());
		long seed = buffer.readLong();
		return new RespawnMessage(dimension, difficulty, mode, worldHeight, seed);
	}

	@Override
	public ChannelBuffer encode(RespawnMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(14);
		buffer.writeByte(message.getDimension());
		buffer.writeByte(message.getDifficulty());
		buffer.writeByte(message.getGameMode());
		buffer.writeShort(ChannelBufferUtils.getShifts(message.getWorldHeight()) - 1);
		buffer.writeLong(message.getSeed());
		return buffer;
	}
}
