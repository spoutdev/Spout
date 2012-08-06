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
import org.spout.api.math.Vector3;
import org.spout.api.model.ModelFace;
import org.spout.api.model.Vertex;
import org.spout.api.resource.BasicResourceLoader;
import org.spout.engine.mesh.BaseMesh;

public class MeshLoader extends BasicResourceLoader<BaseMesh> {

	private static BaseMesh loadObj(InputStream stream) {
		Scanner scan = new Scanner(stream);

		ArrayList<Vector3> verticies = new ArrayList<Vector3>();
		ArrayList<Vector3> normals = new ArrayList<Vector3>();
		ArrayList<Vector2> uvs = new ArrayList<Vector2>();
		ArrayList<ModelFace> faces = new ArrayList<ModelFace>();

		while (scan.hasNext()) {
			String s = scan.nextLine();
			if (s.startsWith("#"))
				continue; // it's a comment, skip it
			if (s.startsWith("v")) {
				String[] sp = s.split(" ");
				verticies.add(new Vector3(Float.parseFloat(sp[1]), Float.parseFloat(sp[2]), Float.parseFloat(sp[3])));
			}
			if (s.startsWith("vn")) {
				String[] sp = s.split(" ");
				normals.add(new Vector3(Float.parseFloat(sp[1]), Float.parseFloat(sp[2]), Float.parseFloat(sp[3])));
			}
			if (s.startsWith("vt")) {
				String[] sp = s.split(" ");
				uvs.add(new Vector2(Float.parseFloat(sp[1]), Float.parseFloat(sp[2])));
			}
			if (s.startsWith("f")) {
				String[] sp = s.split(" ");

				if (sp[1].contains("//")) {
					ArrayList<Vertex> ar = new ArrayList<Vertex>();
					for (int i = 1; i <= 3; i++) {
						String[] sn = sp[i].split("//");
						int pos = Integer.parseInt(sn[0]);
						int norm = Integer.parseInt(sn[1]);
						ar.add(new Vertex(verticies.get(pos - 1), normals.get(norm - 1)));

					}
					faces.add(new ModelFace(ar.get(0), ar.get(1), ar.get(2)));
					ar.clear();

				} else if (sp[1].contains("/")) {
					ArrayList<Vertex> ar = new ArrayList<Vertex>();
					for (int i = 1; i <= 2; i++) {
						String[] sn = sp[i].split("/");
						int pos = Integer.parseInt(sn[0]);
						int uv = Integer.parseInt(sn[1]);
						ar.add(new Vertex(verticies.get(pos - 1), uvs.get(uv - 1)));

					}
					faces.add(new ModelFace(ar.get(0), ar.get(1), ar.get(2)));
					ar.clear();

				} else {
					int face1 = Integer.parseInt(sp[1]) - 1;
					int face2 = Integer.parseInt(sp[2]) - 1;
					int face3 = Integer.parseInt(sp[3]) - 1;

					Vertex p = new Vertex(verticies.get(face1));
					Vertex p2 = new Vertex(verticies.get(face2));
					Vertex p3 = new Vertex(verticies.get(face3));

					faces.add(new ModelFace(p, p2, p3));

				}

			}

		}

		return new BaseMesh(faces);

	}

	@Override
	public String getFallbackResourceName() {
		return "mesh://Spout/resources/fallbacks/fallback.obj";
	}

	@Override
	public BaseMesh getResource(InputStream stream) {
		return MeshLoader.loadObj(stream);
	}

	@Override
	public String getProtocol() {
		return "mesh";
	}

	@Override
	public String[] getExtensions() {
		return new String[] { "obj" };
	}

}
