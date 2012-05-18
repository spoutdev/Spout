package org.spout.engine.renderer.shader;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.math.Matrix;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.math.Vector4;
import org.spout.api.render.RenderMode;
import org.spout.api.render.Shader;
import org.spout.api.render.Texture;
import org.spout.api.resource.Resource;

import org.spout.engine.renderer.shader.variables.AttributeShaderVariable;
import org.spout.engine.renderer.shader.variables.ColorShaderVariable;
import org.spout.engine.renderer.shader.variables.FloatShaderVariable;
import org.spout.engine.renderer.shader.variables.IntShaderVariable;
import org.spout.engine.renderer.shader.variables.Mat2ShaderVariable;
import org.spout.engine.renderer.shader.variables.Mat3ShaderVariable;
import org.spout.engine.renderer.shader.variables.Mat4ShaderVariable;
import org.spout.engine.renderer.shader.variables.ShaderVariable;
import org.spout.engine.renderer.shader.variables.TextureSamplerShaderVariable;
import org.spout.engine.renderer.shader.variables.Vec2ShaderVariable;
import org.spout.engine.renderer.shader.variables.Vec3ShaderVariable;
import org.spout.engine.renderer.shader.variables.Vec4ShaderVariable;

/**
 * Represents a Shader Object in OpenGL
 * @author RoyAwesome
 */
public class ClientShader extends Resource implements Shader {
	int program;
	int textures = 0;
	HashMap<String, ShaderVariable> variables = new HashMap<String, ShaderVariable>();
	public static boolean validateShader = true;

	public static final ClientShader BASIC = new BasicShader();
	
	public ClientShader(){
		
	}
	
	public ClientShader(String vshaderSource, String fshaderSource, boolean override){
		doCompileShader(vshaderSource, fshaderSource);
	}
	
	
	public ClientShader(String vertexShader, String fragmentShader) {

		System.out.println("Compiling " + vertexShader + " and " + fragmentShader);
		
		
	
		//Compile the vertex shader
		String vshader;
		if (vertexShader == null) {
			vshader = fallbackVertexShader;
		} else {
			try {
				vshader = readShaderSource(vertexShader);
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
				fshader = readShaderSource(fragmentShader);
			} catch (FileNotFoundException e) {
				System.out.println("Fragment Shader: " + fragmentShader + " Not found, using fallback");
				fshader = fallbackFragmentShader;
			}
		}

		doCompileShader(vshader, fshader);
	
	}

	
	private void doCompileShader(String vsource, String fsource){
		if (((Client) Spout.getEngine()).getRenderMode() == RenderMode.GL11) {
			return;
		}
	
		
		//Create a new Shader object on the GPU
		program = GL20.glCreateProgram();
		
		int vShader = compileShader(vsource, GL20.GL_VERTEX_SHADER);
		GL20.glAttachShader(program, vShader);
		
		
		int fShader = compileShader(fsource, GL20.GL_FRAGMENT_SHADER);
		GL20.glAttachShader(program, fShader);

		GL20.glLinkProgram(program);

		int status = GL20.glGetProgram(program, GL20.GL_LINK_STATUS);
		if (status != GL11.GL_TRUE) {
			String error = GL20.glGetProgramInfoLog(program, 255);
			throw new ShaderCompileException("Link Error: " + error);
		}
		if (validateShader) {
			GL20.glValidateProgram(this.program);
			if (GL20.glGetProgram(program, GL20.GL_VALIDATE_STATUS) != GL11.GL_TRUE) {
				String info = GL20.glGetProgramInfoLog(program, 255);
				System.out.println("Validate Log: \n" + info);
			}

			System.out.println("Attached Shaders: " + GL20.glGetProgram(program, GL20.GL_ATTACHED_SHADERS));
			int activeAttributes = GL20.glGetProgram(program, GL20.GL_ACTIVE_ATTRIBUTES);
			System.out.println("Active Attributes: " + activeAttributes);
			int maxAttributeLength = GL20.glGetProgram(program, GL20.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH);
			for (int i = 0; i < activeAttributes; i++) {
				System.out.println("\t" + GL20.glGetActiveAttrib(program, i, maxAttributeLength));
			}

			int activeUniforms = GL20.glGetProgram(program, GL20.GL_ACTIVE_UNIFORMS);
			System.out.println("Active Uniforms: " + activeUniforms);
			int maxUniformLength = GL20.glGetProgram(program, GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH);
			for (int i = 0; i < activeUniforms; i++) {
				System.out.println("\t" + GL20.glGetActiveUniform(program, i, maxUniformLength));
			}
		}
		System.out.println("Compiled Shader with id: " + program);
	}
	
	
	
