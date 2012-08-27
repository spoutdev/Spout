/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.engine.resources.loader;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import org.spout.api.render.Texture;
import org.spout.api.resource.BasicResourceLoader;
import org.spout.engine.resources.ClientTexture;

public class TextureLoader extends BasicResourceLoader<Texture> {
	/**
	 * An array of strings of the supported extensions in this TextureLoader.
	 */
	public static final String[] EXTENSIONS = unique(ImageIO.getReaderFormatNames());

	@Override
	public Texture getResource(InputStream stream) {
		Texture t = null;
		try {
			BufferedImage image = ImageIO.read(stream);
			t = new ClientTexture(image);
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			try {
				stream.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return t;
	}

	@Override
	public String getFallbackResourceName() {
		return "texture://Spout/resources/fallbacks/fallback.png";
	}

	@Override
	public String getProtocol() {
		return "texture";
	}

	@Override
	public String[] getExtensions() {
		return EXTENSIONS;
	}

	public static String[] unique(String[] strings) {
		Set<String> set = new HashSet<String>();
		for (int i = 0; i < strings.length; i++) {
			String name = strings[i].toLowerCase();
			set.add(name);
		}
		return (String[]) set.toArray(new String[0]);
	}

}
