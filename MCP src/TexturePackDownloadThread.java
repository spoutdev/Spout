package net.minecraft.src;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class TexturePackDownloadThread extends Thread{
	private final String url;
	public TexturePackDownloadThread(String url) {
		this.url = url;
	}
	
	public void run() {
		System.out.println("Starting Texture Pack Download");
		String file = BukkitContrib.getFileName(url);
		File texturePack = new File(BukkitContribCache.getTexturePackDirectory(), file);
		if (texturePack.exists()) {
			texturePack.delete();
		}
		try {
			URL packUrl = new URL(url);
			ReadableByteChannel rbc = Channels.newChannel(packUrl.openStream());
			FileOutputStream fos = new FileOutputStream(texturePack);
			fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		}
		catch (Exception e) {
			if (texturePack.exists()) {
				texturePack.delete();
			}
			return;
		}
		BukkitContrib.updateTextureFile = texturePack;
		System.out.println("Finished Texture Pack Download");
	}

}
