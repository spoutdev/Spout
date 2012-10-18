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

import java.awt.Font;
import java.io.InputStream;

import org.spout.api.Spout;
import org.spout.api.resource.BasicResourceLoader;

import org.spout.engine.resources.ClientFont;

public class FontLoader extends BasicResourceLoader<ClientFont> {
	@Override
	public final String getFallbackResourceName() {
		return "font://Spout/resources/resources/fonts/ubuntu/Ubuntu-R.ttf";
	}

	@Override
	public ClientFont getResource(InputStream stream) {
		if (stream == null) {
			throw new IllegalArgumentException("Stream passed into font loader is null!");
		}
		ClientFont fontFromResource = null;
		try {
			final Font raw = Font.createFont(Font.TRUETYPE_FONT, stream);
			fontFromResource = new ClientFont(raw);
		} catch (Exception e) {
			Spout.getLogger().severe("Exception caught when reading in a font.");
			e.printStackTrace();
		}
		return fontFromResource;
	}

	@Override
	public final String getProtocol() {
		return "font";
	}

	@Override
	public final String[] getExtensions() {
		return new String[]{"ttf"};
	}
}
