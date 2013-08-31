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

import java.util.logging.Level;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import org.spout.api.Spout;
import org.spout.api.event.player.PlayerKickEvent;
import org.spout.api.event.player.PlayerLeaveEvent;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.ServerSession;
import org.spout.engine.SpoutServer;
import org.spout.engine.entity.SpoutPlayer;

/**
 * SpoutSession for servers
 */
public class SpoutServerSession<T extends SpoutServer> extends SpoutSession<T> implements ServerSession {
	public SpoutServerSession(T engine, Channel channel, Protocol bootstrapProtocol) {
		super(engine, channel, bootstrapProtocol);
	}

	public String getDefaultLeaveMessage() {
		if (getPlayer() == null) {
			return "Unknown player has left the game";
		} else {
			return getPlayer().getDisplayName() + " has left the game";
		}
	}

	// TODO why is this not is SpoutSession
	@Override
	public boolean disconnect(String reason) {
		return disconnect(false, reason);
	}

	public boolean disconnect(boolean stopping, String reason) {
		if (getChannel().isActive()) {
			if (isDisconnected) {
				throw new IllegalStateException("Channel is active but disconnect has already been called.");
			}
			if (getPlayer() != null) {
				PlayerLeaveEvent event;
				if (stopping) {
					event = getEngine().getEventManager().callEvent(new PlayerLeaveEvent(getPlayer(), getDefaultLeaveMessage()));
				} else {
					event = getEngine().getEventManager().callEvent(new PlayerKickEvent(getPlayer(), getDefaultLeaveMessage(), reason));
					if (event.isCancelled()) {
						return false;
					}
					reason = ((PlayerKickEvent) event).getKickReason();
					getEngine().getCommandSource().sendMessage("DEBUG (not duplicate): Player " + getPlayer().getName() + " kicked: " + reason);
				}
				broadcastLeaveMessage(event);
			}

			Protocol protocol = getProtocol();
			Message kickMessage = protocol == null ? null : protocol.getKickMessage(reason);

			if (kickMessage != null) {
				getChannel().writeAndFlush(kickMessage).addListener(ChannelFutureListener.CLOSE);
			} else {
				getChannel().close();
			}
			dispose(stopping);
		} else if (!isDisconnected) {
			if (getPlayer() != null) {
				broadcastLeaveMessage(getEngine().getEventManager().callEvent(new PlayerLeaveEvent(getPlayer(), getDefaultLeaveMessage())));
			}
			dispose(false);
		}
		isDisconnected = true;
		return true;
	}

	private void broadcastLeaveMessage(PlayerLeaveEvent leaveEvent) {
		String msg = leaveEvent.getMessage();
		if (msg != null) {
			getEngine().broadcastMessage(msg);
		}
	}

	private void dispose(boolean isStopping) {
		SpoutPlayer player = getPlayer();
		if (player == null) {
			return;
		}

		try {
			player.disconnect(!isStopping); //can not save async if the engine is stopping
		} catch (Exception e) {
			Spout.getLogger().log(Level.WARNING, "Did not disconnect " + player.getName() + " cleanly", e);
		}
	}
}
