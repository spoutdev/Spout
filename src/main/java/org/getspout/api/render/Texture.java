/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.getspout.api.render;

import java.util.ArrayList;
import java.util.List;

import org.getspout.api.plugin.Plugin;

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

		int amount = width / spriteSize * (height / spriteSize);

		subTextures = new ArrayList<SubTexture>(amount);

		int count = 0;
		for (int y = height / spriteSize - 1; y >= 0; y--) {
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
