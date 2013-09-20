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

import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;

import org.spout.api.Spout;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.gui.FullScreen;

import org.spout.engine.gui.SpoutScreenStack;
import org.spout.math.imaginary.Quaternion;
import org.spout.math.vector.Vector2;
import org.spout.math.vector.Vector3;
import org.spout.renderer.Camera;
import org.spout.renderer.Material;
import org.spout.renderer.data.Color;
import org.spout.renderer.data.RenderList;
import org.spout.renderer.data.VertexAttribute;
import org.spout.renderer.data.VertexAttribute.DataType;
import org.spout.renderer.data.VertexData;
import org.spout.renderer.gl.Capability;
import org.spout.renderer.gl.GLFactory;
import org.spout.renderer.gl.Program;
import org.spout.renderer.gl.Renderer;
import org.spout.renderer.gl.Shader.ShaderType;
import org.spout.renderer.gl.VertexArray;
import org.spout.renderer.model.Model;
import org.spout.renderer.util.ObjFileLoader;

public class SpoutRenderer {
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
		renderer.render();
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

	private Renderer renderer;
	private Program program;
	private Material material;
	private Camera camera;
	private RenderList renderList;
	private Model model;
	// TODO: this should be a queue
	private final Map<Vector3, VertexData> chunkMeshes = new ConcurrentHashMap<>();
	private final Map<Vector3, Model> chunkModels = new HashMap<>();

	private void initRendering() {
		renderer = gl.createRenderer();
		renderer.setWindowTitle(client.getName());
		renderer.getViewPort().setSize(resolution.getFloorX(), resolution.getFloorY());
		renderer.create();
		renderer.setClearColor(Color.DARK_GRAY);

		program = Spout.getFileSystem().getResource("shader://Spout/fallbacks/fallback.ssf");
		program.getShader(ShaderType.VERTEX).create();
		program.getShader(ShaderType.FRAGMENT).create();
		program.create();

		material = new Material(program);

		camera = Camera.createPerspective(60, resolution.getFloorX(), resolution.getFloorY(), 0.1f, 1000f);
		camera.setPosition(new Vector3(0, 0, 0));

		renderList = new RenderList("chunks", camera, 0);
		renderList.addCapability(Capability.DEPTH_TEST);
		renderer.addRenderList(renderList);

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
		model.setPosition(new Vector3(0, 0, -5));
		renderList.add(model);
	}

	private void disposeRendering() {
		program.destroy();

		material = null;

		for (Model model : renderList) {
			model.getVertexArray().destroy();
		}

		renderList.clear();
		renderList.clearCapabilities();

		chunkMeshes.clear();

		renderer.destroy();
	}

	public void addMesh(ChunkSnapshot chunk, VertexData mesh) {
		if (renderList.size() < 100) {
			System.out.println(chunk.getBase().mul(16));
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
			model.setPosition(chunk.mul(Chunk.BLOCKS.SIZE));

			renderList.add(model);

			chunkModels.put(chunk, model);
		}

		chunkMeshes.clear();
	}
}
