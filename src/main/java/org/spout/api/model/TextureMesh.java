package org.spout.api.model;

import org.spout.api.math.Vector2;
import org.spout.api.resource.Resource;

public class TextureMesh extends Resource implements Mesh {

	private Vector2 [][] uvs;
	
	public TextureMesh(Vector2 [][] uvs){
		this.uvs = uvs;
	}

	public Vector2 getUV(int face, int vertex) {
		int i = face % uvs.length;//Allow to render 6 face of a cube with a one face specified
		return uvs[i][vertex % uvs[i].length];
	}
	
}
