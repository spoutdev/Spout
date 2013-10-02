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
package org.spout.engine;

import java.awt.Canvas;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.List;

import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.entity.Player;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.gui.FullScreen;

import org.spout.engine.gui.SpoutScreenStack;
import org.spout.math.imaginary.Quaternion;
import org.spout.math.matrix.Matrix4;
import org.spout.math.vector.Vector2;
import org.spout.math.vector.Vector3;
import org.spout.renderer.Camera;
import org.spout.renderer.Material;
import org.spout.renderer.Pipeline;
import org.spout.renderer.Pipeline.PipelineBuilder;
import org.spout.renderer.data.Color;
import org.spout.renderer.data.VertexAttribute;
import org.spout.renderer.data.VertexAttribute.DataType;
import org.spout.renderer.data.VertexData;
import org.spout.renderer.gl.Context;
import org.spout.renderer.gl.Context.Capability;
import org.spout.renderer.gl.GLFactory;
import org.spout.renderer.gl.Program;
import org.spout.renderer.gl.Shader.ShaderType;
import org.spout.renderer.gl.VertexArray;
import org.spout.renderer.model.Model;
import org.spout.renderer.util.ObjFileLoader;

public class SpoutRenderer {
	/**
	 * Target Frames per Second for the renderer
	 */
	public static final int TARGET_FPS = 60;
	private final SpoutClient client;
	private final GLFactory gl;
	private Canvas parent;
	private SpoutScreenStack screenStack;
	private final Vector2 resolution;
	private final float aspectRatio;

	public SpoutRenderer(SpoutClient client, GLFactory gl, Vector2 resolution) {
		this.client = client;
		this.gl = gl;
		this.resolution = resolution;
		this.aspectRatio = resolution.getX() / resolution.getY();
		final FullScreen mainScreen = new FullScreen();
		mainScreen.setTakesInput(false);
		this.screenStack = new SpoutScreenStack(mainScreen);
	}

	public void init() {
		initRendering();
	}

	public void dispose() {
		disposeRendering();
	}

	public void render(float dt) {
		generateChunkModels();
		renderer.setCamera(((Client) Spout.getEngine()).getPlayer().getType(Camera.class));
		pipeline.run(renderer);
		model.setRotation(Quaternion.fromAngleDegAxis(20 * dt, 1, 0, 0).mul(Quaternion.fromAngleDegAxis(10 * dt, 0, 1, 0)).mul(model.getRotation()));
	}

	public SpoutScreenStack getScreenStack() {
		return screenStack;
	}

	public Vector2 getResolution() {
		return resolution;
	}

	public float getAspectRatio() {
		return aspectRatio;
	}

	public Canvas getParent() {
		return parent;
	}

	public void setParent(Canvas parent) {
		this.parent = parent;
	}

	public GLFactory getGL() {
		return gl;
	}

	private Context renderer;
	private Pipeline pipeline;
	private Program program;
	private Material material;
	private final Camera camera = new ClientCamera();
	private List<Model> renderList;
	private Model model;
	// TODO: this should be a queue
	private final Map<Vector3, VertexData> chunkMeshes = new ConcurrentHashMap<>();
	private final Map<Vector3, Model> chunkModels = new HashMap<>();

	private void initRendering() {
		renderer = gl.createContext();
		renderer.setWindowTitle(client.getName());
		renderer.setWindowSize(resolution);
		renderer.create();
		renderer.setClearColor(Color.DARK_GRAY);

		program = Spout.getFileSystem().getResource("shader://Spout/fallbacks/fallback.ssf");
		program.getShader(ShaderType.VERTEX).create();
		program.getShader(ShaderType.FRAGMENT).create();
		program.create();

		material = new Material(program);

		renderer.setCamera(camera);

		renderList = new ArrayList<>();
		PipelineBuilder builder = new PipelineBuilder();
		builder.enableCapabilities(Capability.DEPTH_TEST);
		builder.renderModels(renderList);
		pipeline = builder.build();

		final TFloatList positions = new TFloatArrayList();
		final TFloatList textureCoords = new TFloatArrayList();
		final TFloatList normals = new TFloatArrayList();
		final TIntList indices = new TIntArrayList();
		ObjFileLoader.load(SpoutRenderer.class.getResourceAsStream("/models/meshes/cube.obj"), positions, textureCoords, normals, indices);
		final VertexData vertexData = new VertexData();
		final VertexAttribute posAttribute = new VertexAttribute("positions", DataType.FLOAT, 3);
		posAttribute.setData(positions);
		vertexData.addAttribute(0, posAttribute);
		vertexData.getIndices().addAll(indices);
		final VertexArray vertexArray = gl.createVertexArray();
		vertexArray.setData(vertexData);
		vertexArray.create();

		model = new Model(vertexArray, material);
		model.setPosition(new Vector3(0, 5, -5));
		renderList.add(model);
	}

	private void disposeRendering() {
		program.destroy();

		material = null;

		for (Model model : renderList) {
			model.getVertexArray().destroy();
		}

		renderList.clear();

		chunkMeshes.clear();

		renderer.destroy();
	}

	public void addMesh(ChunkSnapshot chunk, VertexData mesh) {
		if (renderList.size() < 100) {
			System.out.println(chunk.getBase());
			chunkMeshes.put(chunk.getBase(), mesh);
		}
	}

	private void generateChunkModels() {
		for (Entry<Vector3, VertexData> chunkMesh : chunkMeshes.entrySet()) {
			final Vector3 chunk = chunkMesh.getKey();
			final VertexData mesh = chunkMesh.getValue();

			final VertexArray vertexArray = gl.createVertexArray();
			vertexArray.setData(mesh);
			vertexArray.create();

			final Model model = new Model(vertexArray, material);
			model.setPosition(chunk);

			renderList.add(model);

			chunkModels.put(chunk, model);
		}

		chunkMeshes.clear();
	}

	private static class ClientCamera extends Camera {
		private final Player player;
		public ClientCamera() {
			super(Matrix4.ZERO);
			player = ((Client) Spout.getEngine()).getPlayer();
		}

		@Override
		public Matrix4 getProjectionMatrix() {
			return player.getType(Camera.class).getProjectionMatrix();
		}

		@Override
		public Matrix4 getViewMatrix() {
			return player.getType(Camera.class).getViewMatrix();
		}

		@Override
		public Vector3 getPosition() {
			return player.getType(Camera.class).getPosition();
		}

		@Override
		public void setPosition(Vector3 position) {
			player.getType(Camera.class).setPosition(position);
		}

		@Override
		public Quaternion getRotation() {
			return player.getType(Camera.class).getRotation();
		}

		@Override
		public void setRotation(Quaternion rotation) {
			player.getType(Camera.class).setRotation(rotation);
		}

		@Override
		public Vector3 getRight() {
			return player.getType(Camera.class).getRight();
		}

		@Override
		public Vector3 getUp() {
			return player.getType(Camera.class).getUp();
		}

		@Override
		public Vector3 getForward() {
			return player.getType(Camera.class).getForward();
		}

	}
}
