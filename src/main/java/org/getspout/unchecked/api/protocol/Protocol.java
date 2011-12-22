package org.getspout.unchecked.api.protocol;

import org.jboss.netty.buffer.ChannelBuffer;

import java.io.IOException;

/**
 *
 * @author zml2008
 */
public abstract class Protocol {
	private final CodecLookupService codecService;
	
	public Protocol(CodecLookupService codecService) {
		this.codecService = codecService;
	}

	public abstract <T extends Message> MessageCodec<T> find(Class<T> clazz);

	public MessageCodec<?> find(int opcode) {
		return codecService.find(opcode);
	}

	public abstract ChannelBuffer encode(MessageCodec codec) throws IOException;

	public abstract Message decode(ChannelBuffer incoming) throws IOException;
}
