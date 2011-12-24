package org.getspout.server.util.bans;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.getspout.server.util.PlayerListFile;
import org.getspout.unchecked.server.SpoutServer;

/**
 * Implementation of BanManager that uses PlayerListFiles.
 */
public class FlatFileBanManager implements BanManager {
	private final PlayerListFile bannedNames;
	private final PlayerListFile bannedIps;

	public FlatFileBanManager(SpoutServer server) {
		bannedIps = new PlayerListFile(new File(server.getConfigDir(), "banned-ips.txt"));
		bannedNames = new PlayerListFile(new File(server.getConfigDir(), "banned-names.txt"));
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
