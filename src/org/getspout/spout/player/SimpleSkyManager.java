/*
 * This file is part of Spout (http://wiki.getspout.org/).
 * 
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.spout.player;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.Color;
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
	private final HashMap<String, Color> skyColor = new HashMap<String, Color>();
	private final HashMap<String, Color> fogColor = new HashMap<String, Color>();
	private final HashMap<String, Color> cloudColor = new HashMap<String, Color>();

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
		if (player.isSpoutCraftEnabled()) {
			player.sendPacket(new PacketSky(y, 0, 0 ,0));
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
		if (player.isSpoutCraftEnabled()) {
			player.sendPacket(new PacketSky(0, frequency, 0 ,0));
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
		if (player.isSpoutCraftEnabled()) {
			player.sendPacket(new PacketSky(0, 0, percent, 0));
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
			if (player.isSpoutCraftEnabled()) {
				player.sendPacket(new PacketSky("[reset]", ""));
			}
		}
		else {
			checkUrl(Url);
			sunUrl.put(player.getName(), Url);
			if (player.isSpoutCraftEnabled()) {
				player.sendPacket(new PacketSky(Url, ""));
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
		if (player.isSpoutCraftEnabled()) {
			player.sendPacket(new PacketSky(0, 0, 0, percent));
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
			if (player.isSpoutCraftEnabled()) {
				player.sendPacket(new PacketSky("", "[reset]"));
			}
		}
		else {
			checkUrl(Url);
			moonUrl.put(player.getName(), Url);
			if (player.isSpoutCraftEnabled()) {
				player.sendPacket(new PacketSky("", Url));
			}
		}
	}
	

	@Override
	public void setSkyColor(SpoutPlayer player, Color skycolor) {
		skyColor.put(player.getName(), skycolor);
		player.sendPacket(new PacketSky(skycolor, null, null));
	}

	@Override
	public Color getSkyColor(SpoutPlayer player) {
		return skyColor.get(player.getName());
	}
	
	@Override
	public void setFogColor(SpoutPlayer player, Color fogColor) {
		this.fogColor.put(player.getName(), fogColor);
		player.sendPacket(new PacketSky(null, fogColor, null));
	}

	@Override
	public Color getFogColor(SpoutPlayer player) {
		return fogColor.get(player.getName());
	}

	@Override
	public void setCloudColor(SpoutPlayer player, Color cloudColor) {
		this.cloudColor.put(player.getName(), cloudColor);
		player.sendPacket(new PacketSky(null, null, cloudColor));
	}

	@Override
	public Color getCloudColor(SpoutPlayer player) {
		return cloudColor.get(player.getName());
	}
	
	public void onPlayerJoin(SpoutPlayer player) {
		if (player.isSpoutCraftEnabled()) {
			String moon = getMoonTextureUrl(player);
			moon = moon == null ? "" : moon;
			String sun = getSunTextureUrl(player);
			sun = sun == null ? "" : sun;
			player.sendPacket(new PacketSky(getRealCloudHeight(player), getStarFrequency(player), getSunSizePercent(player), getMoonSizePercent(player), getSkyColor(player), getFogColor(player), getCloudColor(player), sun, moon));
		}
	}
	
	public void reset() {
		cloudHeight.clear();
		starFrequency.clear();
		sunPercent.clear();
		moonPercent.clear();
		sunUrl.clear();
		moonUrl.clear();
		skyColor.clear();
		fogColor.clear();
		cloudColor.clear();
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player instanceof SpoutPlayer) {
				if (((SpoutPlayer)player).isSpoutCraftEnabled()) {
					((SpoutPlayer)player).sendPacket(new PacketSky(108, 1500, 100, 100, new Color(-2,-2,-2), new Color(-2,-2,-2), new Color(-2,-2,-2), "[reset]", "[reset]"));
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
