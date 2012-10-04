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
package org.spout.engine.resources;

import java.awt.Color;
import java.util.Map;
import java.util.Set;

import org.lwjgl.opengl.GL11;
import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.math.Matrix;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.math.Vector4;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.Shader;
import org.spout.api.resource.Resource;

public class ClientRenderMaterial extends Resource implements RenderMaterial {
	
	Shader shader;
	Map<String, Object> materialParameters;

	boolean depthTesting;
	Matrix view;
	Matrix projection;
	
	
	
	public ClientRenderMaterial(Shader s, Map<String, Object> params){
		this(s, params, null, null, true);
	}
	
	public ClientRenderMaterial(Shader s, Map<String, Object> params, Matrix projection, Matrix view, boolean depth){
		this.shader = s;
		this.materialParameters = params;
		this.projection = projection;
		this.view = view;
		this.depthTesting = depth;
	}
	
	@Override
	public void assign(){
		Set<Map.Entry<String, Object>> s = materialParameters.entrySet();

		for(Map.Entry<String, Object> entry : s){
			if(entry.getValue() instanceof Integer){
				shader.setUniform(entry.getKey(), ((Integer)entry.getValue()).intValue());
			} else if( entry.getValue() instanceof Float){
				shader.setUniform(entry.getKey(), ((Float)entry.getValue()).floatValue());
			} else if( entry.getValue() instanceof Double){
				shader.setUniform(entry.getKey(), ((Double)entry.getValue()).floatValue());
			} else if( entry.getValue() instanceof ClientTexture){
				shader.setUniform(entry.getKey(), (ClientTexture)entry.getValue());
			} else if( entry.getValue() instanceof Vector2){
				shader.setUniform(entry.getKey(), (Vector2)entry.getValue());
			} else if( entry.getValue() instanceof Vector3){
				shader.setUniform(entry.getKey(), (Vector3)entry.getValue());
			} else if( entry.getValue() instanceof Vector4){
				shader.setUniform(entry.getKey(), (Vector4)entry.getValue());
			} else if( entry.getValue() instanceof Color){
				shader.setUniform(entry.getKey(), (Color)entry.getValue());
			} else if( entry.getValue() instanceof Matrix) {
				shader.setUniform(entry.getKey(), (Matrix)entry.getValue());
			}
		}
		
		shader.assign();

	}

	@Override
	public Object getValue(String name) {
		return materialParameters.get(name);
	}
	
	@Override
	public Shader getShader(){
		return shader;
	}
	
	@Override
	public void preRender() {
		if(!depthTesting){
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		}
		
	}
	
	@Override
	public void postRender() {
		if(!depthTesting){
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
	}
}
