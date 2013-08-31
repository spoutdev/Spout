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

import gnu.trove.map.hash.TIntObjectHashMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.opengl.Util;

import org.spout.api.Spout;
import org.spout.api.component.world.SkydomeComponent;
import org.spout.api.geo.discrete.Point;
import org.spout.api.gui.FullScreen;
import org.spout.api.gui.Screen;
import org.spout.api.gui.Widget;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.gui.render.RenderPartPack;
import org.spout.api.math.Rectangle;
import org.spout.api.render.Camera;
import org.spout.api.render.RenderMode;
import org.spout.api.render.shader.Shader;

import org.spout.engine.batcher.SpriteBatch;
import org.spout.engine.filesystem.resource.ClientRenderMaterial;
import org.spout.engine.filesystem.resource.ClientRenderTexture;
import org.spout.engine.gui.DebugScreen;
import org.spout.engine.gui.SpoutScreenStack;
import org.spout.engine.gui.SpoutWidget;
import org.spout.engine.mesh.BaseMesh;
import org.spout.engine.renderer.BatchVertexRenderer;
import org.spout.engine.renderer.EntityRenderer;
import org.spout.engine.renderer.WorldRenderer;
import org.spout.engine.util.MacOSXUtils;
import org.spout.math.matrix.Matrix4;
import org.spout.math.vector.Vector2;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public class SpoutRenderer {
	private static final TIntObjectHashMap<String> GL_TYPE_NAMES = new TIntObjectHashMap<>();
	private final SpoutClient client;
	private DebugScreen debugScreen;
	private SpoutScreenStack screenStack;
	private boolean showDebugInfos = true;
	//private ArrayList<RenderMaterial> postProcessMaterials = new ArrayList<>();
	private boolean ccoverride = false;
	private Vector2 resolution;
	private float aspectRatio;
	private EntityRenderer entityRenderer;
	private WorldRenderer worldRenderer;
	private boolean wireframe = false;
	private Matrix4 ident = Matrix4.IDENTITY;
	// Screen texture
	private SpriteBatch screenBatcher;
	private ClientRenderTexture t;
	private ClientRenderMaterial mat;
	// Reflected world FBO
	// This will need the stencil buffer
	private boolean useReflexion = false; // Set this to true to experiment
	private ClientRenderTexture reflected;
	private SpriteBatch reflectedDebugBatch; // Debug
	private ClientRenderMaterial reflectedDebugMat; //Debug

	public SpoutRenderer(SpoutClient client, Vector2 resolution, boolean ccoverride) {
		this.client = client;
		this.resolution = resolution;
		this.aspectRatio = resolution.getX() / resolution.getY();

		// Building the screenStack
		FullScreen mainScreen = new FullScreen();
		mainScreen.setTakesInput(false);
		this.screenStack = new SpoutScreenStack(mainScreen);
		this.debugScreen = (DebugScreen) screenStack.getDebugHud();

		this.entityRenderer = new EntityRenderer();

		this.ccoverride = ccoverride;

		worldRenderer = new WorldRenderer();
	}

	public void initRenderer(Canvas parent) {
		createWindow(parent);

		if (Spout.debugMode()) {
			client.getLogger().info("SpoutClient Information");
			client.getLogger().info("Operating System: " + System.getProperty("os.name"));
			client.getLogger().info("Renderer Mode: " + client.getRenderMode().toString());
			client.getLogger().info("GL21: " + GLContext.getCapabilities().OpenGL21 + " GL32: " + GLContext.getCapabilities().OpenGL32);
			client.getLogger().info("Resolution: " + Display.getWidth() + "x" + Display.getHeight());
			client.getLogger().info("OpenGL Information");
			client.getLogger().info("Vendor: " + GL11.glGetString(GL11.GL_VENDOR));
			client.getLogger().info("OpenGL Version: " + GL11.glGetString(GL11.GL_VERSION));
			client.getLogger().info("GLSL Version: " + GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
			client.getLogger().info("Max Textures: " + GL11.glGetInteger(GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS));
			String extensions = "Extensions Supported: ";
			if (client.getRenderMode() == RenderMode.GL30 || client.getRenderMode() == RenderMode.GL40) {
				for (int i = 0; i < GL11.glGetInteger(GL30.GL_NUM_EXTENSIONS); i++) {
					extensions += GL30.glGetStringi(GL11.GL_EXTENSIONS, i) + " ";
				}
			} else {
				extensions += GL11.glGetString(GL11.GL_EXTENSIONS);
			}
			client.getLogger().info(extensions);
		}

		SpoutRenderer.checkGLError();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glFrontFace(GL11.GL_CW);
		GL11.glCullFace(GL11.GL_BACK);
		SpoutRenderer.checkGLError();

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glClearColor((135.f / 255.0f), 206.f / 255.f, 250.f / 255.f, 1);
		SpoutRenderer.checkGLError();

		//Init pool of BatchVertexRenderer
		BatchVertexRenderer.initPool(GL11.GL_TRIANGLES, 500);

		if (useReflexion) {
			reflected = new ClientRenderTexture(true, false, true);
			reflected.writeGPU();
			// Test
			reflectedDebugBatch = new SpriteBatch();
			Shader s1 = client.getFileSystem().getResource("shader://Spout/shaders/diffuse.ssf");
			HashMap<String, Object> map1 = new HashMap<>();
			map1.put("Diffuse", reflected);
			reflectedDebugMat = new ClientRenderMaterial(s1, map1);
			RenderPart screenPart1 = new RenderPart();
			screenPart1.setSprite(new Rectangle(-1, -1, 0.5f, 0.5f));
			screenPart1.setSource(new Rectangle(0, 1, 1, -1));
			RenderPartPack pack1 = new RenderPartPack(reflectedDebugMat);
			pack1.add(screenPart1);
			reflectedDebugBatch.flush(pack1);
			// Test end
		}

		screenBatcher = new SpriteBatch();
		t = new ClientRenderTexture(true, false, true);
		t.writeGPU();
		Shader s = client.getFileSystem().getResource("shader://Spout/shaders/diffuse.ssf");
		HashMap<String, Object> map = new HashMap<>();
		map.put("Diffuse", t);
		mat = new ClientRenderMaterial(s, map);
		RenderPart screenPart = new RenderPart();
		screenPart.setSprite(new Rectangle(-1, -1, 2, 2));
		screenPart.setSource(new Rectangle(0, 1, 1, -1));
		RenderPartPack pack = new RenderPartPack(mat);
		pack.add(screenPart);
		screenBatcher.flush(pack);
	}

	public void updateRender(long limit) {
		worldRenderer.update(limit);
	}

	long guiTime, worldTime, entityTime;

	public void render(float dt) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		final Camera camera = client.getPlayer().getType(Camera.class);
		final SkydomeComponent skydome = client.getWorld().get(SkydomeComponent.class);

		// Render reflected world
		if (useReflexion) {
			reflected.activate();
			GL11.glCullFace(GL11.GL_FRONT);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			if (camera != null) {
				camera.updateReflectedView();

				if (skydome != null && skydome.getModel() != null) {
					skydome.getModel().getRenderMaterial().getShader().setUniform("View", camera.getRotation());
					skydome.getModel().getRenderMaterial().getShader().setUniform("Projection", camera.getProjection());
					skydome.getModel().getRenderMaterial().getShader().setUniform("Model", ident);
					BaseMesh reflectedSkydomeMesh = (BaseMesh) skydome.getModel().getMesh();
					if (!reflectedSkydomeMesh.isBatched()) {
						reflectedSkydomeMesh.batch();
					}
					reflectedSkydomeMesh.render(skydome.getModel().getRenderMaterial());
				}
			}

			worldRenderer.render();
			entityRenderer.render(dt);

			GL11.glCullFace(GL11.GL_BACK);
			reflected.release();
		}

		// Render normal world
		t.activate();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		if (camera != null) {
			camera.updateView();

			if (skydome != null && skydome.getModel() != null) {
				skydome.getModel().getRenderMaterial().getShader().setUniform("View", camera.getRotation());
				skydome.getModel().getRenderMaterial().getShader().setUniform("Projection", camera.getProjection());
				skydome.getModel().getRenderMaterial().getShader().setUniform("Model", ident);
				BaseMesh skydomeMesh = (BaseMesh) skydome.getModel().getMesh();
				if (!skydomeMesh.isBatched()) {
					skydomeMesh.batch();
				}
				skydomeMesh.render(skydome.getModel().getRenderMaterial());
			}
		}

		long start = System.nanoTime();
		worldRenderer.render();
		worldTime = System.nanoTime() - start;
		start = System.nanoTime();
		entityRenderer.render(dt);
		entityTime = System.nanoTime() - start;
		start = System.nanoTime();

		t.release();

		// Render gui
		if (wireframe) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		}

		screenBatcher.render(ident);
		if (useReflexion) {
			reflectedDebugBatch.render(ident);
		}

		//GUI -> Render all widgets
		for (Screen screen : screenStack.getVisibleScreens()) {
			for (Widget widget : screen.getWidgets()) {
				((SpoutWidget) widget).render();
			}
		}

		//GUI -> Give the main screen the mouse if no input screen is set
		final Screen input = screenStack.getInputScreen();
		if (input == null) {
			Mouse.setGrabbed(true);
		} else {
			Mouse.setGrabbed(input.grabsMouse());
		}

		//GUI -> Update debug info
		if (showDebugInfos) {
			int id = 0;
			Point position = client.getPlayer().getPhysics().getPosition();
			debugScreen.spoutUpdate(id++, "Spout client! Logged as " + client.getPlayer().getDisplayName() + " in world: " + client.getWorld().getName());
			debugScreen.spoutUpdate(id++, "x: " + position.getX() + "y: " + position.getY() + "z: " + position.getZ());
			debugScreen.spoutUpdate(id++, "FPS: " + client.getScheduler().getFps() + " (" + (client.getScheduler().isRendererOverloaded() ? "Overloaded" : "Normal") + ")");
			debugScreen.spoutUpdate(id++, "Chunks Loaded: " + client.getWorld().getNumLoadedChunks());
			debugScreen.spoutUpdate(id++, "Total ChunkMeshBatchAggregators in Renderer: " + worldRenderer.getTotalChunks() + "");
			debugScreen.spoutUpdate(id++, "Chunks Drawn: " + ((int) ((float) worldRenderer.getRenderedChunks() / (float) (worldRenderer.getTotalChunks()) * 100)) + "%" + " (" + worldRenderer.getRenderedChunks() + ")");
			debugScreen.spoutUpdate(id++, "Occluded Chunks: " + (int) ((float) worldRenderer.getOccludedChunks() / worldRenderer.getTotalChunks() * 100) + "% (" + worldRenderer.getOccludedChunks() + ")");
			debugScreen.spoutUpdate(id++, "Cull Chunks: " + (int) ((float) worldRenderer.getCulledChunks() / worldRenderer.getTotalChunks() * 100) + "% (" + worldRenderer.getCulledChunks() + ")");
			debugScreen.spoutUpdate(id++, "Entities: " + entityRenderer.getRenderedEntities());
			debugScreen.spoutUpdate(id++, "Buffer: " + worldRenderer.addedBatch + " / " + worldRenderer.updatedBatch);
			debugScreen.spoutUpdate(id++, "Mesh batch queue size: " + ((SpoutClient) Spout.getEngine()).getRenderer().getWorldRenderer().getBatchWaiting());
		}

		for (Screen screen : screenStack.getVisibleScreens()) {
			for (Widget widget : screen.getWidgets()) {
				((SpoutWidget) widget).render();
			}
		}

		guiTime = System.nanoTime() - start;

		if (wireframe) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		}
	}

	public WorldRenderer getWorldRenderer() {
		return worldRenderer;
	}

	public EntityRenderer getEntityRenderer() {
		return entityRenderer;
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

	private void createWindow(Canvas parent) {
		try {
			Display.setDisplayMode(new DisplayMode((int) resolution.getX(), (int) resolution.getY()));
			Display.setParent(parent);
			//Override using ContextAttribs for some videocards that don't support ARB_CREATE_CONTEXT
			if (ccoverride) {
				Display.create(new PixelFormat(8, 24, 0));
				Display.setTitle("Spout Client");
				return;
			}

			if (MacOSXUtils.isMac()) {
				createMacWindow();
			} else {
				if (client.getRenderMode() == RenderMode.GL20) {
					ContextAttribs ca = new ContextAttribs(2, 1);
					Display.create(new PixelFormat(8, 24, 0), ca);
				} else if (client.getRenderMode() == RenderMode.GL30) {
					ContextAttribs ca = new ContextAttribs(3, 2).withForwardCompatible(false);
					Display.create(new PixelFormat(8, 24, 0), ca);
				} else if (client.getRenderMode() == RenderMode.GL40) {
					ContextAttribs ca = new ContextAttribs(4, 0).withForwardCompatible(false);
					Display.create(new PixelFormat(8, 24, 0), ca);
				}
			}

			Display.setTitle("Spout Client");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	private void createMacWindow() throws LWJGLException {
		if (client.getRenderMode() == RenderMode.GL30) {
			if (MacOSXUtils.getOSXVersion() >= 7) {
				ContextAttribs ca = new ContextAttribs(3, 2).withProfileCore(true);
				Display.create(new PixelFormat(8, 24, 0), ca);
			} else {
				throw new UnsupportedOperationException("Cannot create a 3.0 context without OSX 10.7_");
			}
		} else {
			Display.create();
		}
	}

	public void toggleWireframe() {
		if (wireframe) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			wireframe = false;
		} else {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			wireframe = true;
		}
	}

	private static void setGLTypeName(int typeId, String typeName) {
		GL_TYPE_NAMES.put(typeId, typeName);
	}

	static {
		setGLTypeName(GL20.GL_BOOL, "GL_BOOL");
		setGLTypeName(GL20.GL_BOOL_VEC2, "GL_BOOL_VEC2");
		setGLTypeName(GL20.GL_BOOL_VEC3, "GL_BOOL_VEC3");
		setGLTypeName(GL20.GL_BOOL_VEC4, "GL_BOOL_VEC4");
		setGLTypeName(GL20.GL_FLOAT_VEC2, "GL_FLOAT_VEC2");
		setGLTypeName(GL20.GL_FLOAT_VEC3, "GL_FLOAT_VEC3");
		setGLTypeName(GL20.GL_FLOAT_VEC4, "GL_FLOAT_VEC4");
		setGLTypeName(GL20.GL_FLOAT_MAT2, "GL_FLOAT_MAT2");
		setGLTypeName(GL20.GL_FLOAT_MAT3, "GL_FLOAT_MAT3");
		setGLTypeName(GL20.GL_FLOAT_MAT4, "GL_FLOAT_MAT4");
		setGLTypeName(GL20.GL_INT_VEC2, "GL_INT_VEC2");
		setGLTypeName(GL20.GL_INT_VEC3, "GL_INT_VEC3");
		setGLTypeName(GL20.GL_INT_VEC4, "GL_INT_VEC4");
		setGLTypeName(GL20.GL_SAMPLER_1D, "GL_SAMPLER_1D");
		setGLTypeName(GL20.GL_SAMPLER_1D_SHADOW, "GL_SAMPLER_1D_SHADOW");
		setGLTypeName(GL20.GL_SAMPLER_2D, "GL_SAMPLER_2D");
		setGLTypeName(GL20.GL_SAMPLER_2D_SHADOW, "GL_SAMPLER_2D_SHADOW");
		setGLTypeName(GL20.GL_SAMPLER_3D, "GL_SAMPLER_3D");
		setGLTypeName(GL20.GL_SAMPLER_CUBE, "GL_SAMPLER_CUBE");
	}

	/**
	 * Gets the GL type name from a GL type identifier
	 *
	 * @param glType to get the name of
	 * @return The GL type name
	 */
	public static String getGLTypeName(int glType) {
		String name = GL_TYPE_NAMES.get(glType);
		if (name == null) {
			return "UNKNOWN(" + glType + ")";
		} else {
			return name;
		}
	}

	public static void checkGLError() {
		try {
			Util.checkGLError();
		} catch (OpenGLException e) {
			e.printStackTrace();
		}
	}
}
