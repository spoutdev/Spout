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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.spout.api.math.Vector2;
import org.spout.api.model.TextureMesh;
import org.spout.api.resource.BasicResourceLoader;

public class TextureMeshLoader extends BasicResourceLoader<TextureMesh> {

	/* Exemple : TextureMesh file
	 * #Comment
	 * #First uv pos
	 * vt 0.000 0.000
	 * vt 1.000 0.000
	 * vt 0.000 1.000
	 * vt 1.000 1.000
	 * #Second face->uv
	 * #Order of face is important
	 * f 0 1 2 3
	 * f 2 1 3 4
	 */

	private static TextureMesh loadObj(InputStream stream) {
		Scanner scan = new Scanner(stream);

		ArrayList<Vector2[]> textures = new ArrayList<Vector2[]>();
		ArrayList<Vector2> uvs = new ArrayList<Vector2>();
		Vector2 scale = Vector2.ONE;
		Vector2 size = Vector2.ONE;
		Vector2 sizeScaled = Vector2.ONE;

		while (scan.hasNext()) {
			String s = scan.nextLine();
			if (s.startsWith("#")) // Comment
				continue;
			else if (s.startsWith("scale ")) { // Scale x y
				String[] sp = s.split(" ");
				scale = new Vector2(1f / Float.parseFloat(sp[1]), 1f / Float.parseFloat(sp[2]));
				sizeScaled = scale.multiply(size);
			}else if (s.startsWith("size ")) { // Size x y
				String[] sp = s.split(" ");
				size = new Vector2(Float.parseFloat(sp[1]), Float.parseFloat(sp[2]));
				sizeScaled = scale.multiply(size);
			}else if (s.startsWith("rect ")) { // Rect x y
				String[] sp = s.split(" ");
				Vector2 base = scale.multiply(new Vector2(Integer.parseInt(sp[1]), Integer.parseInt(sp[2])));
				textures.add(new Vector2[]{
						base,
						base.add(0f, sizeScaled.getY()),
						base.add(sizeScaled.getX(), sizeScaled.getY()),
						base.add(sizeScaled.getX(), 0f)});
			}else if (s.startsWith("vt ")) { // Vertex texture x y
				String[] sp = s.split(" ");
				uvs.add(scale.multiply(new Vector2(Float.parseFloat(sp[1]), 1 - Float.parseFloat(sp[2]))));
			}else if (s.startsWith("f ")) { // Face 1 2 3 ...
				String[] sp = s.split(" ");

				ArrayList<Vector2> ar = new ArrayList<Vector2>();
				for (int i = 1; i < sp.length; i++) {
					int uv = Integer.parseInt(sp[i]) - 1; //Begin at 1 ?
					ar.add(uvs.get(uv));
				}
				textures.add((Vector2[]) ar.toArray(new Vector2[0]));
				ar.clear();
			}

		}

		scan.close();
		return new TextureMesh((Vector2[][]) textures.toArray(new Vector2[0][]));
	}

	@Override
	public String getFallbackResourceName() {
		return "texturemesh://Spout/resources/fallbacks/cube.uvs";
	}

	@Override
	public TextureMesh getResource(InputStream stream) {
		return TextureMeshLoader.loadObj(stream);
	}

	@Override
	public String getProtocol() {
		return "texturemesh";
	}

	@Override
	public String[] getExtensions() {
		return new String[] { "uvs" };
	}

}
