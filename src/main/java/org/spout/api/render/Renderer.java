package org.spout.api.render;

import java.awt.Color;

import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.math.Vector4;

public interface Renderer {

	/**
	 * Begin batching render calls
	 */
	public abstract void begin();

	/**
	 * Ends batching and flushes cache to the GPU
	 */
	public abstract void end();

	/**
	 * Renders the batch.
	 */
	public abstract void render();

	public abstract void addVertex(float x, float y, float z, float w);

	public abstract void addVertex(float x, float y, float z);

	public abstract void addVertex(float x, float y);

	public abstract void addVertex(Vector3 vertex);

	public abstract void addVertex(Vector2 vertex);

	public abstract void addVertex(Vector4 vertex);

	public abstract void addColor(float r, float g, float b);

	public abstract void addColor(float r, float g, float b, float a);

	public abstract void addColor(Color color);

	public abstract void addNormal(float x, float y, float z, float w);

	public abstract void addNormal(float x, float y, float z);

	public abstract void addNormal(Vector3 vertex);

	public abstract void addNormal(Vector4 vertex);

	public abstract void addTexCoord(float u, float v);

	public abstract void addTexCoord(Vector2 uv);

	public abstract void setShader(Shader shader);
	
	public abstract Shader getShader();

	public abstract void enableColors();

	public abstract void enableNormals();

	public abstract void enableTextures();

}