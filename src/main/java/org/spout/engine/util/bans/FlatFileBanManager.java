/*
 * This file is part of Spout (http://www.spout.org/).
 *
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
import java.util.HashSet;
import java.util.Set;

import org.spout.engine.SpoutServer;
import org.spout.engine.util.PlayerListFile;

/**
 * Implementation of BanManager that uses PlayerListFiles.
 */
public class FlatFileBanManager implements BanManager {
	private final PlayerListFile bannedNames;
	private final PlayerListFile bannedIps;

	public FlatFileBanManager(SpoutServer server) {
		bannedIps = new PlayerListFile(new File(server.getConfigDirectory(), "banned-ips.txt"));
		bannedNames = new PlayerListFile(new File(server.getConfigDirectory(), "banned-names.txt"));
	}

	@Override
	public void load() {
		bannedIps.load();
		bannedNames.load();
	}

	@Override
	public boolean isBanned(String player) {
		return bannedNames.contains(player);
	}

	@Override
	public boolean setBanned(String player, boolean banned) {
		boolean alreadyBanned = !(isBanned(player) == banned);
		if (banned) {
			bannedNames.add(player);
		} else {
			bannedNames.remove(player);
		}
		return alreadyBanned;
	}

	@Override
	public Set<String> getBans() {
		return new HashSet<String>(bannedNames.getContents());
	}

	@Override
	public String getBanMessage(String name) {
		return "You are banned from this server";
	}

	@Override
	public boolean isIpBanned(String address) {
		return bannedIps.contains(address);
	}

	@Override
	public boolean setIpBanned(String address, boolean banned) {
		boolean alreadyBanned = !(isIpBanned(address) == banned);
		if (banned) {
			bannedIps.add(address);
		} else {
			bannedIps.remove(address);
		}
		return alreadyBanned;
	}

	@Override
	public Set<String> getIpBans() {
		return new HashSet<String>(bannedIps.getContents());
	}

	@Override
	public String getIpBanMessage(String address) {
		return "You are banned from this server";
	}

	@Override
	public boolean isBanned(String player, String address) {
		return isBanned(player) || isIpBanned(address);
	}
}
