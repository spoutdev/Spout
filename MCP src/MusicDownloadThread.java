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
    int volume;
    boolean loc;
    public MusicDownloadThread(String URL, String file, boolean location, int x, int y, int z, int volume) {
        this.URL = URL;
        this.file = file;
        this.loc = location;
        this.x = x;
        this.y = y;
        this.z = z;
        this.volume = volume;
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
                BukkitContrib.createBukkitContribAlert("Downloading Music...", file, 2256 /*Gold Record*/);
                URL songUrl = new URL(URL);
                ReadableByteChannel rbc = Channels.newChannel(songUrl.openStream());
                FileOutputStream fos = new FileOutputStream(song);
                fos.getChannel().transferFrom(rbc, 0, 1 << 24);
                BukkitContrib.createBukkitContribAlert("Download Complete!", file, 2256 /*Gold Record*/);
            }
        }
        catch (Exception e) {
            if (song.exists()) {
                song.delete();
            }
            BukkitContrib.createBukkitContribAlert("Download Failed!", file, 2256 /*Gold Record*/);
        }
        if (song.exists() && volume > 0) {
            SoundManager sndManager = BukkitContrib.getGameInstance().sndManager;
            if (!sndManager.hasSound(song.getName().toString(), 0)) {
                sndManager.addCustomSound(song.getName().toString(), song);
            }
            sndManager.playMusic(song.getName().toString(), 0, x, y, z, volume / 100F, loc);
        }
    }
}