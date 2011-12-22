package org.getspout.unchecked.api.protocol.notch.codec;

import org.getspout.api.util.nbt.Tag;
import org.getspout.unchecked.api.protocol.MessageCodec;
import org.getspout.unchecked.api.protocol.notch.ChannelBufferUtils;
import org.getspout.unchecked.api.protocol.notch.msg.SetWindowSlotMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;
import java.util.Map;

public final class SetWindowSlotCodec extends MessageCodec<SetWindowSlotMessage> {
	public SetWindowSlotCodec() {
		super(SetWindowSlotMessage.class, 0x67);
	}

	@Override
	public SetWindowSlotMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readUnsignedByte();
		int slot = buffer.readUnsignedShort();
		int item = buffer.readUnsignedShort();
		if (item == 0xFFFF) {
			return new SetWindowSlotMessage(id, slot);
		} else {
			int count = buffer.readUnsignedByte();
			int damage = buffer.readUnsignedShort();
			Map<String, Tag> nbtData = null;
			if (ChannelBufferUtils.hasNbtData(id)) {
				nbtData = ChannelBufferUtils.readCompound(buffer);
			}
			return new SetWindowSlotMessage(id, slot, item, count, damage, nbtData);
		}
	}

	@Override
	public ChannelBuffer encode(SetWindowSlotMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeByte(message.getId());
		buffer.writeShort(message.getSlot());
		buffer.writeShort(message.getItem());
		if (message.getItem() != -1) {
			buffer.writeByte(message.getCount());
			buffer.writeShort(message.getDamage());
			if (ChannelBufferUtils.hasNbtData(message.getId())) {
				ChannelBufferUtils.writeCompound(buffer, message.getNbtData());
			}
		}
		return buffer;
	}
}
