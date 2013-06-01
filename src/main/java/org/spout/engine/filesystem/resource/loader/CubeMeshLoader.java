/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.engine.filesystem.resource.loader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.spout.api.math.Vector2;
import org.spout.api.model.mesh.CubeMeshFactory;
import org.spout.api.model.mesh.OrientedMesh;
import org.spout.api.resource.ResourceLoader;

public class CubeMeshLoader extends ResourceLoader {
	public CubeMeshLoader() {
		super("cubemesh", "cubemesh://Spout/fallbacks/cube.uvs");
	}

	private static OrientedMesh loadObj(InputStream stream) {
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
			else if (s.startsWith("scale ") || s.startsWith("subtextures")) { // Scale x y
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
				textures.add(ar.toArray(new Vector2[0]));
				ar.clear();
			}else if (s.startsWith("rotate ")) { // rotate Phi Theta
				String[] sp = s.split(" ");
				int Phi = Integer.parseInt(sp[1]), Theta = Integer.parseInt(sp[2]);
				if(Phi%90 + Theta%90 == 0){
					Phi = (Phi%360)/90;
					Theta = (Theta%360)/90;
					// Rotation around y axe
					for(int i=0;i<Phi;i++){
						// Faces rotation
						Vector2[] temp = textures.get(2);
						textures.set(2, textures.get(4));
						textures.set(4, textures.get(3));
						textures.set(3, textures.get(5));
						textures.set(5, temp);
						// Top and bot textures rotation
						textures.set(1, new Vector2[]{textures.get(1)[1],textures.get(1)[2],textures.get(1)[3],textures.get(1)[0]});
						textures.set(0, new Vector2[]{textures.get(0)[3],textures.get(0)[0],textures.get(0)[1],textures.get(0)[2]});
					}
					// Rotation around z axe
					for(int i=0;i<Theta;i++){
						// Faces rotation
						Vector2[] temp = textures.get(0);
						textures.set(0, textures.get(3));
						textures.set(3, textures.get(1));
						textures.set(1, textures.get(2));
						textures.set(2, temp);
						// East and Weast textures rotation
						textures.set(4, new Vector2[]{textures.get(4)[1],textures.get(4)[2],textures.get(4)[3],textures.get(4)[0]});
						textures.set(5, new Vector2[]{textures.get(5)[3],textures.get(5)[0],textures.get(5)[1],textures.get(5)[2]});
						// Others textures rotation fixes
						textures.set(2, new Vector2[]{textures.get(2)[2],textures.get(2)[3],textures.get(2)[0],textures.get(2)[1]});
						textures.set(3, new Vector2[]{textures.get(3)[2],textures.get(3)[3],textures.get(3)[0],textures.get(3)[1]});
					}
				}
			}

		}

		scan.close();
		return CubeMeshFactory.generateCubeMesh(textures.toArray(new Vector2[0][]));
	}

	@Override
	public OrientedMesh load(InputStream stream) {
		return CubeMeshLoader.loadObj(stream);
	}
}