	/* (non-Javadoc)
		 * @see org.spout.client.renderer.shader.IShader#setUniform(java.lang.String, int)
		 */
	@Override
	public void setUniform(String name, int value) {
		variables.put(name, new IntShaderVariable(program, name, value));
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.shader.IShader#setUniform(java.lang.String, float)
		 */
	@Override
	public void setUniform(String name, float value) {
		variables.put(name, new FloatShaderVariable(program, name, value));
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.shader.IShader#setUniform(java.lang.String, org.spout.api.math.Vector2)
		 */
	@Override
	public void setUniform(String name, Vector2 value) {
		variables.put(name, new Vec2ShaderVariable(program, name, value));
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.shader.IShader#setUniform(java.lang.String, org.spout.api.math.Vector3)
		 */
	@Override
	public void setUniform(String name, Vector3 value) {
		variables.put(name, new Vec3ShaderVariable(program, name, value));
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.shader.IShader#setUniform(java.lang.String, org.spout.api.math.Vector4)
		 */
	@Override
	public void setUniform(String name, Vector4 value) {
		variables.put(name, new Vec4ShaderVariable(program, name, value));
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.shader.IShader#setUniform(java.lang.String, org.spout.api.math.Matrix)
		 */
	@Override
	public void setUniform(String name, Matrix value) {
		if (value.getDimension() == 2) {
			variables.put(name, new Mat2ShaderVariable(program, name, value));
		} else if (value.getDimension() == 3) {
			variables.put(name, new Mat3ShaderVariable(program, name, value));
		} else if (value.getDimension() == 4) {
			variables.put(name, new Mat4ShaderVariable(program, name, value));
		}
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.shader.IShader#setUniform(java.lang.String, org.spout.api.util.Color)
		 */
	@Override
	public void setUniform(String name, Color value) {
		variables.put(name, new ColorShaderVariable(program, name, value));
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.shader.IShader#setUniform(java.lang.String, org.newdawn.slick.opengl.Texture)
		 */
	@Override
	public void setUniform(String name, Texture value) {
		if (variables.containsKey(name)) {
			ShaderVariable texture = variables.get(name);
			if (!(texture instanceof TextureSamplerShaderVariable)) {
				throw new IllegalStateException(name + " is not a texture!");
			}
			TextureSamplerShaderVariable t = (TextureSamplerShaderVariable) texture;
			t.set(value);
		} else {
			textures++;
			variables.put(name, new TextureSamplerShaderVariable(program, name, value, textures));
		}
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.shader.IShader#enableAttribute(java.lang.String, int, int, int, long)
		 */
	@Override
	public void enableAttribute(String name, int size, int type, int stride, long offset) {
		variables.put(name, new AttributeShaderVariable(program, name, size, type, stride, offset));
	}

	/* (non-Javadoc)
		 * @see org.spout.client.renderer.shader.IShader#assign()
		 */
	@Override
	public void assign() {
		GL20.glUseProgram(program);
		for (ShaderVariable v : variables.values()) {
			v.assign();
		}
	}

	private int compileShader(String source, int type) {

		int shader = GL20.glCreateShader(type);
		GL20.glShaderSource(shader, source);
		GL20.glCompileShader(shader);
		int status = GL20.glGetShader(shader, GL20.GL_COMPILE_STATUS);
		
		if (status == GL11.GL_FALSE) {
			String error = GL20.glGetShaderInfoLog(shader, 255);
			Spout.log("Status: " + status);
			Spout.log("Shader: " + source);
			throw new ShaderCompileException("Compile Error in " + ((type == GL20.GL_FRAGMENT_SHADER) ? "Fragment Shader" : "VertexShader") + ": " + error);
		}
		return shader;
	}
	

	private String readShaderSource(String file) throws FileNotFoundException {

		FileInputStream in = new FileInputStream(file);
		Scanner scan = new Scanner(in);

		StringBuilder src = new StringBuilder();

		while (scan.hasNextLine()) {
			src.append(scan.nextLine() + "\n");
		}

		return src.toString();
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
}
