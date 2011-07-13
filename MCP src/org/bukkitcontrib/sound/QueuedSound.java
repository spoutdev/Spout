package org.bukkitcontrib.sound;

import java.io.File;
import net.minecraft.src.SoundManager;
import net.minecraft.src.BukkitContrib;

public class QueuedSound {
	File song;
	int x,y, z, volume, distance;
	boolean soundEffect;
	public QueuedSound(File song, int x, int y, int z, int volume, int distance, boolean soundEffect) {
		this.song = song;
		this.x = x;
		this.y = y;
		this.z = z;
		this.volume = volume;
		this.distance = distance;
		this.soundEffect = soundEffect;
	}
	
	public boolean play() {
		if (song.exists()) {
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
