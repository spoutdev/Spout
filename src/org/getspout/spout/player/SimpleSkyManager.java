package org.getspout.spout.player;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.packet.PacketSky;
import org.getspout.spoutapi.player.SkyManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SimpleSkyManager implements SkyManager{
	private final HashMap<String, Integer> cloudHeight = new HashMap<String, Integer>();
	private final HashMap<String, Integer> starFrequency = new HashMap<String, Integer>();
	private final HashMap<String, Integer> sunPercent = new HashMap<String, Integer>();
	private final HashMap<String, Integer> moonPercent = new HashMap<String, Integer>();
	private final HashMap<String, String> sunUrl = new HashMap<String, String>();
	private final HashMap<String, String> moonUrl = new HashMap<String, String>();

	@Override
	public int getCloudHeight(SpoutPlayer player) {
		if (cloudHeight.containsKey(player.getName())) {
			return cloudHeight.get(player.getName());
		}
		return 108;
	}

	@Override
	public void setCloudHeight(SpoutPlayer player, int y) {
		cloudHeight.put(player.getName(), y);
		if (player.getVersion() > 8) {
			((SpoutCraftPlayer)player).sendPacket(new PacketSky(y, 0, 0 ,0));
		}
	}

	@Override
	public boolean isCloudsVisible(SpoutPlayer player) {
		if (cloudHeight.containsKey(player.getName())) {
			return cloudHeight.get(player.getName()) > -1;
		}
		return true;
	}

	@Override
	public void setCloudsVisible(SpoutPlayer player, boolean visible) {
		if (isCloudsVisible(player) != visible) {
			setCloudHeight(player, visible ? 108 : -1);
		}
	}

	@Override
	public int getStarFrequency(SpoutPlayer player) {
		if (starFrequency.containsKey(player.getName())) {
			return starFrequency.get(player.getName());
		}
		return 1500;
	}

	@Override
	public void setStarFrequency(SpoutPlayer player, int frequency) {
		starFrequency.put(player.getName(), frequency);
		if (player.getVersion() > 8) {
			((SpoutCraftPlayer)player).sendPacket(new PacketSky(0, frequency, 0 ,0));
		}
	}

	@Override
	public boolean isStarsVisible(SpoutPlayer player) {
		if (starFrequency.containsKey(player.getName())) {
			return starFrequency.get(player.getName()) > -1;
		}
		return true;
	}

	@Override
	public void setStarsVisible(SpoutPlayer player, boolean visible) {
		if (isStarsVisible(player) != visible) {
			setStarFrequency(player, visible ? 1500 : -1);
		}
	}

	@Override
	public int getSunSizePercent(SpoutPlayer player) {
		if (sunPercent.containsKey(player.getName())) {
			return sunPercent.get(player.getName());
		}
		return 100;
	}

	@Override
	public void setSunSizePercent(SpoutPlayer player, int percent) {
		sunPercent.put(player.getName(), percent);
		if (player.getVersion() > 8) {
			((SpoutCraftPlayer)player).sendPacket(new PacketSky(0, 0, percent, 0));
		}
	}

	@Override
	public boolean isSunVisible(SpoutPlayer player) {
		if (sunPercent.containsKey(player.getName())) {
			return sunPercent.get(player.getName()) > -1;
		}
		return true;
	}

	@Override
	public void setSunVisible(SpoutPlayer player, boolean visible) {
		if (isSunVisible(player) != visible) {
			setSunSizePercent(player, visible ? 100 : -1);
		}
	}

	@Override
	public String getSunTextureUrl(SpoutPlayer player) {
		if (sunUrl.containsKey(player.getName())) {
			return sunUrl.get(player.getName());
		}
		return null;
	}

	@Override
	public void setSunTextureUrl(SpoutPlayer player, String Url) {
		if (Url == null) {
			sunUrl.remove(player.getName());
			if (player.getVersion() > 8) {
				((SpoutCraftPlayer)player).sendPacket(new PacketSky("[reset]", ""));
			}
		}
		else {
			checkUrl(Url);
			sunUrl.put(player.getName(), Url);
			if (player.getVersion() > 8) {
				((SpoutCraftPlayer)player).sendPacket(new PacketSky(Url, ""));
			}
		}
	}

	@Override
	public int getMoonSizePercent(SpoutPlayer player) {
		if (moonPercent.containsKey(player.getName())) {
			return moonPercent.get(player.getName());
		}
		return 100;
	}

	@Override
	public void setMoonSizePercent(SpoutPlayer player, int percent) {
		moonPercent.put(player.getName(), percent);
		if (player.getVersion() > 8) {
			((SpoutCraftPlayer)player).sendPacket(new PacketSky(0, 0, 0, percent));
		}
	}

	@Override
	public boolean isMoonVisible(SpoutPlayer player) {
		if (moonPercent.containsKey(player.getName())) {
			return moonPercent.get(player.getName()) > -1;
		}
		return true;
	}

	@Override
	public void setMoonVisible(SpoutPlayer player, boolean visible) {
		if (isMoonVisible(player) != visible) {
			setMoonSizePercent(player, visible ? 100 : -1);
		}
	}

	@Override
	public String getMoonTextureUrl(SpoutPlayer player) {
		if (moonUrl.containsKey(player.getName())) {
			return moonUrl.get(player.getName());
		}
		return null;
	}

	@Override
	public void setMoonTextureUrl(SpoutPlayer player, String Url) {
		if (Url == null) {
			moonUrl.remove(player.getName());
			if (player.getVersion() > 8) {
				((SpoutCraftPlayer)player).sendPacket(new PacketSky("", "[reset]"));
			}
		}
		else {
			checkUrl(Url);
			moonUrl.put(player.getName(), Url);
			if (player.getVersion() > 8) {
				((SpoutCraftPlayer)player).sendPacket(new PacketSky("", Url));
			}
		}
	}
	
	public void onPlayerJoin(SpoutPlayer player) {
		if (player.getVersion() > 8) {
			String moon = getMoonTextureUrl(player);
			moon = moon == null ? "" : moon;
			String sun = getSunTextureUrl(player);
			sun = sun == null ? "" : sun;
			((SpoutCraftPlayer)player).sendPacket(new PacketSky(getRealCloudHeight(player), getStarFrequency(player), getSunSizePercent(player), getMoonSizePercent(player), sun, moon));
		}
	}
	
	public void reset() {
		cloudHeight.clear();
		starFrequency.clear();
		sunPercent.clear();
		moonPercent.clear();
		sunUrl.clear();
		moonUrl.clear();
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player instanceof SpoutPlayer) {
				if (((SpoutCraftPlayer)player).getVersion() > 8) {
					((SpoutCraftPlayer)player).sendPacket(new PacketSky(108, 1500, 100, 100, "[reset]", "[reset]"));
				}
			}
		}
	}
	
	private int getRealCloudHeight(SpoutPlayer player) {
		if (cloudHeight.containsKey(player.getName())) {
			return cloudHeight.get(player.getName());
		}
		return -999; //special value tells the client to use the default client values
	}
	
	private void checkUrl(String Url) {
		if (Url == null || Url.length() < 5) {
			throw new UnsupportedOperationException("Invalid URL");
		}
		if (!Url.substring(Url.length() - 4, Url.length()).equalsIgnoreCase(".png")) {
			throw new UnsupportedOperationException("All textures must be a PNG image");
		}
		if (Url.length() > 255) {
			throw new UnsupportedOperationException("All Url's must be shorter than 256 characters");
		}
	}

}
