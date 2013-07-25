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
package org.spout.api.render.shader;

import java.awt.Color;

import org.spout.api.math.Matrix;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.math.Vector4;
import org.spout.api.render.Texture;

public interface Shader {
	public abstract void setUniform(String name, int value);

	public abstract void setUniform(String name, float value);

	public abstract void setUniform(String name, Vector2 value);

	public abstract void setUniform(String name, Vector3 value);

	public abstract void setUniform(String name, Vector4 value);

	public abstract void setUniform(String name, Matrix value);

	public abstract void setUniform(String name, Matrix[] value);

	public abstract void setUniform(String name, Color value);

	public abstract void setUniform(String name, Texture value);

	public abstract void setUniform(String name, Vector3[] values);
}