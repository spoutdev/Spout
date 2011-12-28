package org.getspout.api.protocol;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.getspout.api.Commons;
import org.getspout.api.protocol.bootstrap.BootstrapCodecLookupService;
import org.getspout.api.protocol.bootstrap.msg.BootstrapIdentificationMessage;
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
	private volatile CodecLookupService codecLookup = bootstrapCodecLookup;
	private int previousOpcode = -1;
	private boolean configListen = true;
	private final CommonHandler handler;
	private final CommonEncoder encoder;
	
	public CommonDecoder(CommonHandler handler, CommonEncoder encoder) {
		this.encoder = encoder;
		this.handler = handler;
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel c, ChannelBuffer buf, VoidEnum state) throws Exception {
		int opcode = buf.getShort(buf.readerIndex());
		
		MessageCodec<?> codec = codecLookup.find(opcode);
		
		if (codec == null) {
			throw new IOException("Unknown operation code: " + opcode + " (previous opcode: " + previousOpcode + ").");
		}
		
		if (codec.isExpanded()) {
			buf.readShort();
		} else {
			buf.readByte();
		}

		previousOpcode = opcode;
		
		Object message = codec.decode(buf);
		
		if (configListen) {
			if (Commons.isSpout) {
				if (message instanceof BootstrapIdentificationMessage) {
					BootstrapIdentificationMessage idMessage = (BootstrapIdentificationMessage)message;

					long id = idMessage.getSeed();

					Protocol protocol = Protocol.getProtocol(id);
					
					if (protocol != null) {
						codecLookup = protocol.getCodecLookupService();
						encoder.setProtocol(protocol);
						handler.setProtocol(protocol);
						configListen = true;
					} else {
						throw new IllegalStateException("No protocol associated with an id of " + id);
					}
				}
			}
		}
			
		return message;
	}
	
}