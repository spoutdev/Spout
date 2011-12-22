package org.getspout.api.protocol.notch.codec;

import org.getspout.api.protocol.MessageCodec;
import org.getspout.api.protocol.notch.ChannelBufferUtils;
import org.getspout.api.protocol.notch.msg.BlockPlacementMessage;
import org.getspout.api.util.nbt.Tag;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;
import java.util.Map;

public final class BlockPlacementCodec extends MessageCodec<BlockPlacementMessage> {
	public BlockPlacementCodec() {
		super(BlockPlacementMessage.class, 0x0F);
	}

	@Override
	public BlockPlacementMessage decode(ChannelBuffer buffer) throws IOException {
		int x = buffer.readInt();
		int y = buffer.readUnsignedByte();
		int z = buffer.readInt();
		int direction = buffer.readUnsignedByte();
		int id = buffer.readUnsignedShort();
		if (id == 0xFFFF) {
			return new BlockPlacementMessage(x, y, z, direction);
		} else {
			int count = buffer.readUnsignedByte();
			int damage = buffer.readShort();
			Map<String, Tag> nbtData = null;
			if (ChannelBufferUtils.hasNbtData(id)) nbtData = ChannelBufferUtils.readCompound(buffer);
			return new BlockPlacementMessage(x, y, z, direction, id, count, damage, nbtData);
		}
	}

	@Override
	public ChannelBuffer encode(BlockPlacementMessage message) throws IOException {
		int id = message.getId();

		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeInt(message.getX());
		buffer.writeByte(message.getY());
		buffer.writeInt(message.getZ());
		buffer.writeByte(message.getDirection());
		buffer.writeShort(id);
		if (id != -1) {
			buffer.writeByte(message.getCount());
			buffer.writeShort(message.getDamage());
			if (ChannelBufferUtils.hasNbtData(id)) ChannelBufferUtils.writeCompound(buffer, message.getNbtData());
		}
		return buffer;
	}
}
