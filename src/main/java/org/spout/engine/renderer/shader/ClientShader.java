/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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
package org.spout.engine.renderer.shader;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.math.Matrix;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.math.Vector4;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.RenderMode;
import org.spout.api.render.Shader;
import org.spout.api.render.Texture;
import org.spout.api.resource.Resource;

import org.spout.engine.SpoutClient;
import org.spout.engine.SpoutRenderer;
import org.spout.engine.renderer.shader.variables.ColorShaderVariable;
import org.spout.engine.renderer.shader.variables.FloatShaderVariable;
import org.spout.engine.renderer.shader.variables.IntShaderVariable;
import org.spout.engine.renderer.shader.variables.Mat2ShaderVariable;
import org.spout.engine.renderer.shader.variables.Mat3ShaderVariable;
import org.spout.engine.renderer.shader.variables.Mat4ShaderVariable;
import org.spout.engine.renderer.shader.variables.Mat4ArrayShaderVariable;
import org.spout.engine.renderer.shader.variables.ShaderVariable;
import org.spout.engine.renderer.shader.variables.TextureSamplerShaderVariable;
import org.spout.engine.renderer.shader.variables.Vec2ShaderVariable;
import org.spout.engine.renderer.shader.variables.Vec3ShaderVariable;
import org.spout.engine.renderer.shader.variables.Vec4ShaderVariable;
import org.spout.engine.renderer.shader.variables.Vector3ArrayShaderVariable;

/**
 * Represents a Shader Object in OpenGL
 */
public class ClientShader extends Resource implements Shader {

	public class ShaderCompilationTask implements Runnable{

		private final ClientShader shader;
		private final String vsource,fsource;
		private final String vsourceUrl,fsourceUrl;

		public ShaderCompilationTask(ClientShader shader,String vsource, String vsourceUrl, String fsource, String fsourceUrl){
			this.shader = shader;
			this.vsource = vsource;
			this.vsourceUrl = vsourceUrl;
			this.fsource = fsource;
			this.fsourceUrl = fsourceUrl;
		}

		@Override
		public void run() {
			doCompileShader(vsource,fsource);
		}

		@Override
		public String toString() {
			return "ClientShader {ID=" + program + ", Frag=" + this.fsourceUrl + ", Vert=" + this.vsourceUrl + "}";
		}

		private void doCompileShader(String vsource, String fsource){
			if (((Client) Spout.getEngine()).getRenderMode() == RenderMode.GL11) {
				return;
			}

			//Create a new Shader object on the GPU
			program = GL20.glCreateProgram();

			System.out.println("Compiling " + this.toString());

			int vShader = ShaderHelper.compileShader(vsource, vsourceUrl, GL20.GL_VERTEX_SHADER);
			GL20.glAttachShader(program, vShader);

			int fShader = ShaderHelper.compileShader(fsource, fsourceUrl, GL20.GL_FRAGMENT_SHADER);
			GL20.glAttachShader(program, fShader);

			GL20.glLinkProgram(program);

			int status = GL20.glGetProgrami(program, GL20.GL_LINK_STATUS);
			if (status != GL11.GL_TRUE) {
				String error = GL20.glGetProgramInfoLog(program, 255);
				throw new ShaderCompileException("Link Error in " + vsourceUrl + ", " + fsourceUrl +": " + error);
			}
			if (validateShader) {
				//Shaders
				shader.shaderName = "Shader " + this.fsourceUrl + " " + this.vsourceUrl;
				shader.attachedShaders = GL20.glGetProgrami(program, GL20.GL_ATTACHED_SHADERS);

				//Attributes
				int activeAttributes = GL20.glGetProgrami(program, GL20.GL_ACTIVE_ATTRIBUTES);
				int maxAttributeLength = GL20.glGetProgrami(program, GL20.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH);
				for (int i = 0; i < activeAttributes; i++) {
					String name = GL20.glGetActiveAttrib(program, i, maxAttributeLength);
					int type = GL20.glGetActiveAttribType(program, i);
					int size = GL20.glGetActiveAttribSize(program, i);
					
					AttrUniInfo info = new AttrUniInfo(name, type, size, i, false);

					shader.attributes.put(info.getLocation(), info);
				}

				//Uniforms
				int activeUniforms = GL20.glGetProgrami(program, GL20.GL_ACTIVE_UNIFORMS);
				int maxUniformLength = GL20.glGetProgrami(program, GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH);
				for (int i = 0; i < activeUniforms; i++) {
					String name = GL20.glGetActiveUniform(program, i, maxUniformLength);
					int type = GL20.glGetActiveUniformType(program, i);
					int size = GL20.glGetActiveUniformSize(program, i);

					//Fix to avoid [0] at the end of uniform name
					if(size != 1)
						name = name.substring(0, name.length() - 3);

					AttrUniInfo info = new AttrUniInfo(name, type, size, i, true);

					shader.uniforms.put(info.getName(), info);
				}

				//Dump
				shader.dump();

				// Show the log, info/warning based on whether validation was successful
				// Print line by line to avoid botched time stamps
				GL20.glValidateProgram(shader.program);
				String[] logLines  = GL20.glGetProgramInfoLog(program, 255).split("\n");
				if (GL20.glGetProgrami(program, GL20.GL_VALIDATE_STATUS) == GL11.GL_TRUE) {
					for (String line : logLines) {
						Spout.getLogger().info(line.trim());
					}
				} else {
					for (String line : logLines) {
						Spout.getLogger().warning(line.trim());
					}
				}
			}
			SpoutRenderer.checkGLError();
		}
	}

