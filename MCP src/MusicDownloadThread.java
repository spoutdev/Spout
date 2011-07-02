package net.minecraft.src;
//BukkitContrib
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class MusicDownloadThread extends Thread{
    String URL;
    String file;
    int x, y, z;
    int volume, distance;
    boolean soundEffect, notify;
    public MusicDownloadThread(String URL, String file, int distance, int x, int y, int z, int volume, boolean soundEffect, boolean notify) {
        this.URL = URL;
        this.file = file;
        this.distance = distance;
        this.x = x;
        this.y = y;
        this.z = z;
        this.volume = volume;
        this.soundEffect = soundEffect;
        this.notify = notify;
    }
    
    @Override
    public void run() {
        File directory, song;
        try {
            directory = new File(BukkitContrib.getGameInstance().getMinecraftDir(), "BukkitContribMusicCache");
            if (!directory.exists()){
                directory.mkdir();
            }
            song = new File(directory.getPath(), file);
        }
        catch (Exception e) {return;}
        try {
            if (!song.exists()) {
                if (notify)
                    BukkitContrib.createBukkitContribAlert("Downloading Music...", file, 2256 /*Gold Record*/);
                URL songUrl = new URL(URL);
                ReadableByteChannel rbc = Channels.newChannel(songUrl.openStream());
                FileOutputStream fos = new FileOutputStream(song);
                fos.getChannel().transferFrom(rbc, 0, 1 << 24);
                
                if (notify)
                    BukkitContrib.createBukkitContribAlert("Download Complete!", file, 2256 /*Gold Record*/);
            }
        }
        catch (Exception e) {
            if (song.exists()) {
                song.delete();
            }
            if (notify)
                BukkitContrib.createBukkitContribAlert("Download Failed!", file, 2256 /*Gold Record*/);
        }
        if (song.exists() && volume > 0) {
            SoundManager sndManager = BukkitContrib.getGameInstance().sndManager;
            if (!sndManager.hasSoundEffect(song.getName().toString(), 0) && soundEffect) {
                sndManager.addCustomSoundEffect(song.getName().toString(), song);
            }
            if (!sndManager.hasMusic(song.getName().toString(), 0) && !soundEffect) {
                sndManager.addCustomMusic(song.getName().toString(), song);
            }
            if (!soundEffect) {
                sndManager.playMusic(song.getName().toString(), 0, x, y, z, volume / 100F, distance);
            }
            else {
                sndManager.playCustomSoundEffect(song.getName().toString(), x, y, z, volume / 100F, distance);
            }
        }
    }
}