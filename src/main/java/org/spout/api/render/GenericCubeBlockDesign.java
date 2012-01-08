/*
 * This file is part of SpoutAPI (http://www.spout.org/).
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
package org.spout.api.render;

import org.spout.api.plugin.Plugin;

public class GenericCubeBlockDesign extends GenericBlockDesign {

	/**
	 * Creates a basic cube custom block model
	 *
	 * @param addon making this block
	 * @param texture to use
	 * @param textureId[6] Array of faces, give Id's for SubTexture locations
	 */
	public GenericCubeBlockDesign(Plugin addon, Texture texture, int[] textureId) {

		if (textureId.length != 6) {
			throw new IllegalArgumentException("Invalid textureId Array length: " + textureId.length + ". Should be 6");
		}

		setBoundingBox(0, 0, 0, 1, 1, 1);

		setQuadNumber(6);

		setMinBrightness(0.0F).setMaxBrightness(1.0F).setTexture(addon, texture);

		Quad bottom = new Quad(0, texture.getSubTexture(textureId[0]));
		bottom.addVertex(0, 0.0F, 0.0F, 0.0F);
		bottom.addVertex(1, 1.0F, 0.0F, 0.0F);
		bottom.addVertex(2, 1.0F, 0.0F, 1.0F);
		bottom.addVertex(3, 0.0F, 0.0F, 1.0F);
		setLightSource(0, 0, -1, 0);

		Quad face1 = new Quad(1, texture.getSubTexture(textureId[1]));
		face1.addVertex(0, 0.0F, 0.0F, 0.0F);
		face1.addVertex(1, 0.0F, 1.0F, 0.0F);
		face1.addVertex(2, 1.0F, 1.0F, 0.0F);
		face1.addVertex(3, 1.0F, 0.0F, 0.0F);
		setLightSource(1, 0, 0, -1);

		Quad face2 = new Quad(2, texture.getSubTexture(textureId[2]));
		face2.addVertex(0, 1.0F, 0.0F, 0.0F);
		face2.addVertex(1, 1.0F, 1.0F, 0.0F);
		face2.addVertex(2, 1.0F, 1.0F, 1.0F);
		face2.addVertex(3, 1.0F, 0.0F, 1.0F);
		setLightSource(2, 1, 0, 0);

		Quad face3 = new Quad(3, texture.getSubTexture(textureId[3]));
		face3.addVertex(0, 1.0F, 0.0F, 1.0F);
		face3.addVertex(1, 1.0F, 1.0F, 1.0F);
		face3.addVertex(2, 0.0F, 1.0F, 1.0F);
		face3.addVertex(3, 0.0F, 0.0F, 1.0F);
		setLightSource(3, 0, 0, 1);

		Quad face4 = new Quad(4, texture.getSubTexture(textureId[4]));
		face4.addVertex(0, 0.0F, 0.0F, 1.0F);
		face4.addVertex(1, 0.0F, 1.0F, 1.0F);
		face4.addVertex(2, 0.0F, 1.0F, 0.0F);
		face4.addVertex(3, 0.0F, 0.0F, 0.0F);
		setLightSource(4, -1, 0, 0);

		Quad top = new Quad(5, texture.getSubTexture(textureId[5]));
		top.addVertex(0, 0.0F, 1.0F, 0.0F);
		top.addVertex(1, 0.0F, 1.0F, 1.0F);
		top.addVertex(2, 1.0F, 1.0F, 1.0F);
		top.addVertex(3, 1.0F, 1.0F, 0.0F);
		setLightSource(5, 0, 1, 0);

		setQuad(bottom).setQuad(face1).setQuad(face2).setQuad(face3).setQuad(face4).setQuad(top);
	}

	/**
	 * Creates a basic cube custom block model with only one texture
	 *
	 * @param addon making this block
	 * @param texture to use
	 * @param textureId to get the SubTexture to use
	 */
	public GenericCubeBlockDesign(Plugin addon, Texture texture, int textureId) {
		this(addon, texture, getIdMap(textureId));
	}

	/**
	 * Creates a basic cube custom block model with only one texture
	 *
	 * @param addon making this block
	 * @param texture url to use - must be square
	 * @param textureSize size of the width/height of the texture in pixels
	 */
	public GenericCubeBlockDesign(Plugin addon, String texture, int textureSize) {
		this(addon, new Texture(addon, texture, textureSize, textureSize, textureSize), 0);
	}

	private static int[] getIdMap(int textureId) {
		int[] idMap = new int[6];
		for (int i = 0; i < 6; i++) {
			idMap[i] = textureId;
		}
		return idMap;
	}
}
