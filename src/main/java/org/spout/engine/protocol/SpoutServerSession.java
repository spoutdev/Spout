/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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

import java.util.List;
import java.util.logging.Level;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;

import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.event.player.PlayerKickEvent;
import org.spout.api.event.player.PlayerLeaveEvent;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.Protocol;

import org.spout.engine.SpoutServer;
import org.spout.engine.entity.SpoutPlayer;

/**
 * SpoutSession for servers
 */
public class SpoutServerSession<T extends SpoutServer> extends SpoutSession<T> {
	public SpoutServerSession(T engine, Channel channel, Protocol bootstrapProtocol) {
		super(engine, channel, bootstrapProtocol);
	}

	@Override
	public void send(boolean upstream, boolean force, Message message) {
		if (upstream) {
			getEngine().getLogger().warning("Attempt made to send packet to server");
			return;
		}
		super.send(upstream, force, message);
	}

	@Override
	public boolean disconnect(Object... reason) {
		return disconnect(true, reason);
	}

	public Object[] getDefaultLeaveMessage() {
		if (getPlayer() == null) {
			return new Object[]{ChatStyle.WHITE, "Unknown", ChatStyle.CYAN, " has left the game"};
		} else {
			return new Object[]{ChatStyle.WHITE, getPlayer().getDisplayName(), ChatStyle.CYAN, " has left the game"};
		}
	}

	@Override
	public boolean disconnect(boolean kick, Object... reason) {
		if (getPlayer() != null) {
			PlayerLeaveEvent event;
			if (kick) {
				event = getEngine().getEventManager().callEvent(new PlayerKickEvent(getPlayer(), getDefaultLeaveMessage(), reason));
				if (event.isCancelled()) {
					return false;
				}
				List<Object> args = ((PlayerKickEvent) event).getKickReason().getArguments();
				reason = args.toArray(new Object[args.size()]);
				getEngine().getCommandSource().sendMessage("Player ", getPlayer().getName(), " kicked: ", reason);
			} else {
				event = new PlayerLeaveEvent(getPlayer(), getDefaultLeaveMessage());
			}
			dispose(event);
		}
		Protocol protocol = getProtocol();
		Message kickMessage = null;
		if (protocol != null) {
			kickMessage = protocol.getKickMessage(new ChatArguments(reason));
		}
		if (kickMessage != null) {
			channel.write(kickMessage).addListener(ChannelFutureListener.CLOSE);
		} else {
			channel.close();
		}
		return true;
	}

	@Override
	public void dispose() {
		super.dispose();
		dispose(new PlayerLeaveEvent(getPlayer(), getDefaultLeaveMessage()));
	}

	public void dispose(PlayerLeaveEvent leaveEvent) {
		SpoutPlayer player;
		if ((player = this.player.getAndSet(null)) != null) {
			if (!leaveEvent.hasBeenCalled()) {
				getEngine().getEventManager().callEvent(leaveEvent);
			}

			ChatArguments text = leaveEvent.getMessage();
			if (text != null && text.getArguments().size() > 0) {
				getEngine().broadcastMessage(text);
			}
			player.save();
			try {
				player.disconnect();
			} catch (Exception e) {
				Spout.getLogger().log(Level.WARNING, "Did not disconnect " + player.getName() + " cleanly", e);
			}
		}
	}
}
