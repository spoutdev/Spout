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
package org.spout.engine.util.bans;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.event.server.BanChangeEvent;
import org.spout.api.util.ban.BanManager;

import org.spout.engine.SpoutConfiguration;
import org.spout.engine.SpoutServer;

/**
 * Implementation of BanManager that uses BanList.
 */
public class FlatFileBanManager implements BanManager {
	private final BanList bannedPlayers;
	private final BanList bannedIps;
	private final BanList whitelistedPlayers;
	private ChatArguments banMessage = new ChatArguments(ChatStyle.RED, "Your playername is banned from this server!");
	private ChatArguments ipBanMessage = new ChatArguments(ChatStyle.RED, "You are IP banned from this server!");
	private ChatArguments whitelistBanMessage = new ChatArguments(ChatStyle.RED, "You are not on the whitelist of this server!");

	public FlatFileBanManager(SpoutServer server) {
		bannedIps = new BanList(new File(server.getConfigFolder(), "banned_ips.txt"));
		whitelistedPlayers = new BanList(new File(server.getConfigFolder(), "whitelisted_players.txt"));
		bannedPlayers = new BanList(new File(server.getConfigFolder(), "banned_players.txt"));
	}

	@Override
	public void load() {
		bannedPlayers.load();
		whitelistedPlayers.load();
		bannedIps.load();
	}

	@Override
	public boolean isBanned(String player) {
		return bannedPlayers.contains(player);
	}

	@Override
	public boolean setBanned(String player, boolean banned) {
		BanChangeEvent event = Spout.getEventManager().callEvent(new BanChangeEvent(BanChangeEvent.BanType.PLAYER, player, banned));

		boolean alreadyBanned = !(isBanned(player) == event.isBanned());

		if (!event.isCancelled()) {
			if (banned) {
				bannedPlayers.add(player);
			} else {
				bannedPlayers.remove(player);
			}
		}

		return alreadyBanned;
	}

	/**
	 * Returns a string set of currently banned names
	 * @return
	 */
	@Override
	public Set<String> getBannedPlayers() {
		return Collections.unmodifiableSet(new HashSet<String>(bannedPlayers.getContents()));
	}

	@Override
	public ChatArguments getBanMessage() {
		return banMessage;
	}

	@Override
	public void setBanMessage(Object... message) {
		banMessage = new ChatArguments(message);
	}

	@Override
	public boolean isIpBanned(String address) {
		return bannedIps.contains(address);
	}

	@Override
	public boolean setIpBanned(String address, boolean banned) {
		BanChangeEvent event = Spout.getEventManager().callEvent(new BanChangeEvent(BanChangeEvent.BanType.IP, address, banned));

		boolean alreadyBanned = !(isIpBanned(address) == event.isBanned());

		if (!event.isCancelled()) {
			if (banned) {
				bannedIps.add(address);
			} else {
				bannedIps.remove(address);
			}
		}

		return alreadyBanned;
	}

	@Override
	public Set<String> getBannedIps() {
		return Collections.unmodifiableSet(new HashSet<String>(bannedIps.getContents()));
	}

	@Override
	public ChatArguments getIpBanMessage() {
		return ipBanMessage;
	}

	@Override
	public void setIpBanMessage(Object... message) {
		ipBanMessage = new ChatArguments(message);
	}

	@Override
	public boolean isWhitelisted(String player) {
		Boolean isWhitelisted = true;
		if (SpoutConfiguration.USE_WHITELIST.getBoolean() && !whitelistedPlayers.contains(player))
			isWhitelisted = false;
		return isWhitelisted;
	}

	@Override
	public ChatArguments getNotWhitelistedMessage() {
		return whitelistBanMessage;
	}

	@Override
	public boolean setWhitelisted(String player, boolean whitelisted) {
		BanChangeEvent event = Spout.getEventManager().callEvent(new BanChangeEvent(BanChangeEvent.BanType.WHITELIST, player, whitelisted));

		boolean alreadyWhitelisted = !(isWhitelisted(player) == event.isBanned());

		if (!event.isCancelled()) {
			if (whitelisted) {
				whitelistedPlayers.add(player);
			} else {
				whitelistedPlayers.remove(player);
			}
		}

		return alreadyWhitelisted;
	}

	@Override
	public Set<String> getWhitelist() {
		return Collections.unmodifiableSet(new HashSet<String>(whitelistedPlayers.getContents()));
	}

	@Override
	public void save() {
		bannedIps.save();
		bannedPlayers.save();
		whitelistedPlayers.save();
	}
}
