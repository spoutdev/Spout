package org.spout.api.protocol.builder;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;

public class CompoundMessageField extends MessageFieldImpl {
	
	private final MessageField[] fields;
	private final MessageField[] fieldsCompressed;
	private final int fixedLength;
	
	public CompoundMessageField(MessageField[] fields) {
		this.fields = fields;
		this.fieldsCompressed = compressFields(fields);
		if (fieldsCompressed.length == 0) {
			fixedLength = 0;
		} else if (fieldsCompressed.length == 1) {
			fixedLength = fieldsCompressed[0].getFixedLength();
		} else {
			fixedLength = -1;
		}
	}
	
	@Override
	public int getFixedLength() {
		return fixedLength;
	}
	
	@Override
	public MessageField getCompressed() {
		if (fieldsCompressed.length == 1) {
			MessageField compressed = fieldsCompressed[0].getCompressed();
			if (compressed != null) {
				return compressed;
			} else {
				return fieldsCompressed[0];
			}
		} else {
			return null;
		}
	}

	@Override
	public int skip(ChannelBuffer buffer) {
		int length = 0;
		for (int i = 0; i < fieldsCompressed.length; i++) {
			length += fieldsCompressed[i].skip(buffer);
		}
		return length;
	}
	
	@Override
	public int getLength(ChannelBuffer buffer) {
		int startPosition = buffer.readerIndex();
		int length = skip(buffer);
		buffer.readerIndex(startPosition);
		return length;
	}

	@Override
	public Object[] read(ChannelBuffer buffer) {
		Object[] array = new Object[fields.length];
		for (int i = 0; i < fields.length; i++) {
			array[i] = fields[i].read(buffer);
		}
		return array;
	}

	@Override
	public void write(ChannelBuffer buffer, Object value) {
		Object[] array = (Object[]) value;
		if (array.length != fields.length) {
			throw new IllegalArgumentException("Number of elements in the value array does not match the number of fields");
		}
		for (int i = 0; i < fields.length; i++) {
			fields[i].write(buffer, array[i]);
		}
	}

	@Override
	public void transfer(ChannelBuffer sourceBuffer, ChannelBuffer targetBuffer) {
		getLength(sourceBuffer);
		for (int i = 0; i < fieldsCompressed.length; i++) {
			fields[i].transfer(sourceBuffer, targetBuffer);
		}
	}
	
	public static MessageField[] compressFields(MessageField[] fields) {
		List<MessageField> compressedArray = new ArrayList<MessageField>();
		int i = 0;
		while (i < fields.length) {
			int fixedLength = 0;
			while (i < fields.length && fields[i].getFixedLength() >= 0) {
				fixedLength += fields[i].getFixedLength();
				i++;
			}
			if (fixedLength > 0) {
				compressedArray.add(new FixedMessageField(fixedLength));
			}
			if (i < fields.length) {
				MessageField compressed = fields[i].getCompressed();
				if (compressed != null) {
					compressedArray.add(compressed);
				} else {
					compressedArray.add(fields[i]);
				}
				i++;
			}
		}
		return compressedArray.toArray(new MessageField[0]);
	}

}
