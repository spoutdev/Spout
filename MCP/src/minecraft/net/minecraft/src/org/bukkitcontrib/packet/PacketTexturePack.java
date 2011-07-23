package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.File;
import org.bukkitcontrib.io.FileUtil;
import org.bukkitcontrib.texture.TexturePackAction;
import org.bukkitcontrib.io.Download;
import org.bukkitcontrib.io.FileDownloadThread;

public class PacketTexturePack implements BukkitContribPacket{
	private String url;
	public PacketTexturePack(){
		
	}
	
	public PacketTexturePack(String url) {
		this.url = url;
	}

	@Override
	public int getNumBytes() {
		return PacketUtil.getNumBytes(url);
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		url = PacketUtil.readString(input, 256);
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		PacketUtil.writeString(output, url);
	}

	@Override
	public void run(int PlayerId) {
		File texturePack = new File(FileUtil.getTexturePackDirectory(), FileUtil.getFileName(url));
		if (texturePack.exists()) {
			texturePack.delete();
		}
		Download download = new Download(FileUtil.getFileName(url), FileUtil.getTexturePackDirectory(), url, new TexturePackAction(FileUtil.getFileName(url), FileUtil.getTexturePackDirectory()));
		FileDownloadThread.getInstance().addToDownloadQueue(download);
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.PacketTexturePack;
	}

}
