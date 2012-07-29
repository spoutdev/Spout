package org.spout.api.protocol.builder;


public abstract class MessageFieldImpl implements MessageField {

	@Override
	public int getFixedLength() {
		return -1;
	}

}
