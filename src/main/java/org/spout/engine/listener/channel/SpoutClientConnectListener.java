/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.engine.listener.channel;

import java.util.logging.Level;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import org.spout.api.protocol.CommonHandler;
import org.spout.api.protocol.PortBinding;

import org.spout.engine.SpoutClient;
import org.spout.engine.protocol.SpoutClientSession;

/**
 * A listener for SpoutClient connects
 */
public class SpoutClientConnectListener implements ChannelFutureListener {
	private final SpoutClient client;
	private final PortBinding binding;

	public SpoutClientConnectListener(SpoutClient client, PortBinding binding) {
		this.client = client;
		this.binding = binding;
	}

	@Override
	public void operationComplete(ChannelFuture channelFuture) throws Exception {
		Channel channel = channelFuture.getChannel();
		if (channelFuture.isSuccess()) {
			CommonHandler handler = channel.getPipeline().get(CommonHandler.class);
			SpoutClientSession session = client.newSession(channel);
			handler.setSession(session);
			client.setSession(session);
			session.send(true, true, binding.getProtocol().getIntroductionMessage(client.getActivePlayer().getName()));
		} else {
			client.getLogger().log(Level.WARNING, "Could not connect to " + binding, channelFuture.getCause());
		}
	}
}
