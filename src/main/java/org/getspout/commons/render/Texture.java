package org.getspout.commons.render;

import java.util.ArrayList;
import java.util.List;

import org.getspout.commons.plugin.Plugin;

public class Texture {

	public String texture;
	public Plugin addon;
	public int width;
	public int height;
	public int spriteSize;

	public List<SubTexture> subTextures;

	public Texture(Plugin addon, String texture, int width, int height, int spriteSize) {
		this.texture = texture;
		this.addon = addon;
		this.width = width;
		this.height = height;
		this.spriteSize = spriteSize;

		int amount = (width / spriteSize) * (height / spriteSize);

		subTextures = new ArrayList<SubTexture>(amount);

		int count = 0;
		for (int y = (height / spriteSize) - 1; y >= 0; y--) {
			for (int x = 0; x < width / spriteSize; x++) {
				subTextures.add(count, new SubTexture(this, x * spriteSize, y * spriteSize, spriteSize));
				count++;
			}
		}
	}

	public SubTexture getSubTexture(int textureId) {

		return subTextures.get(textureId);
	}

	public String getTexture() {
		return texture;
	}
	
	public int getSpriteSize() {
		return spriteSize;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Plugin getAddon() {
		return addon;
	}
}
