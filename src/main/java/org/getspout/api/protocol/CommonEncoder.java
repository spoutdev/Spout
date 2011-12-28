package org.getspout.api.protocol;

import java.io.IOException;

import org.getspout.api.protocol.bootstrap.BootstrapCodecLookupService;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * A {@link OneToOneEncoder} which encodes Minecraft {@link Message}s into
 * {@link ChannelBuffer}s.
 */
public class CommonEncoder extends OneToOneEncoder {
	
	private final CodecLookupService bootstrapCodecLookup = new BootstrapCodecLookupService();
	private volatile CodecLookupService codecLookup = bootstrapCodecLookup;
	
	@SuppressWarnings("unchecked")
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel c, Object msg) throws Exception {
		if (msg instanceof Message) {
			Message message = (Message) msg;

			Class<? extends Message> clazz = message.getClass();
			MessageCodec<Message> codec;
			
			codec = (MessageCodec<Message>) codecLookup.find(clazz);
			if (codec == null) {
				throw new IOException("Unknown message type: " + clazz + ".");
			}

			ChannelBuffer opcodeBuf = ChannelBuffers.buffer(1);
			opcodeBuf.writeByte(codec.getOpcode());

			return ChannelBuffers.wrappedBuffer(opcodeBuf, codec.encode(message));
		}
		return msg;
	}
	
	public void setProtocol(Protocol protocol) {
		codecLookup = protocol.getCodecLookupService();
	}
}