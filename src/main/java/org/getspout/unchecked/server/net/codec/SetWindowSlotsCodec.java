package org.getspout.unchecked.server.net.codec;

import java.io.IOException;
import java.util.Map;

import org.getspout.api.io.nbt.Tag;
import org.getspout.unchecked.server.inventory.SpoutItemStack;
import org.getspout.unchecked.server.item.ItemProperties;
import org.getspout.unchecked.server.msg.SetWindowSlotsMessage;
import org.getspout.unchecked.server.util.ChannelBufferUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class SetWindowSlotsCodec extends MessageCodec<SetWindowSlotsMessage> {
	public SetWindowSlotsCodec() {
		super(SetWindowSlotsMessage.class, 0x68);
	}

	@Override
	public SetWindowSlotsMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readUnsignedByte();
		int count = buffer.readUnsignedShort();
		SpoutItemStack[] items = new SpoutItemStack[count];
		for (int slot = 0; slot < count; slot++) {
			int item = buffer.readUnsignedShort();
			if (item == 0xFFFF) {
				items[slot] = null;
			} else {
				int itemCount = buffer.readUnsignedByte();
				int damage = buffer.readUnsignedByte();
				Map<String, Tag> nbtData = null;
				if (item > 255) {
					ItemProperties props = ItemProperties.get(item);
					if (props != null && props.hasNbtData()) {
						ChannelBufferUtils.readCompound(buffer);
					}
				}
				items[slot] = new SpoutItemStack(item, itemCount, (short) damage, nbtData);
			}
		}
		return new SetWindowSlotsMessage(id, items);
	}

	@Override
	public ChannelBuffer encode(SetWindowSlotsMessage message) throws IOException {
		SpoutItemStack[] items = message.getItems();

		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeByte(message.getId());
		buffer.writeShort(items.length);
		for (SpoutItemStack item : items) {
			if (item == null) {
				buffer.writeShort(-1);
			} else {
				buffer.writeShort(item.getTypeId());
				buffer.writeByte(item.getAmount());
				buffer.writeByte(item.getDurability());
				if (item.getTypeId() > 255) {
					ItemProperties props = ItemProperties.get(item.getTypeId());
					if (props != null && props.hasNbtData()) {
						ChannelBufferUtils.writeCompound(buffer, item.getNbtData());
					}
				}
			}
		}

		return buffer;
	}
}
