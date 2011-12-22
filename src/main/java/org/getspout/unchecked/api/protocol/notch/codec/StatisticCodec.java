package org.getspout.unchecked.api.protocol.notch.codec;

import org.getspout.unchecked.api.protocol.MessageCodec;
import org.getspout.unchecked.api.protocol.notch.msg.StatisticMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;

public final class StatisticCodec extends MessageCodec<StatisticMessage> {
	public StatisticCodec() {
		super(StatisticMessage.class, 0xC8);
	}

	@Override
	public StatisticMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readInt();
		byte amount = buffer.readByte();
		return new StatisticMessage(id, amount);
	}

	@Override
	public ChannelBuffer encode(StatisticMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(1);
		buffer.writeInt(message.getId());
		buffer.writeByte(message.getAmount());
		return buffer;
	}
}
