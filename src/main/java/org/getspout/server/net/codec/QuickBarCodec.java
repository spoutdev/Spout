package org.getspout.server.net.codec;

import java.io.IOException;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import org.getspout.server.item.ItemProperties;
import org.getspout.server.msg.QuickBarMessage;
import org.getspout.server.util.ChannelBufferUtils;
import org.getspout.server.util.nbt.Tag;

public class QuickBarCodec extends MessageCodec<QuickBarMessage> {
	public QuickBarCodec() {
		super(QuickBarMessage.class, 0x6B);
	}

	@Override
	public ChannelBuffer encode(QuickBarMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeShort(message.getSlot());
		buffer.writeShort(message.getId());
		buffer.writeByte(message.getAmount());
		buffer.writeShort(message.getDamage());
		if (message.getId() > 255 && ItemProperties.get(message.getId()).hasNbtData()) {
			ChannelBufferUtils.writeCompound(buffer, message.getNbtData());
		}
		return buffer;
	}

	@Override
	public QuickBarMessage decode(ChannelBuffer buffer) throws IOException {
		short slot = buffer.readShort();
		short id = buffer.readShort();
		if (id != -1) {
			short amount = buffer.readByte();
			short damage = buffer.readShort();
			Map<String, Tag> nbtData = null;
			if (id > 255) {
				ItemProperties props = ItemProperties.get(id);
				if (props != null && props.hasNbtData()) {
					ChannelBufferUtils.readCompound(buffer);
				}
			}
			return new QuickBarMessage(slot, id, amount, damage, nbtData);
		} else {
			return new QuickBarMessage(slot, id, (short) 0, (short) 0, null);
		}
	}
}
