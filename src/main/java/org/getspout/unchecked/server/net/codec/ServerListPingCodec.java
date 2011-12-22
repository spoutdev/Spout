package org.getspout.unchecked.server.net.codec;

import java.io.IOException;

import org.getspout.unchecked.server.msg.ServerListPingMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class ServerListPingCodec extends MessageCodec<ServerListPingMessage> {
	private static final ServerListPingMessage LIST_PING_MESSAGE = new ServerListPingMessage();

	public ServerListPingCodec() {
		super(ServerListPingMessage.class, 0xFE);
	}

	@Override
	public ChannelBuffer encode(ServerListPingMessage message) throws IOException {
		return ChannelBuffers.EMPTY_BUFFER;
	}

	@Override
	public ServerListPingMessage decode(ChannelBuffer buffer) throws IOException {
		return LIST_PING_MESSAGE;
	}
}
