package org.getspout.unchecked.server.net.codec;

import java.io.IOException;
import java.util.Map;

import org.getspout.api.io.nbt.Tag;
import org.getspout.server.util.ChannelBufferUtils;
import org.getspout.unchecked.server.item.ItemProperties;
import org.getspout.unchecked.server.msg.SetWindowSlotMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

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
			if (item > 255) {
				ItemProperties props = ItemProperties.get(item);
				if (props != null && props.hasNbtData()) {
					ChannelBufferUtils.readCompound(buffer);
				}
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
			if (message.getItem() > 255) {
				ItemProperties props = ItemProperties.get(message.getItem());
				if (props != null && props.hasNbtData()) {
					ChannelBufferUtils.writeCompound(buffer, message.getNbtData());
				}
			}
		}
		return buffer;
	}
}
