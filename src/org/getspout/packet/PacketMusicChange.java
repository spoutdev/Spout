package org.getspout.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.getspout.BukkitContrib;
import org.getspout.event.sound.BackgroundMusicEvent;
import org.getspout.player.ContribCraftPlayer;
import org.getspout.player.ContribPlayer;
import org.getspout.sound.Music;

public class PacketMusicChange implements BukkitContribPacket{
	protected int id;
	protected int volumePercent;
	boolean cancel = false;
	
	public PacketMusicChange() {
		
	}
	
	public PacketMusicChange(int music, int volumePercent) {
		this.id = music;
		this.volumePercent = volumePercent;
	}
	
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public int getNumBytes() {
		return 9;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		id = input.readInt();
		volumePercent = input.readInt();
		cancel =  input.readBoolean();
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		output.writeInt(id);
		output.writeInt(volumePercent);
		output.writeBoolean(cancel);
	}

	@Override
	public void run(int playerId) {
		ContribPlayer player = BukkitContrib.getPlayerFromId(playerId);
		Music music = Music.getMusicFromId(id);
		if (player != null && music != null) {
			BackgroundMusicEvent event = new BackgroundMusicEvent(music, volumePercent, player);
			Bukkit.getServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				cancel = true;
			}
			((ContribCraftPlayer)player).sendPacket(this);
		}
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.PacketMusicChange;
	}

}
