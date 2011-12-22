package org.getspout.unchecked.server;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

/**
 * Represents a player which is not connected to the server.
 */
@SerializableAs("Player")
public class SpoutOfflinePlayer implements OfflinePlayer {
	private final SpoutServer server;
	private final String name;

	public SpoutOfflinePlayer(SpoutServer server, String name) {
		this.server = server;
		this.name = name;
	}

	@Override
	public boolean isOnline() {
		return false;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isBanned() {
		return server.getBanManager().isBanned(name);
	}

	@Override
	public void setBanned(boolean banned) {
		server.getBanManager().setBanned(name, banned);
	}

	@Override
	public boolean isWhitelisted() {
		return server.hasWhitelist() && server.getWhitelist().contains(name);
	}

	@Override
	public boolean hasPlayedBefore() {
		throw new UnsupportedOperationException("not suppored yet");
	}

	@Override
	public long getLastPlayed() {
		throw new UnsupportedOperationException("not suppored yet");
	}

	@Override
	public long getFirstPlayed() {
		throw new UnsupportedOperationException("not suppored yet");
	}

	@Override
	public void setWhitelisted(boolean value) {
		if (value) {
			server.getWhitelist().add(name);
		} else {
			server.getWhitelist().remove(name);
		}
	}

	@Override
	public Player getPlayer() {
		return server.getPlayerExact(name);
	}

	@Override
	public boolean isOp() {
		return server.getOpsList().contains(name);
	}

	@Override
	public void setOp(boolean value) {
		if (value) {
			server.getOpsList().add(name);
		} else {
			server.getOpsList().remove(name);
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> ret = new HashMap<String, Object>();

		ret.put("name", name);
		return ret;
	}

	public static OfflinePlayer deserialize(Map<String, Object> val) {
		return Bukkit.getServer().getOfflinePlayer(val.get("name").toString());
	}
}
