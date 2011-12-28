package org.getspout.api.protocol;

import org.getspout.api.Server;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.StaticChannelPipeline;

/**
 * A common {@link ChannelPipelineFactory}
 */
public final class CommonPipelineFactory implements ChannelPipelineFactory {
	/**
	 * The server.
	 */
	private final Server server;

	/**
	 * Creates a new Minecraft pipeline factory.
	 *
	 * @param server The server.
	 */
	public CommonPipelineFactory(Server server) {
		this.server = server;
	}

	public ChannelPipeline getPipeline() throws Exception {
		CommonHandler handler = new CommonHandler(server);
		CommonEncoder encoder = new CommonEncoder();
		return new StaticChannelPipeline(new CommonDecoder(handler, encoder), encoder, handler);
	}
}
