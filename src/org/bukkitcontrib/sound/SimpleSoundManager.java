package org.bukkitcontrib.sound;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkitcontrib.packet.PacketDownloadMusic;
import org.bukkitcontrib.packet.PacketPlaySound;
import org.bukkitcontrib.player.ContribCraftPlayer;
import org.bukkitcontrib.player.ContribPlayer;

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
    public void playGlobalSoundEffect(SoundEffect effect, Location location, int volumePercent) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            playSoundEffect((ContribCraftPlayer) ContribCraftPlayer.getContribPlayer(player), effect, location, volumePercent);
        }
    }

    @Override
    public void playSoundEffect(ContribPlayer target, SoundEffect effect) {
        playSoundEffect(target, effect, null);
    }

    @Override
    public void playSoundEffect(ContribPlayer target, SoundEffect effect, Location location) {
        playSoundEffect(target, effect, location, 16);
    }

    @Override
    public void playSoundEffect(ContribPlayer target, SoundEffect effect, Location location, int volumePercent) {
        ContribCraftPlayer ccp = (ContribCraftPlayer) target;
        if (location == null || ccp.getWorld().equals(location.getWorld())) {
            if (location == null) {
                ccp.sendPacket(new PacketPlaySound(effect, volumePercent));
            }
            else {
                ccp.sendPacket(new PacketPlaySound(effect, location, volumePercent));
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
            playMusic((ContribCraftPlayer) ContribCraftPlayer.getContribPlayer(player), music, volumePercent);
        }
    }

    @Override
    public void playMusic(ContribPlayer target, Music music) {
        playMusic(target, music, 100);
    }

    @Override
    public void playMusic(ContribPlayer target, Music music, int volumePercent) {
        ((ContribCraftPlayer) target).sendPacket(new PacketPlaySound(music, volumePercent));
    }

    @Override
    public void playGlobalCustomMusic(String Url) {
        playGlobalCustomMusic(Url, 100);
    }

    @Override
    public void playGlobalCustomMusic(String Url, int volumePercent) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            playCustomMusic((ContribCraftPlayer) ContribCraftPlayer.getContribPlayer(player), Url, volumePercent);
        }
    }

    @Override
    public void playCustomMusic(ContribPlayer target, String Url) {
        playCustomMusic(target, Url, 100);
    }

    @Override
    public void playCustomMusic(ContribPlayer target, String Url, int volumePercent) {
    	if (!Url.substring(Url.length() - 4, Url.length()).equalsIgnoreCase(".ogg")) {
            throw new UnsupportedOperationException("All audio files must be ogg vorbis type");
        }
        if (Url.length() > 255) {
            throw new UnsupportedOperationException("All Url's must be shorter than 256 characters");
        }
        ContribCraftPlayer ccp = (ContribCraftPlayer) target;
        ccp.sendPacket(new PacketDownloadMusic(Url, null, volumePercent));
    }

}
