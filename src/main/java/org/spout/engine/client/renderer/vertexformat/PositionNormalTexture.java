package org.spout.engine.client.renderer.vertexformat;

import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;

public class PositionNormalTexture extends VertexFormat {
	Vector3 position;
	Vector3 normal;
	Vector2 texture;
	
	public PositionNormalTexture(Vector3 position, Vector3 normal, Vector2 uv){
		this.position = position;
		this.normal = normal;
		this.texture = uv;
	}

	public Vector3 getPosition() {
		return position;
	}

	public Vector3 getNormal() {
		return normal;
	}

	public Vector2 getTexture() {
		return texture;
	}

	public void setPosition(Vector3 position) {
		this.position = position;
	}

	public void setNormal(Vector3 normal) {
		this.normal = normal;
	}

	public void setTexture(Vector2 texture) {
		this.texture = texture;
	}

	

}
