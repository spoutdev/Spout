package org.getspout.unchecked.api.protocol.notch.codec;

import org.getspout.unchecked.api.protocol.MessageCodec;
import org.getspout.unchecked.api.protocol.notch.msg.TransactionMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;

public final class TransactionCodec extends MessageCodec<TransactionMessage> {
	public TransactionCodec() {
		super(TransactionMessage.class, 0x6A);
	}

	@Override
	public TransactionMessage decode(ChannelBuffer buffer) throws IOException {
		int id = buffer.readUnsignedByte();
		int transaction = buffer.readUnsignedShort();
		boolean accepted = buffer.readUnsignedByte() != 0;
		return new TransactionMessage(id, transaction, accepted);
	}

	@Override
	public ChannelBuffer encode(TransactionMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(4);
		buffer.writeByte(message.getId());
		buffer.writeShort(message.getTransaction());
		buffer.writeByte(message.isAccepted() ? 1 : 0);
		return buffer;
	}
}
