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
package org.spout.engine.util.access;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.entity.Player;
import org.spout.api.event.server.access.BanChangeEvent;
import org.spout.api.util.access.BanType;
import org.spout.api.event.server.access.WhitelistChangeEvent;
import org.spout.api.util.ListFile;
import org.spout.api.util.access.AccessManager;

/**
 * Implementation of AccessManager that uses PlayerListFiles.
 */
public class SpoutAccessManager implements AccessManager {
	private final ListFile bannedPlayers = new ListFile(new File("config/banned_players.txt"));
	private final ListFile bannedIps = new ListFile(new File("config/banned_ips.txt"));
	private final ListFile whitelist = new ListFile(new File("config/whitelist.txt"));
	private boolean whitelistEnabled = false;
	private ChatArguments banMessage = new ChatArguments(ChatStyle.RED, "You are banned from this server!");
	private ChatArguments ipBanMessage = new ChatArguments(ChatStyle.RED, "You are banned from this server!");
	private ChatArguments whitelistMessage = new ChatArguments(ChatStyle.RED, "You are not on the whitelist!");

	@Override
	public void load() {
		bannedPlayers.load();
		bannedIps.load();
		whitelist.load();
	}

	@Override
	public boolean isWhitelistEnabled() {
		return whitelistEnabled;
	}

	@Override
	public void setWhitelistEnabled(boolean enabled) {
		whitelistEnabled = enabled;
	}

	@Override
	public Collection<String> getWhitelistedPlayers() {
		return Collections.unmodifiableSet(new HashSet<String>(whitelist.getContents()));
	}

	@Override
	public boolean isWhitelisted(String player) {
		return whitelist.contains(player);
	}

	@Override
	public void whitelist(String player) {
		setWhitelisted(player, true);
	}

	@Override
	public void unwhitelist(String player) {
		unwhitelist(player, true);
	}

	@Override
	public void unwhitelist(String player, boolean kick) {
		unwhitelist(player, kick, (Object[])null);
	}

	@Override
	public void unwhitelist(String player, boolean kick, Object... reason) {
		Server server = (Server) Spout.getEngine();
		if (kick) {
			if (reason == null) {
				reason = new Object[] {ChatStyle.RED, "Unwhitelisted from server."};
			}
			Player p = server.getPlayer(player, true);
			if (p != null) {
				p.kick(reason);
			}
		}
	}

	private void setWhitelisted(String player, boolean whitelisted) {
		WhitelistChangeEvent event = Spout.getEventManager().callEvent(new WhitelistChangeEvent(player, whitelisted));
		if (event.isCancelled()) {
			return;
		}
		if (event.isWhitelisted()) {
			whitelist.add(player);
		} else {
			whitelist.remove(player);
		}
	}

	@Override
	public ChatArguments getWhitelistMessage() {
		return whitelistMessage;
	}

	@Override
	public void setWhitelistMessage(Object... message) {
		whitelistMessage = new ChatArguments(message);
	}

	@Override
	public void ban(BanType type, String s) {
		ban(type, s, true);
	}

	@Override
	public void ban(BanType type, String s, boolean kick) {
		ban(type, s, kick, (Object[])null);
	}

	@Override
	public void ban(BanType type, String s, boolean kick, Object... reason) {
		Server server = (Server) Spout.getEngine();
		if (kick) {
			if (reason == null) {
				reason = new Object[] {ChatStyle.RED, "Banned from server."};
			}
			if (type == BanType.PLAYER) {
				Player player = server.getPlayer(s, true);
				if (player != null) {
					player.kick(reason);
				}
			} else {
				for (Player player : server.getOnlinePlayers()) {
					if (player.getAddress().getHostAddress().equals(s)) {
						player.kick(reason);
					}
				}
			}
		}
		setBanned(type, s, true);
	}

	@Override
	public void unban(BanType type, String s) {
		setBanned(type, s, false);
	}

	@Override
	public Collection<String> getBanned(BanType type) {
		return Collections.unmodifiableSet(new HashSet<String>(type == BanType.PLAYER ? bannedPlayers.getContents() : bannedIps.getContents()));
	}

	@Override
	public boolean isBanned(BanType type, String s) {
		return type == BanType.PLAYER ? bannedPlayers.contains(s) : bannedIps.contains(s);
	}

	@Override
	public ChatArguments getBanMessage(BanType type) {
		return type == BanType.PLAYER ? banMessage : ipBanMessage;
	}

	@Override
	public void setBanMessage(BanType type, Object... message) {
		ChatArguments args = new ChatArguments(message);
		if (type == BanType.PLAYER) {
			banMessage = args;
		} else {
			ipBanMessage = args;
		}
	}

	private void setBanned(BanType type, String s, boolean banned) {
		BanChangeEvent event = Spout.getEventManager().callEvent(new BanChangeEvent(type, s, banned));
		if (event.isCancelled()) {
			return;
		}

		if (event.isBanned()) {
			if (type == BanType.PLAYER) {
				bannedPlayers.add(s);
			} else {
				bannedIps.add(s);
			}
		} else {
			if (type == BanType.PLAYER) {
				bannedPlayers.remove(s);
			} else {
				bannedIps.add(s);
			}
		}
	}
}
