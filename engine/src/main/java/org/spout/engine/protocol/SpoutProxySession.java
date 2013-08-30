/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.engine.protocol;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import org.spout.api.Spout;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.proxy.ConnectionInfo;
import org.spout.api.protocol.proxy.ConnectionInfoMessage;
import org.spout.api.protocol.proxy.ProxyStartMessage;
import org.spout.api.protocol.proxy.RedirectMessage;
import org.spout.api.protocol.proxy.TransformableMessage;
import org.spout.engine.SpoutProxy;

/**
 * A SpoutSession setup for proxies
 */
public class SpoutProxySession extends SpoutServerSession<SpoutProxy> {
	/**
	 * Information about the connection required for proxying
	 */
	private final AtomicReference<ConnectionInfo> channelInfo = new AtomicReference<>();
	/**
	 * The aux channel for proxy connections. This sends messages upstream.
	 */
	private final AtomicReference<Channel> auxChannel = new AtomicReference<>();
	/**
	 * Information about the connection required for proxying
	 */
	private final AtomicReference<ConnectionInfo> auxChannelInfo = new AtomicReference<>();
	/**
	 * Indicated if the session is in passthrough proxy mode
	 */
	private final AtomicBoolean passthrough = new AtomicBoolean(false);
	/**
	 * Indicates the number of times the proxy has connected to a server for this session
	 */
	private final AtomicInteger connects = new AtomicInteger(0);

	public SpoutProxySession(SpoutProxy engine, Channel channel, Protocol bootstrapProtocol) {
		super(engine, channel, bootstrapProtocol);
	}

	/**
	 * For proxy, the main channel is downstream and the auxChannel is upstream.
	 */
	@Override
	public void send(SendType type, Message message) {
		if (message instanceof ConnectionInfoMessage) {
			updateConnectionInfo(false, (ConnectionInfoMessage) message);
		}
		super.send(type, message);
	}

	public void sendOutbound(Message message) {
		if (message instanceof ConnectionInfoMessage) {
			updateConnectionInfo(true, (ConnectionInfoMessage) message);
		}
		Channel auxChannel = this.auxChannel.get();
		if (auxChannel == null) {
			Spout.getLogger().warning("Attempt made to send data to an unconnected channel");
			return;
		}
		auxChannel.writeAndFlush(message);
	}

	@Override
	public void messageReceived(Message message) {
		if (message instanceof ConnectionInfoMessage) {
			updateConnectionInfo(false, (ConnectionInfoMessage) message);
		}
		if (passthrough.get()) {
			if (message instanceof TransformableMessage) {
				message = ((TransformableMessage) message).transform(true, connects.get(), channelInfo.get(), auxChannelInfo.get());
			}
			sendOutbound(message);
			return;
		}
		super.messageReceived(message);
	}

	@Override
	public void messageReceivedOnAuxChannel(Channel auxChannel, Message message) {
		if (message instanceof ConnectionInfoMessage) {
			updateConnectionInfo(true, (ConnectionInfoMessage) message);
		}
		if (message instanceof ProxyStartMessage) {
			passthrough.compareAndSet(false, true);
		} else if (message instanceof RedirectMessage) {
			RedirectMessage redirect = (RedirectMessage) message;
			if (redirect.isRedirect()) {
				closeAuxChannel(true, "Redirect received");
				auxChannelInfo.set(null);
				ConnectionInfo info = channelInfo.get();
				if (info != null) {
					passthrough.set(false);
					getEngine().connect(redirect.getHostname(), redirect.getPort(), info.getIdentifier(), this);
					return;
				}
			}
		}
		if (passthrough.get()) {
			if (message instanceof TransformableMessage) {
				message = ((TransformableMessage) message).transform(false, connects.get(), channelInfo.get(), auxChannelInfo.get());
			}
			send(message);
		}
	}

	@Override
	public boolean disconnect(String reason) {
		boolean result = super.disconnect(reason);
		closeAuxChannel(false, reason);
		return result;
	}

	@Override
	public void bindAuxChannel(Channel c) {
		if (c == null) {
			throw new IllegalArgumentException("Channel may not be null");
		} else if (!auxChannel.compareAndSet(null, c)) {
			throw new IllegalStateException("Aux channel may not be set without closing the previously bound channel");
		} else {
			connects.incrementAndGet();
		}
		System.out.println("Binding: " + c + " " + connects.get());
	}

	@Override
	public void closeAuxChannel() {
		closeAuxChannel(true);
	}

	private void closeAuxChannel(boolean openedExpected) {
		closeAuxChannel(openedExpected, "Closing aux channel");
	}

	private void closeAuxChannel(boolean openedExpected, String message) {
		Channel c = auxChannel.getAndSet(null);
		if (c != null) {
			Message kickMessage = null;
			Protocol p = getProtocol();
			if (p != null) {
				kickMessage = p.getKickMessage(message);
			}
			if (kickMessage != null) {
				c.writeAndFlush(kickMessage).addListener(ChannelFutureListener.CLOSE);
			} else {
				c.close();
			}
		} else if (openedExpected) {
			throw new IllegalStateException("Attempt made to close aux channel when no aux channel was bound");
		}
	}

	private void updateConnectionInfo(boolean auxChannel, ConnectionInfoMessage info) {
		AtomicReference<ConnectionInfo> ref = auxChannel ? auxChannelInfo : channelInfo;
		boolean success = false;
		while (!success) {
			ConnectionInfo oldInfo = ref.get();
			ConnectionInfo newInfo = info.getConnectionInfo(oldInfo);
			success = ref.compareAndSet(oldInfo, newInfo);
		}
	}
}
