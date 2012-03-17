package org.spout.api.render;

import java.awt.Color;

import org.spout.api.math.Matrix;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.math.Vector4;

public interface Shader {

	public abstract void setUniform(String name, int value);

	public abstract void setUniform(String name, float value);

	public abstract void setUniform(String name, Vector2 value);

	public abstract void setUniform(String name, Vector3 value);

	public abstract void setUniform(String name, Vector4 value);

	public abstract void setUniform(String name, Matrix value);

	public abstract void setUniform(String name, Color value);

	//public abstract void setUniform(String name, Texture value);

	public abstract void enableAttribute(String name, int size, int type, int stride, long offset);

	public abstract void assign();

}