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
package org.spout.engine.filesystem.resource;

import java.util.Collection;
import java.util.Set;

import org.spout.api.Spout;
import org.spout.engine.SpoutClient;
import org.spout.math.matrix.Matrix2;
import org.spout.math.matrix.Matrix3;
import org.spout.math.matrix.Matrix4;
import org.spout.math.vector.Vector2;
import org.spout.math.vector.Vector3;
import org.spout.math.vector.Vector4;
import org.spout.renderer.GLVersioned.GLVersion;
import org.spout.renderer.data.Color;
import org.spout.renderer.data.Uniform;
import org.spout.renderer.data.UniformHolder;
import org.spout.renderer.gl.Program;
import org.spout.renderer.gl.Shader;
import org.spout.renderer.gl.Shader.ShaderType;

public class SpoutProgram implements org.spout.api.render.shader.Program {
	protected Program program;

	public SpoutProgram() {
		program = ((SpoutClient) Spout.getEngine()).getRenderer().getGL().createProgram();
	}

	public void addAttributeLayout(String string, int index) {
		program.addAttributeLayout(string, index);
	}

	public void addShader(SpoutShader shader) {
		program.addShader(shader.shader);
	}

	public void addTextureLayout(String string, int unit) {
		program.addTextureLayout(string, unit);
	}

	public void bind() {
		program.bind();
	}

	public void bindTextureUniform(int unit) {
		program.bindTextureUniform(unit);
	}

	public void checkCreated() {
		program.checkCreated();
	}

	public void create() {
		program.create();
	}

	public void destroy() {
		program.destroy();
	}

	public GLVersion getGLVersion() {
		return program.getGLVersion();
	}

	public int getID() {
		return program.getID();
	}

	public void getShader(ShaderType type) {
		program.getShader(Shader.ShaderType.VERTEX);
	}

	public Collection<Shader> getShaders() {
		return program.getShaders();
	}

	public Set<String> getUniformNames() {
		return program.getUniformNames();
	}

	public boolean hasShader(ShaderType type) {
		return program.hasShader(Shader.ShaderType.VERTEX);
	}

	public void removeAttributeLayout(String string) {
		program.removeAttributeLayout(string);
	}

	public void removeShader(ShaderType type) {
		program.removeShader(type);
	}

	public void removeTextureLayout(int unit) {
		program.removeTextureLayout(unit);
	}

	public void setUniform(String string, Color color) {
		program.setUniform(null, Color.BLUE);
	}

	@Override
	public void setUniform(String string, Matrix2 matrix) {
		program.setUniform(string, matrix);
	}

	@Override
	public void setUniform(String string, Matrix3 matrix) {
		program.setUniform(string, matrix);
	}

	@Override
	public void setUniform(String string, Matrix4 matrix) {
		program.setUniform(string, matrix);
	}

	@Override
	public void setUniform(String string, Vector2 vector) {
		program.setUniform(string, vector);
	}

	@Override
	public void setUniform(String string, Vector2[] vector) {
		program.setUniform(string, vector);
	}

	@Override
	public void setUniform(String string, Vector3 vector) {
		program.setUniform(string, vector);
	}

	@Override
	public void setUniform(String string, Vector3[] vector) {
		program.setUniform(string, vector);
	}

	@Override
	public void setUniform(String string, Vector4 vector) {
		program.setUniform(null, Vector4.ONE);
	}

	@Override
	public void setUniform(String string, Boolean bool) {
		program.setUniform(string, bool);
	}

	@Override
	public void setUniform(String string, float f) {
		program.setUniform(string, f);
	}

	@Override
	public void setUniform(String string, int i) {
		program.setUniform(string, i);
	}

	public void unbind() {
		program.unbind();
	}

	public void upload(Uniform uniform) {
		program.upload(uniform);
	}

	public void upload(UniformHolder uniform) {
		program.upload(uniform);
	}
}
