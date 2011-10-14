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
package org.getspout.spout.sound;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.event.sound.BackgroundMusicEvent;
import org.getspout.spoutapi.packet.PacketDownloadMusic;
import org.getspout.spoutapi.packet.PacketPlaySound;
import org.getspout.spoutapi.packet.PacketStopMusic;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.Music;
import org.getspout.spoutapi.sound.SoundEffect;
import org.getspout.spoutapi.sound.SoundManager;


public class SimpleSoundManager implements SoundManager{

	@Override
	public void playGlobalSoundEffect(SoundEffect effect) {
		playGlobalSoundEffect(effect, null);
	}

	@Override
	public void playGlobalSoundEffect(SoundEffect effect, Location location) {
		playGlobalSoundEffect(effect, location, 16);
	}
	
	@Override
	public void playGlobalSoundEffect(SoundEffect effect, Location location, int distance) {
		playGlobalSoundEffect(effect, location, distance, 16);
	}

	@Override
	public void playGlobalSoundEffect(SoundEffect effect, Location location, int distance, int volumePercent) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			playSoundEffect((SpoutCraftPlayer) SpoutCraftPlayer.getPlayer(player), effect, location, distance, volumePercent);
		}
	}

	@Override
	public void playSoundEffect(SpoutPlayer target, SoundEffect effect) {
		playSoundEffect(target, effect, null);
	}

	@Override
	public void playSoundEffect(SpoutPlayer target, SoundEffect effect, Location location) {
		playSoundEffect(target, effect, location, 16);
	}
	
	@Override
	public void playSoundEffect(SpoutPlayer target, SoundEffect effect, Location location, int distance) {
		playSoundEffect(target, effect, location, distance, 16);
	}

	@Override
	public void playSoundEffect(SpoutPlayer target, SoundEffect effect, Location location, int distance, int volumePercent) {
		if (target.isSpoutCraftEnabled()) {
			SpoutCraftPlayer ccp = (SpoutCraftPlayer) target;
			if (location == null || ccp.getWorld().equals(location.getWorld())) {
				if (location == null) {
					ccp.sendPacket(new PacketPlaySound(effect, distance, volumePercent));
				}
				else {
					ccp.sendPacket(new PacketPlaySound(effect, location, distance, volumePercent));
				}
			}
		}
	}

	@Override
	public void playGlobalMusic(Music music) {
		playGlobalMusic(music, 100);
	}

	@Override
	public void playGlobalMusic(Music music, int volumePercent) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			playMusic((SpoutCraftPlayer) SpoutCraftPlayer.getPlayer(player), music, volumePercent);
		}
	}

	@Override
	public void playMusic(SpoutPlayer target, Music music) {
		playMusic(target, music, 100);
	}

	@Override
	public void playMusic(SpoutPlayer target, Music music, int volumePercent) {
		if (target.isSpoutCraftEnabled()) {
			BackgroundMusicEvent event = new BackgroundMusicEvent(music, volumePercent, target);
			Bukkit.getServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				return;
			}
			
			((SpoutCraftPlayer) target).sendPacket(new PacketPlaySound(music, event.getVolumePercent()));
		}
	}
	
	@Override
	public void stopMusic(SpoutPlayer target) {
		stopMusic(target, true);
	}
	
	@Override
	public void stopMusic(SpoutPlayer target, boolean resetTimer) {
		stopMusic(target, true, -1);
	}
	
	@Override
	public void stopMusic(SpoutPlayer target, boolean resetTimer, int fadeOutTime) {
		if (target.isSpoutCraftEnabled()) {
			((SpoutCraftPlayer) target).sendPacket(new PacketStopMusic(resetTimer, fadeOutTime));
		}
	}
	
	@Override
	public void playGlobalCustomMusic(Plugin plugin, String Url, boolean notify) {
		playGlobalCustomMusic(plugin, Url, notify, null);
	}
	
	@Override
	public void playGlobalCustomMusic(Plugin plugin, String Url, boolean notify, Location location) {
		playGlobalCustomMusic(plugin, Url, notify, location, 16);
	}
	
	@Override
	public void playGlobalCustomMusic(Plugin plugin, String Url, boolean notify, Location location, int distance) {
		playGlobalCustomMusic(plugin, Url, notify, location, distance, 100);
	}

	@Override
	public void playGlobalCustomMusic(Plugin plugin, String Url, boolean notify, Location location, int distance, int volumePercent) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			playCustomMusic(plugin, (SpoutCraftPlayer) SpoutCraftPlayer.getPlayer(player), Url, notify, location, distance, volumePercent);
		}
	}

	@Override
	public void playCustomMusic(Plugin plugin, SpoutPlayer target, String Url, boolean notify) {
		playCustomMusic(plugin, target, Url, notify, null);
	}
	
	@Override
	public void playCustomMusic(Plugin plugin, SpoutPlayer target, String Url, boolean notify, Location location) {
		playCustomMusic(plugin, target, Url, notify, location, 16);
	}
	
	@Override
	public void playCustomMusic(Plugin plugin, SpoutPlayer target, String Url, boolean notify, Location location, int distance) {
		playCustomMusic(plugin, target, Url, notify, location, distance, 100);
	}

	@Override
	public void playCustomMusic(Plugin plugin, SpoutPlayer target, String Url, boolean notify, Location location, int distance, int volumePercent) {
		playCustomFile(plugin, target, Url, notify, location, distance, volumePercent, false);
	}
	
	@Override
	public void playGlobalCustomSoundEffect(Plugin plugin, String Url, boolean notify) {
		playGlobalCustomSoundEffect(plugin, Url, notify, null);
	}
	
	@Override
	public void playGlobalCustomSoundEffect(Plugin plugin, String Url, boolean notify, Location location) {
		playGlobalCustomSoundEffect(plugin, Url, notify, location, 16);
	}
	
	@Override
	public void playGlobalCustomSoundEffect(Plugin plugin, String Url, boolean notify, Location location, int distance) {
		playGlobalCustomSoundEffect(plugin, Url, notify, location, distance, 100);
	}

	@Override
	public void playGlobalCustomSoundEffect(Plugin plugin, String Url, boolean notify, Location location, int distance, int volumePercent) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			playCustomSoundEffect(plugin, (SpoutCraftPlayer) SpoutCraftPlayer.getPlayer(player), Url, notify, location, distance, volumePercent);
		}
	}

	@Override
	public void playCustomSoundEffect(Plugin plugin, SpoutPlayer target, String Url, boolean notify) {
		playCustomSoundEffect(plugin, target, Url, notify, null);
	}
	
	@Override
	public void playCustomSoundEffect(Plugin plugin, SpoutPlayer target, String Url, boolean notify, Location location) {
		playCustomSoundEffect(plugin, target, Url, notify, location, 16);
	}
	
	@Override
	public void playCustomSoundEffect(Plugin plugin, SpoutPlayer target, String Url, boolean notify, Location location, int distance) {
		playCustomSoundEffect(plugin, target, Url, notify, location, distance, 100);
	}
	
	@Override
	public void playCustomSoundEffect(Plugin plugin, SpoutPlayer target, String Url, boolean notify, Location location, int distance, int volumePercent) {
		playCustomFile(plugin, target, Url, notify, location, distance, volumePercent, true);
	}
	
	private void playCustomFile(Plugin plugin, SpoutPlayer target, String Url, boolean notify, Location location, int distance, int volumePercent, boolean soundEffect) {
		if (target.isSpoutCraftEnabled()) {
			if (Url.length() > 255 || Url.length() < 5) {
				throw new UnsupportedOperationException("All Url's must be between 5 and 256 characters");
			}
			String extension = Url.substring(Url.length() - 4, Url.length());
			if (extension.equalsIgnoreCase(".ogg") || extension.equalsIgnoreCase(".wav") || extension.matches(".*[mM][iI][dD][iI]?$")) {
				if (location == null || location.getWorld().equals(target.getWorld())) {
					if (!soundEffect) {
						BackgroundMusicEvent event = new BackgroundMusicEvent(Url, volumePercent, target);
						Bukkit.getServer().getPluginManager().callEvent(event);
						if (event.isCancelled()) {
							return;
						}
						volumePercent = event.getVolumePercent();
					}
					SpoutCraftPlayer ccp = (SpoutCraftPlayer) target;
					ccp.sendPacket(new PacketDownloadMusic(plugin != null ? plugin.getDescription().getName() : "temp", Url, location, distance, volumePercent, soundEffect, notify));
				}
			}
			else {
				throw new UnsupportedOperationException("All audio files must be ogg vorbis, wav, or midi type");
			}
		}
	}
}
