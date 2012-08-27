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
import org.spout.api.event.server.BanChangeEvent.BanType;
import org.spout.api.util.ban.BanManager;

/**
 * Implementation of BanManager that uses PlayerListFiles.
 */
public class FlatFileBanManager implements BanManager {
	private final BanList players = new BanList(new File("config/banned_players.txt"));
	private final BanList ips = new BanList(new File("config/banned_ips.txt"));
	private ChatArguments banMessage = new ChatArguments(ChatStyle.RED, "You are banned from this server!");
	private ChatArguments ipBanMessage = new ChatArguments(ChatStyle.RED, "You are banned from this server!");

	@Override
	public void load() {
		players.load();
		ips.load();
	}

	@Override
	public boolean isBanned(String player) {
		return players.contains(player);
	}

	@Override
	public void setBanned(String player, boolean banned) {
		BanChangeEvent event = Spout.getEventManager().callEvent(new BanChangeEvent(BanType.PLAYER, player, banned));
		banned = event.isBanned();
		player = event.getChanged();
		if (banned) {
			players.add(player);
		} else {
			players.remove(player);
		}
	}

	@Override
	public Set<String> getBannedPlayers() {
		return Collections.unmodifiableSet(new HashSet<String>(players.getContents()));
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
		return ips.contains(address);
	}

	@Override
	public void setIpBanned(String address, boolean banned) {
		BanChangeEvent event = Spout.getEventManager().callEvent(new BanChangeEvent(BanType.IP, address, banned));
		address = event.getChanged();
		banned = event.isBanned();
		if (banned) {
			ips.add(address);
		} else {
			ips.remove(address);
		}
	}

	@Override
	public Set<String> getBannedIps() {
		return Collections.unmodifiableSet(new HashSet<String>(ips.getContents()));
	}

	@Override
	public ChatArguments getIpBanMessage() {
		return ipBanMessage;
	}

	@Override
	public void setIpBanMessage(Object... message) {
		ipBanMessage = new ChatArguments(message);
	}
}
