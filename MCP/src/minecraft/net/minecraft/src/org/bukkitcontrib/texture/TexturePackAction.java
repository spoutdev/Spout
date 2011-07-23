package org.bukkitcontrib.texture;

import java.io.File;
import net.minecraft.src.TexturePackCustom;
import net.minecraft.src.BukkitContrib;

public class TexturePackAction implements Runnable {
	private final File texturePackFile;
	
	public TexturePackAction(String filename, File directory) {
		this.texturePackFile = new File(directory, filename);
	}
	
	public void run() {
		TexturePackCustom pack = new TexturePackCustom(texturePackFile);
		BukkitContrib.getGameInstance().renderEngine.texturePack.setTexturePack(pack);
		//BukkitContrib.getGameInstance().renderEngine.refreshTextures();
	}
}