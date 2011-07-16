package org.bukkitcontrib.event.sound;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkitcontrib.player.ContribPlayer;
import org.bukkitcontrib.sound.Music;

public class BackgroundMusicEvent extends Event implements Cancellable {
	protected Music music = null;
	protected int volume;
	protected ContribPlayer target;
	protected String Url = null;
	protected boolean cancel = false;
	public BackgroundMusicEvent(Music music, int volume, ContribPlayer target) {
		super("BackgroundMusicEvent");
		this.music = music;
		this.volume = volume;
		this.target = target;
	}
	
	public BackgroundMusicEvent(String Url, int volume, ContribPlayer target) {
		super("BackgroundMusicEvent");
		this.Url = Url;
		this.volume = volume;
		this.target = target;
	}
	
	/**
	 * Get's the music this song is playing, or null if it is custom
	 * @return music
	 */
	public Music getMusic() {
		return music;
	}
	
	/**
	 * Get's the music Url this song is playing, or null if it is official MC music
	 * @return music url
	 */
	public String getMusicUrl() {
		return Url;
	}
	
	/**
	 * Get's the volume percent for this music
	 * @return volume percent
	 */
	public int getVolumePercent() {
		return volume;
	}
	
	/**
	 * Set's the volume percent for this music
	 * @param volume to set
	 */
	public void setVolumePercent(int volume) {
		this.volume = volume;
	}
	
	/**
	 * Get's the player that the music is intended for
	 * @return target
	 */
	public ContribPlayer getTargetPlayer() {
		return target;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

}
