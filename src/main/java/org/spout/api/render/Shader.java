package org.spout.api.render;


import org.spout.api.math.Matrix;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.math.Vector4;
import org.spout.api.util.Color;

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