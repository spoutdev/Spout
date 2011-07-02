package org.bukkitcontrib.sound;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkitcontrib.event.sound.BackgroundMusicEvent;
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
    public void playGlobalSoundEffect(SoundEffect effect, Location location, int distance) {
        playGlobalSoundEffect(effect, location, distance, 16);
    }

    @Override
    public void playGlobalSoundEffect(SoundEffect effect, Location location, int distance, int volumePercent) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            playSoundEffect((ContribCraftPlayer) ContribCraftPlayer.getContribPlayer(player), effect, location, distance, volumePercent);
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
    public void playSoundEffect(ContribPlayer target, SoundEffect effect, Location location, int distance) {
        playSoundEffect(target, effect, location, distance, 16);
    }

    @Override
    public void playSoundEffect(ContribPlayer target, SoundEffect effect, Location location, int distance, int volumePercent) {
        ContribCraftPlayer ccp = (ContribCraftPlayer) target;
        if (location == null || ccp.getWorld().equals(location.getWorld())) {
            if (location == null) {
                ccp.sendPacket(new PacketPlaySound(effect, distance, volumePercent));
            }
            else {
                ccp.sendPacket(new PacketPlaySound(effect, location, distance, volumePercent));
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
        BackgroundMusicEvent event = new BackgroundMusicEvent(music, volumePercent, target);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        ((ContribCraftPlayer) target).sendPacket(new PacketPlaySound(music, event.getVolumePercent()));
    }

    @Override
    public void playGlobalCustomMusic(String Url, boolean notify) {
        playGlobalCustomMusic(Url, notify, null);
    }
    
    @Override
    public void playGlobalCustomMusic(String Url, boolean notify, Location location) {
        playGlobalCustomMusic(Url, notify, location, 16);
    }
    
    @Override
    public void playGlobalCustomMusic(String Url, boolean notify, Location location, int distance) {
        playGlobalCustomMusic(Url, notify, location, distance, 100);
    }

    @Override
    public void playGlobalCustomMusic(String Url, boolean notify, Location location, int distance, int volumePercent) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            playCustomMusic((ContribCraftPlayer) ContribCraftPlayer.getContribPlayer(player), Url, notify, location, distance, volumePercent);
        }
    }

    @Override
    public void playCustomMusic(ContribPlayer target, String Url, boolean notify) {
        playCustomMusic(target, Url, notify, null);
    }
    
    @Override
    public void playCustomMusic(ContribPlayer target, String Url, boolean notify, Location location) {
        playCustomMusic(target, Url, notify, location, 16);
    }
    
    @Override
    public void playCustomMusic(ContribPlayer target, String Url, boolean notify, Location location, int distance) {
        playCustomMusic(target, Url, notify, location, distance, 100);
    }

    @Override
    public void playCustomMusic(ContribPlayer target, String Url, boolean notify, Location location, int distance, int volumePercent) {
        playCustomFile(target, Url, notify, location, distance, volumePercent, false);
    }
    
    @Override
    public void playGlobalCustomSoundEffect(String Url, boolean notify) {
        playGlobalCustomSoundEffect(Url, notify, null);
    }
    
    @Override
    public void playGlobalCustomSoundEffect(String Url, boolean notify, Location location) {
        playGlobalCustomSoundEffect(Url, notify, location, 16);
    }
    
    @Override
    public void playGlobalCustomSoundEffect(String Url, boolean notify, Location location, int distance) {
        playGlobalCustomSoundEffect(Url, notify, location, distance, 100);
    }

    @Override
    public void playGlobalCustomSoundEffect(String Url, boolean notify, Location location, int distance, int volumePercent) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            playCustomSoundEffect((ContribCraftPlayer) ContribCraftPlayer.getContribPlayer(player), Url, notify, location, distance, volumePercent);
        }
    }

    @Override
    public void playCustomSoundEffect(ContribPlayer target, String Url, boolean notify) {
        playCustomSoundEffect(target, Url, notify, null);
    }
    
    @Override
    public void playCustomSoundEffect(ContribPlayer target, String Url, boolean notify, Location location) {
        playCustomSoundEffect(target, Url, notify, location, 16);
    }
    
    @Override
    public void playCustomSoundEffect(ContribPlayer target, String Url, boolean notify, Location location, int distance) {
        playCustomSoundEffect(target, Url, notify, location, distance, 100);
    }
    
    @Override
    public void playCustomSoundEffect(ContribPlayer target, String Url, boolean notify, Location location, int distance, int volumePercent) {
        playCustomFile(target, Url, notify, location, distance, volumePercent, true);
    }
    
    private void playCustomFile(ContribPlayer target, String Url, boolean notify, Location location, int distance, int volumePercent, boolean soundEffect) {
        if (target.getVersion() > 7) {
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
                    ContribCraftPlayer ccp = (ContribCraftPlayer) target;
                    ccp.sendPacket(new PacketDownloadMusic(Url, location, distance, volumePercent, soundEffect, notify));
                }
            }
            else {
                throw new UnsupportedOperationException("All audio files must be ogg vorbis, wav, or midi type");
            }
        }
    }

}
