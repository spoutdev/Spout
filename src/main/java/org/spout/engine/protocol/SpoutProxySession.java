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
package org.spout.engine.protocol;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.Message;
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
	private final AtomicReference<ConnectionInfo> channelInfo = new AtomicReference<ConnectionInfo>();
	/**
	 * The aux channel for proxy connections
	 */
	private final AtomicReference<Channel> auxChannel = new AtomicReference<Channel>();
	/**
	 * Information about the connection required for proxying
	 */
	private final AtomicReference<ConnectionInfo> auxChannelInfo = new AtomicReference<ConnectionInfo>();
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

	@Override
	public void send(boolean upstream, boolean force, Message message) {
		if (message == null) {
			return;
		}

		try {
			if (message instanceof ConnectionInfoMessage) {
				updateConnectionInfo(upstream, !upstream, (ConnectionInfoMessage) message);
			}
			if (upstream) {
				Channel auxChannel = this.auxChannel.get();
				if (auxChannel == null) {
					Spout.getLogger().warning("Attempt made to send data to an unconnected channel");
					return;
				}
				auxChannel.write(message);
			} else {
				super.send(upstream, force, message);
			}
		} catch (Exception e) {
			disconnect(false, new Object[] {"Socket Error!"});
		}
	}

	@Override
	public void messageReceived(boolean upstream, Message message) {
		if (message instanceof ConnectionInfoMessage) {
			updateConnectionInfo(upstream, upstream, (ConnectionInfoMessage) message);
		}
		if (upstream) {
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
						((SpoutProxy) getEngine()).connect(redirect.getHostname(), redirect.getPort(), info.getIdentifier(), this);
						return;
					}
				}
			}
		}
		if (passthrough.get()) {
			if (message instanceof TransformableMessage) {
				message = ((TransformableMessage) message).transform(upstream, connects.get(), channelInfo.get(), auxChannelInfo.get());
			}
			send(!upstream, true, message);
			return;
		}
		super.messageReceived(upstream, message);
	}

	@Override
	public boolean disconnect(boolean kick, Object... reason) {
		boolean result = super.disconnect(kick, reason);
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

	private void closeAuxChannel(boolean openedExpected, Object... message) {
		Channel c = auxChannel.getAndSet(null);
		if (c != null) {
			Message kickMessage = null;
			Protocol p = getProtocol();
			if (p != null) {
				kickMessage = p.getKickMessage(new ChatArguments(message));
			}
			if (kickMessage != null) {
				c.write(kickMessage).addListener(ChannelFutureListener.CLOSE);
			} else {
				c.close();
			}
		} else if (openedExpected) {
			throw new IllegalStateException("Attempt made to close aux channel when no aux channel was bound");
		}
	}

	private void updateConnectionInfo(boolean auxChannel, boolean upstream, ConnectionInfoMessage info) {
		AtomicReference<ConnectionInfo> ref = auxChannel ? auxChannelInfo : channelInfo;
		boolean success = false;
		while (!success) {
			ConnectionInfo oldInfo = ref.get();
			ConnectionInfo newInfo = info.getConnectionInfo(upstream, oldInfo);
			success = ref.compareAndSet(oldInfo, newInfo);
		}
	}
}
