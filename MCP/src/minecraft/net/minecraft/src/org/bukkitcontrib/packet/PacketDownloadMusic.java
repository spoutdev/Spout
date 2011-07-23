package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.File;
import net.minecraft.src.*;
import org.bukkitcontrib.io.Download;
import org.bukkitcontrib.io.FileUtil;
import org.bukkitcontrib.io.FileDownloadThread;
import org.bukkitcontrib.sound.QueuedSound;

public class PacketDownloadMusic implements BukkitContribPacket{
	int x, y, z;
	int volume, distance;
	boolean soundEffect, notify;
	String URL, plugin;
	public PacketDownloadMusic() {
		
	}

	@Override
	public int getNumBytes() {
		return 22 + PacketUtil.getNumBytes(URL) + PacketUtil.getNumBytes(plugin);
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		URL = PacketUtil.readString(input, 255);
		plugin = PacketUtil.readString(input, 255);
		distance = input.readInt();
		x = input.readInt();
		y = input.readInt();
		z = input.readInt();
		volume = input.readInt();
		soundEffect = input.readBoolean();
		notify = input.readBoolean();
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		PacketUtil.writeString(output, URL);
		PacketUtil.writeString(output, plugin);
		output.writeInt(distance);
		output.writeInt(x);
		output.writeInt(y);
		output.writeInt(z);
		output.writeInt(volume);
		output.writeBoolean(soundEffect);
		output.writeBoolean(notify);
	}


	@Override
	public void run(int PlayerId) {
		File directory = new File(FileUtil.getAudioCacheDirectory(), plugin);
		if (!directory.exists()){	
			directory.mkdir();
		}
		File song = new File(directory, getFileName());
		QueuedSound action = new QueuedSound(song, x, y, z, volume, distance, soundEffect);
		Download download = new Download(getFileName(), directory, URL, action);
		action.setNotify(!download.isDownloaded() && notify);
		if (!download.isDownloaded() && notify) {
			BukkitContrib.createBukkitContribAlert("Downloading Music...", getFileName(), 2256 /*Gold Record*/);
		}
		FileDownloadThread.getInstance().addToDownloadQueue(download);
	}
	
	private String getFileName() {
		int slashIndex = URL.lastIndexOf('/');
		int dotIndex = URL.lastIndexOf('.', slashIndex);
		if (dotIndex == -1 || dotIndex < slashIndex) {
				return URL.substring(slashIndex + 1).replaceAll("%20", " ");
		}
		return URL.substring(slashIndex + 1, dotIndex).replaceAll("%20", " ");
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.PacketDownloadMusic;
	}

}
