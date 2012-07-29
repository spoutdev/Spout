package org.spout.api.protocol.builder;

import org.jboss.netty.buffer.ChannelBuffer;


public class FixedMessageField extends MessageFieldImpl {
	
	protected final int length;
	
	protected FixedMessageField(int length) {
		this.length = length;
	}
	
	@Override
	public MessageField getCompressed() {
		return null;
	}
	
	@Override
	public int getLength(ChannelBuffer buffer) {
		return length;
	}
	
	@Override
	public int getFixedLength() {
		return length;
	}

	@Override
	public int skip(ChannelBuffer buffer) {
		buffer.skipBytes(length);
		return length;
	}

	@Override
	public Object read(ChannelBuffer buffer) {
		throw new UnsupportedOperationException("The raw fixed length field class cannot decode byte arrays");
	}

	@Override
	public void write(ChannelBuffer buffer, Object value) {
		throw new UnsupportedOperationException("The raw fixed length field class cannot encode byte arrays");		
	}

	@Override
	public void transfer(ChannelBuffer sourceBuffer, ChannelBuffer targetBuffer) {
		sourceBuffer.readBytes(targetBuffer, length);
	}

}