	public class AttrUniInfo{

		private final String name;
		private final int type, size, location;
		private final boolean isUniform;

		public AttrUniInfo(String name, int type, int size, int location, boolean isUniform){
			this.name = name;
			this.type = type;
			this.size = size;
			this.location = location;
			this.isUniform = isUniform;
		}

		public String getName(){
			return name;
		}

		public int getType(){
			return type;
		}

		public int getSize(){
			return size;
		}

		public int getLocation(){
			return location;
		}

		public boolean isUniform(){
			return isUniform;
		}

		@Override
		public String toString(){
			return name + " type:" + SpoutRenderer.getGLTypeName(type) + (size == 1 ? "" : "[" + size + "]");
		}
	}

	int program;

	String shaderName;
	int attachedShaders;
	Map<Integer, AttrUniInfo> attributes = new HashMap<Integer, ClientShader.AttrUniInfo>();
	Map<String, AttrUniInfo> uniforms = new HashMap<String, ClientShader.AttrUniInfo>();

	HashMap<String, ShaderVariable> variables = new HashMap<String, ShaderVariable>();
	HashMap<String, TextureSamplerShaderVariable> textures = new HashMap<String, TextureSamplerShaderVariable>();
	List<String> dirtyVariables = new ArrayList<String>();
	List<String> dirtyTextures = new ArrayList<String>();

	int maxTextures;

	public static boolean validateShader = true;

	public ClientShader(){

	}

	public ClientShader(String vshaderSource, String vshaderUrl, String fshaderSource, String fshaderUrl, boolean override){
		doCompileShader(vshaderSource, vshaderUrl, fshaderSource, fshaderUrl);
	}

	public ClientShader(String vertexShader, String fragmentShader) {

		System.out.println("Compiling " + vertexShader + " and " + fragmentShader);



		//Compile the vertex shader
		String vshader;
		if (vertexShader == null) {
			vshader = fallbackVertexShader;
		} else {
			try {
				vshader = ShaderHelper.readShaderSource(vertexShader);
			} catch (FileNotFoundException e) {
				System.out.println("Vertex Shader: " + vertexShader + " Not found, using fallback");
				vshader = fallbackVertexShader;
			}
		}

		String fshader;
		if (fragmentShader == null) {
			fshader = fallbackFragmentShader;
		} else {
			try {
				fshader = ShaderHelper.readShaderSource(fragmentShader);
			} catch (FileNotFoundException e) {
				System.out.println("Fragment Shader: " + fragmentShader + " Not found, using fallback");
				fshader = fallbackFragmentShader;
			}
		}

		doCompileShader(vshader, vertexShader, fshader, fragmentShader);

	}


	private void doCompileShader(String vsource, String vsourceUrl, String fsource, String fsourceUrl){
		((SpoutClient) Spout.getEngine()).getScheduler().enqueueRenderTask(new ShaderCompilationTask(this, vsource, vsourceUrl, fsource, fsourceUrl));
	}



	@Override
	public void setUniform(String name, int value) {
		variables.put(name, new IntShaderVariable(program, name, value));
		if(assigned == this) dirtyVariables.add(name);
	}


	@Override
	public void setUniform(String name, float value) {
		variables.put(name, new FloatShaderVariable(program, name, value));
		if(assigned == this) dirtyVariables.add(name);
	}

	@Override
	public void setUniform(String name, Vector2 value) {
		variables.put(name, new Vec2ShaderVariable(program, name, value));
		if(assigned == this) dirtyVariables.add(name);
	}

	@Override
	public void setUniform(String name, Matrix[] value) {
		variables.put(name, new Mat4ArrayShaderVariable(program, name, value));
		if(assigned == this) dirtyVariables.add(name);
	}

	@Override
	public void setUniform(String name, Vector3 value) {
		variables.put(name, new Vec3ShaderVariable(program, name, value));
		if(assigned == this) dirtyVariables.add(name);
	}

	public void setUniform(String name, Vector3[] value) {
		variables.put(name, new Vector3ArrayShaderVariable(program, name, value));
		if(assigned == this) dirtyVariables.add(name);
	}

	@Override
	public void setUniform(String name, Vector4 value) {
		variables.put(name, new Vec4ShaderVariable(program, name, value));
		if(assigned == this) dirtyVariables.add(name);
	}


	@Override
	public void setUniform(String name, Matrix value) {
		if (value.getDimension() == 2) {
			variables.put(name, new Mat2ShaderVariable(program, name, value));
		} else if (value.getDimension() == 3) {
			variables.put(name, new Mat3ShaderVariable(program, name, value));
		} else if (value.getDimension() == 4) {
			variables.put(name, new Mat4ShaderVariable(program, name, value));
		}
		if(assigned == this) dirtyVariables.add(name);
	}

