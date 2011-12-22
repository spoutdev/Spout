package org.getspout.api.protocol.notch.codec;

import org.getspout.api.protocol.MessageCodec;
import org.getspout.api.protocol.notch.ChannelBufferUtils;
import org.getspout.api.protocol.notch.msg.QuickBarMessage;
import org.getspout.api.util.nbt.Tag;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import java.io.IOException;
import java.util.Map;

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
		if (ChannelBufferUtils.hasNbtData(message.getId())) {
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
			if (ChannelBufferUtils.hasNbtData(id)) nbtData = ChannelBufferUtils.readCompound(buffer);
			return new QuickBarMessage(slot, id, amount, damage, nbtData);
		} else {
			return new QuickBarMessage(slot, id, (short)0, (short)0, null);
		}
	}
}
