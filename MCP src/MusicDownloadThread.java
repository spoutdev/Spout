package net.minecraft.src;
//BukkitContrib
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import org.bukkitcontrib.sound.QueuedSound;

public class MusicDownloadThread extends Thread{
	String URL;
	String file;
	String plugin;
	int x, y, z;
	int volume, distance;
	boolean soundEffect, notify;
	public MusicDownloadThread(String plugin, String URL, String file, int distance, int x, int y, int z, int volume, boolean soundEffect, boolean notify) {
		this.URL = URL;
		this.plugin = plugin;
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
		File song = null;
		try {
			File directory = new File(BukkitContribCache.getAudioCacheDirectory(), plugin);
			if (!directory.exists()){	
				directory.mkdir();
			}
			song = new File(directory, file);
				if (!song.exists()) {
					if (notify)
						BukkitContrib.createBukkitContribAlert("Downloading Music...", file, 2256 /*Gold Record*/);
						URL songUrl = new URL(URL);
						ReadableByteChannel rbc = Channels.newChannel(songUrl.openStream());
						FileOutputStream fos = new FileOutputStream(song);
						fos.getChannel().transferFrom(rbc, 0, 1 << 24);
						fos.close();
					if (notify)
						BukkitContrib.createBukkitContribAlert("Download Complete!", file, 2256 /*Gold Record*/);
				}
		}
		catch (Exception e) {
				if (song.exists()) {
					song.delete();
				}
				e.printStackTrace();
			if (notify)
				BukkitContrib.createBukkitContribAlert("Download Failed!", file, 2256 /*Gold Record*/);
		}
		if (song != null && song.exists() && volume > 0) {
			QueuedSound sound = new QueuedSound(song, x, y, z, volume, distance, soundEffect);
			BukkitContrib.queuedAudio.add(sound);
		}
	}
}