package org.bukkitcontrib.sound;

import java.io.File;
import net.minecraft.src.SoundManager;
import net.minecraft.src.BukkitContrib;

public class QueuedSound implements Runnable{
	File song;
	int x,y, z, volume, distance;
	boolean soundEffect, notify;
	public QueuedSound(File song, int x, int y, int z, int volume, int distance, boolean soundEffect) {
		this.song = song;
		this.x = x;
		this.y = y;
		this.z = z;
		this.volume = volume;
		this.distance = distance;
		this.soundEffect = soundEffect;
	}
	
	public void setNotify(boolean notify) {
		this.notify = notify;
	}
	
	public void run() {
		play();
	}
	
	private boolean play() {
		if (song.exists()) {
			if (notify) {
				BukkitContrib.createBukkitContribAlert("Download Complete!", song.getName(), 2256 /*Gold Record*/);
			}
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
			return true;
		}
		return false;
	}
}