	@Override
	public void setUniform(String name, Color value) {
		variables.put(name, new ColorShaderVariable(program, name, value));
		if(assigned == this) dirtyVariables.add(name);
	}


	@Override
	public void setUniform(String name, Texture value) {
		textures.put(name, new TextureSamplerShaderVariable(program, name, value));
		if(assigned == this) dirtyTextures.add(name);
	}


	@Override
	public void enableAttribute(String name, int size, int type, int stride, long offset, int layout) {
		GL20.glBindAttribLocation(program, layout, name);
		SpoutRenderer.checkGLError();
		//GL20.glEnableVertexAttribArray(layout);
		//GL20.glVertexAttribPointer(layout, size, type, false, 0, offset);
	}

	private static ClientShader assigned = null;

	@Override
	public void assign() {
		if (((Client)Spout.getEngine()).getRenderMode()==RenderMode.GL11) {
			assign(true);
			return;
		}

		if(assigned != this){
			GL20.glUseProgram(program);
			SpoutRenderer.checkGLError();
			for (ShaderVariable v : variables.values()) {
				v.assign();
			}
			dirtyVariables.clear();
			int i = 0;
			for(TextureSamplerShaderVariable v : textures.values()){
				v.bind(i);
				i++;
			}
			dirtyTextures.clear();
			assigned = this;
		}else{
			for(String key : dirtyVariables) {
				variables.get(key).assign();
			}
			dirtyVariables.clear();

			if(!dirtyTextures.isEmpty()){ // MUST all reassign it, because keep texture number
				int i = 0;
				for(TextureSamplerShaderVariable v : textures.values()){
					v.bind(i);
					i++;
				}
				dirtyTextures.clear();
			}

		}
	}

	public void assign(boolean compatibilityMode) {
		// Overriden by basic shader
	}

	String fallbackVertexShader = "#version 120\n" +
			"attribute vec4 vPosition;\n" +
			"attribute vec4 vColor;\n" +
			"attribute vec2 vTexCoord; \n" +
			"varying vec4 color;\n" +
			"varying vec2 uvcoord; \n" +
			"uniform mat4 Projection; \n" +
			"uniform mat4 View; \n" +
			"void main() \n" +
			"{\n    gl_Position = Projection * View * vPosition; \n" +
			"	uvcoord = vTexCoord; \n" +
			"color = vColor; \n" +
			"} \n";
	String fallbackFragmentShader = "#version 120\n" +
			"varying vec4 color;  //in \n" +
			"varying vec2 uvcoord; \n" +
			"uniform sampler2D texture; \n" +
			"void main()\n{\n" +
			"gl_FragColor =  color; \n} \n";

	private void dispose() {
		if(program != -1 ) GL20.glDeleteProgram(program);
	}

	public void finalize() {
		dispose();
	}

	private RenderMaterial renderMaterial = null;

	@Override
	public RenderMaterial getMaterialAssigned() {
		return renderMaterial;
	}

	@Override
	public void setMaterialAssigned(RenderMaterial material) {
		this.renderMaterial = material;
	}
	
	public void checkAttributes(List<Integer> used){
		/*Map<Integer,AttrUniInfo> map = new HashMap<Integer, ClientShader.AttrUniInfo>(attributes);
		
		for(Integer layout : used){
			if(map.remove(layout) == null){
				Spout.getLogger().warning( "In " + shaderName + " Attribut " + layout + " don't exist");
			}
		}
		
		for(Entry<Integer, AttrUniInfo> var : map.entrySet()){
			Spout.getLogger().warning( "In " + shaderName + " Attribut " + var.getValue().getName() + " not assigned");
		}*/
	}
	
	public void checkUniform(){
		if(!dirtyTextures.isEmpty() || !dirtyVariables.isEmpty()){
			throw new IllegalStateException("You must check uniform after assign the shader");
		}
		
		Map<String,AttrUniInfo> map = new HashMap<String, ClientShader.AttrUniInfo>(uniforms);
		
		for(Entry<String, ShaderVariable> var : variables.entrySet()){
			if(map.remove(var.getKey()) == null){
				Spout.getLogger().warning( "In " + shaderName + " Uniform " + var.getKey() + " don't exist");
			}
		}
		
		for(Entry<String, TextureSamplerShaderVariable> var : textures.entrySet()){
			if(map.remove(var.getKey()) == null){
				Spout.getLogger().warning( "In " + shaderName + " Uniform " + var.getKey() + " don't exist");
			}
		}
		
		for(Entry<String, AttrUniInfo> var : map.entrySet()){
			Spout.getLogger().warning( "In " + shaderName + " Uniform " + var.getKey() + " not assigned");
		}
	}

	public void dump(){
		System.out.println("Attached Shaders: " + attachedShaders);

		System.out.println("Active Attributes: " + attributes.size());
		for (AttrUniInfo i : attributes.values()) {
			System.out.println("\t" + i);
		}

		System.out.println("Active Uniforms: " + uniforms.size());
		for (AttrUniInfo i : uniforms.values()) {
			System.out.println("\t" + i);
		}
	}
}
