package org.getspout.unchecked.api.protocol.notch.codec;

import org.getspout.unchecked.api.protocol.MessageCodec;
import org.getspout.unchecked.api.protocol.notch.msg.TimeMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;

public final class TimeCodec extends MessageCodec<TimeMessage> {
	public TimeCodec() {
		super(TimeMessage.class, 0x04);
	}

	@Override
	public TimeMessage decode(ChannelBuffer buffer) throws IOException {
		return new TimeMessage(buffer.readLong());
	}

	@Override
	public ChannelBuffer encode(TimeMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(8);
		buffer.writeLong(message.getTime());
		return buffer;
	}
}
