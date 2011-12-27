package org.getspout.api.protocol;

import java.io.IOException;

import org.getspout.api.protocol.bootstrap.BootstrapCodecLookupService;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;

/**
 * A {@link ReplayingDecoder} which decodes {@link ChannelBuffer}s into
 * Common {@link org.getspout.unchecked.server.msg.Message}s.
 */
public class CommonDecoder extends ReplayingDecoder<VoidEnum> {
	
	private final CodecLookupService bootstrapCodecLookup = new BootstrapCodecLookupService();
	private CodecLookupService codecLookup = bootstrapCodecLookup;
	private int previousOpcode = -1;

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel c, ChannelBuffer buf, VoidEnum state) throws Exception {
		int opcode = buf.readUnsignedByte();
		
		MessageCodec<?> codec = codecLookup.find(opcode);
		
		if (codec == null) {
			throw new IOException("Unknown operation code: " + opcode + " (previous opcode: " + previousOpcode + ").");
		}

		previousOpcode = opcode;
		
		// TODO - need to add detection for protocol change message

		return codec.decode(buf);
	}
}