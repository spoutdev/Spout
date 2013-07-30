/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
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

import org.spout.math.vector.Vector3;
import org.spout.api.model.mesh.MeshFace;
import org.spout.api.model.mesh.Vertex;
import org.spout.api.resource.ResourceLoader;
import org.spout.engine.mesh.BaseMesh;
import org.spout.math.vector.Vector2;

public class MeshLoader extends ResourceLoader {
	public MeshLoader() {
		super("mesh", "mesh://Spout/fallbacks/fallback.obj");
	}

	private static BaseMesh loadObj(InputStream stream) {

		boolean normal, color, texture0;
		ArrayList<MeshFace> faces;
		try (Scanner scan = new Scanner(stream)) {
			normal = false;
			color = false;
			texture0 = false;
			ArrayList<Vector3> verticies = new ArrayList<>();
			ArrayList<Vector3> normals = new ArrayList<>();
			ArrayList<Vector2> uvs = new ArrayList<>();
			faces = new ArrayList<>();
			while (scan.hasNext()) {
				String s = scan.nextLine();
				if (s.startsWith("#")) {
					continue; // it's a comment, skip it
				}
				if (s.startsWith("v ")) { // Space is important !!
					String[] sp = s.split(" ");
					verticies.add(new Vector3(Float.parseFloat(sp[1]), Float.parseFloat(sp[2]), Float.parseFloat(sp[3])));
				}
				if (s.startsWith("vn ")) {
					String[] sp = s.split(" ");
					normals.add(new Vector3(Float.parseFloat(sp[1]), Float.parseFloat(sp[2]), Float.parseFloat(sp[3])));
				}
				if (s.startsWith("vt ")) {
					String[] sp = s.split(" ");
					uvs.add(new Vector2(Float.parseFloat(sp[1]), 1 - Float.parseFloat(sp[2])));
				}
				if (s.startsWith("f ")) {
					String[] sp = s.split(" ");
					if (sp.length > 4) {
						throw new RuntimeException(".obj files must be triangulated.  Re-export the mesh with triangles, not quads");
					}
					if (sp[1].contains("//")) {
						normal = true;
						ArrayList<Vertex> ar = new ArrayList<>();
						for (int i = 1; i <= 3; i++) {
							String[] sn = sp[i].split("//");
							int pos = Integer.parseInt(sn[0]);
							int norm = Integer.parseInt(sn[1]);
							ar.add(Vertex.createVertexPositionNormalIndex(verticies.get(pos - 1), normals.get(norm - 1), pos));
						}
						faces.add(new MeshFace(ar.get(2), ar.get(1), ar.get(0)));
						ar.clear();
					} else if (sp[1].contains("/")) {
						texture0 = true;
						ArrayList<Vertex> ar = new ArrayList<>();
						for (int i = 1; i <= 3; i++) {
							String[] sn = sp[i].split("/");
							int pos = Integer.parseInt(sn[0]);
							int uv = Integer.parseInt(sn[1]);
							if (sn.length > 2) {
								normal = true;
								int norm = Integer.parseInt(sn[2]);
								ar.add(Vertex.createVertexPositionNormalTexture0Index(verticies.get(pos - 1), normals.get(norm - 1), uvs.get(uv - 1), pos));
							} else {
								ar.add(Vertex.createVertexPositionTexture0Index(verticies.get(pos - 1), uvs.get(uv - 1), pos));
							}
						}
						faces.add(new MeshFace(ar.get(2), ar.get(1), ar.get(0)));
						ar.clear();
					} else {
						int face1 = Integer.parseInt(sp[1]);
						int face2 = Integer.parseInt(sp[2]);
						int face3 = Integer.parseInt(sp[3]);

						Vertex p1 = Vertex.createVertexPositionIndex(verticies.get(face1 - 1), face1);
						Vertex p2 = Vertex.createVertexPositionIndex(verticies.get(face2 - 1), face2);
						Vertex p3 = Vertex.createVertexPositionIndex(verticies.get(face3 - 1), face3);

						faces.add(new MeshFace(p3, p2, p1));
					}
				}
			}
		}

		return new BaseMesh(faces, normal, color, texture0, true);
	}

	@Override
	public BaseMesh load(InputStream in) {
		return MeshLoader.loadObj(in);
	}
}
