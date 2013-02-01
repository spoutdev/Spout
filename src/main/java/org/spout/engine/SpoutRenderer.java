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
package org.spout.engine;

import java.awt.Canvas;
import java.util.ArrayList;
import java.util.HashMap;

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

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.entity.Entity;
import org.spout.api.geo.discrete.Point;
import org.spout.api.gui.FullScreen;
import org.spout.api.gui.Screen;
import org.spout.api.gui.ScreenStack;
import org.spout.api.gui.Widget;
import org.spout.api.math.Matrix;
import org.spout.api.math.MatrixMath;
import org.spout.api.math.Vector2;
import org.spout.api.model.Model;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.RenderMode;
import org.spout.api.render.Shader;

import org.spout.engine.batcher.SpriteBatch;
import org.spout.engine.entity.component.SpoutSceneComponent;
import org.spout.engine.input.SpoutInputManager;
import org.spout.engine.mesh.BaseMesh;
import org.spout.engine.renderer.BatchVertexRenderer;
import org.spout.engine.renderer.EntityRenderer;
import org.spout.engine.renderer.WorldRenderer;
import org.spout.engine.resources.ClientFont;
import org.spout.engine.resources.ClientRenderMaterial;
import org.spout.engine.resources.ClientRenderTexture;
import org.spout.engine.util.MacOSXUtils;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public class SpoutRenderer {
	private SpriteBatch gui;
	private ScreenStack screenStack;
	private ClientFont font;
	private boolean showDebugInfos = true;
	private ArrayList<RenderMaterial> postProcessMaterials = new ArrayList<RenderMaterial>();
	private boolean ccoverride = false;
	private Vector2 resolution = new Vector2(1024, 768);
	private float aspectRatio = resolution.getX() / resolution.getY();
	private EntityRenderer entityRenderer;
	private WorldRenderer worldRenderer;
	private boolean wireframe = false;

	public SpoutRenderer(Vector2 resolution, boolean ccoverride) {
		this.resolution = resolution;
		aspectRatio = resolution.getX() / resolution.getY();

		// Building the screenStack
		FullScreen mainScreen = new FullScreen();
		mainScreen.setTakesInput(false);
		screenStack = new ScreenStack(mainScreen);

		entityRenderer = new EntityRenderer();

		this.ccoverride = ccoverride;
	}

	public void initRenderer(Canvas parent) {
		createWindow(parent);

		SpoutClient client = (SpoutClient) Spout.getEngine();

		client.getLogger().info("SpoutClient Information");
		client.getLogger().info("Operating System: " + System.getProperty("os.name"));
		client.getLogger().info("Renderer Mode: " + client.getRenderMode().toString());
		client.getLogger().info("GL21: " + GLContext.getCapabilities().OpenGL21 + " GL32: " + GLContext.getCapabilities().OpenGL32);
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
		SpoutRenderer.checkGLError();
		client.getLogger().info(extensions);
		//soundManager.init();
		Spout.getFilesystem().postStartup();

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
		BatchVertexRenderer.initPool(GL11.GL_TRIANGLES, 10000);

		worldRenderer = new WorldRenderer();

		gui = SpriteBatch.createSpriteBatch(client.getRenderMode(), resolution.getX(), resolution.getY());

		font = (ClientFont) Spout.getFilesystem().getResource("font://Spout/fonts/ubuntu/Ubuntu-M.ttf");

		t = new ClientRenderTexture(true, false, true);
		t.writeGPU();
		Shader s = (Shader) Spout.getFilesystem().getResource("shader://Spout/shaders/diffuse.ssf");
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("Diffuse", t);
		mat = new ClientRenderMaterial(s, map);
	}

	public void updateRender(long limit) {
		worldRenderer.update(limit);
	}

	Matrix ident = MatrixMath.createIdentity();
	ClientRenderTexture t;
	ClientRenderMaterial mat;

	public void render(float dt) {
		SpoutClient client = (SpoutClient) Spout.getEngine();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		t.activate();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		Model skydome = (Model) client.getActiveWorld().getDataMap().get("Skydome");
		if (skydome != null) {
			skydome.getRenderMaterial().getShader().setUniform("View", client.getActiveCamera().getRotation());
			skydome.getRenderMaterial().getShader().setUniform("Projection", client.getActiveCamera().getProjection());
			skydome.getRenderMaterial().getShader().setUniform("Model", ident);
			BaseMesh skydomeMesh = (BaseMesh) skydome.getMesh();
			if (!skydomeMesh.isBatched()) {
				skydomeMesh.batch();
			}
			skydomeMesh.render(skydome.getRenderMaterial());
		}
		//Interpolate entity transform if Physics is not currently applied to the entity
		for (Entity e : client.getActiveWorld().getAll()) {
			final SpoutSceneComponent scene = (SpoutSceneComponent) e.getScene();
			if (scene.getBody() == null) {
				scene.interpolateRender(dt);
			}
		}
		client.getActiveCamera().updateView();

		//Pull input each frame
		((SpoutInputManager) ((Client) Spout.getEngine()).getInputManager()).pollInput(client.getActivePlayer());

		//Call InputExecutor registred by plugin
		((SpoutInputManager) ((Client) Spout.getEngine()).getInputManager()).execute(dt);

		Mouse.setGrabbed(screenStack.getVisibleScreens().getLast().grabsMouse());

		long start = System.nanoTime();

		worldRenderer.render();

		start = System.nanoTime();

		entityRenderer.render(dt);

		if (wireframe) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		}

		start = System.nanoTime();

		t.release();

		gui.begin();

		gui.draw(mat, 0, 1, 1, -1, -1, -1, 2, 2);

		if (showDebugInfos) {
			Point position = client.getActivePlayer().getScene().getPosition();
			gui.drawText(new ChatArguments("Spout client! Logged as ", ChatStyle.RED, client.getActivePlayer().getDisplayName(), ChatStyle.RESET, " in world: ", ChatStyle.RED, client.getActiveWorld().getName()), font, -0.95f, 0.9f, 10f);
			gui.drawText(new ChatArguments(ChatStyle.BLUE, "x: ", position.getX()), font, -0.95f, 0.8f, 8f);
			gui.drawText(new ChatArguments(ChatStyle.BLUE, "y: ", position.getY()), font, -0.95f, 0.7f, 8f);
			gui.drawText(new ChatArguments(ChatStyle.BLUE, "z: ", position.getZ()), font, -0.95f, 0.6f, 8f);
			gui.drawText(new ChatArguments(ChatStyle.BLUE, "fps: ", client.getScheduler().getFps(), " (", client.getScheduler().isRendererOverloaded() ? "Overloaded" : "Normal", ")"), font, -0.95f, 0.5f, 8f);
			gui.drawText(new ChatArguments(ChatStyle.BLUE, "Chunks Drawn: ", ((int) ((float) worldRenderer.getRenderedChunks() / (float) (worldRenderer.getTotalChunks()) * 100)) + "%" + " (" + worldRenderer.getRenderedChunks() + ")"), font, -0.95f, 0.4f, 8f);
			gui.drawText(new ChatArguments(ChatStyle.BLUE, "Occluded Chunks: ", (int) ((float) worldRenderer.getOccludedChunks() / worldRenderer.getTotalChunks() * 100) + "% (" + worldRenderer.getOccludedChunks() + ")"), font, -0.95f, 0.3f, 8f);
			gui.drawText(new ChatArguments(ChatStyle.BLUE, "Cull Chunks: ", (int) ((float) worldRenderer.getCulledChunks() / worldRenderer.getTotalChunks() * 100), "% (" + worldRenderer.getCulledChunks() + ")"), font, -0.95f, 0.2f, 8f);
			gui.drawText(new ChatArguments(ChatStyle.BLUE, "Entities: ", entityRenderer.getEntitiesRended()), font, -0.95f, 0.1f, 8f);
			gui.drawText(new ChatArguments(ChatStyle.BLUE, "Buffer: ", worldRenderer.addedBatch + " / " + worldRenderer.updatedBatch), font, -0.95f, 0.0f, 8f);
			//gui.drawText(new ChatArguments(ChatStyle.BLUE, "Time: ", worldTime / 1000000.0 + " / " + entityTime / 1000000.0 + " / " + guiTime / 1000000.0), font, -0.95f, -0.1f, 8f);
		}
		for (Screen screen : screenStack.getVisibleScreens()) {
			for (Widget widget : screen.getWidgets()) {
				gui.draw(widget.getRenderParts());
			}
		}
		gui.render();

		guiTime = System.nanoTime() - start;

		if (wireframe) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		}
	}

	long guiTime;

	public void toggleDebugInfos() {
		showDebugInfos = !showDebugInfos;
	}

	public WorldRenderer getWorldRenderer() {
		return worldRenderer;
	}

	public ScreenStack getScreenStack() {
		return screenStack;
	}

	public Vector2 getResolution() {
		return resolution;
	}

	public float getAspectRatio() {
		return aspectRatio;
	}

	private void createWindow(Canvas parent) {
		SpoutClient client = (SpoutClient) Spout.getEngine();

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
				if (client.getRenderMode() == RenderMode.GL11) {
					ContextAttribs ca = new ContextAttribs(1, 5);
					Display.create(new PixelFormat(8, 24, 0), ca);
				} else if (client.getRenderMode() == RenderMode.GL20) {
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

		SpoutClient client = (SpoutClient) Spout.getEngine();

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

	public static void checkGLError() {
		try {
			Util.checkGLError();
		} catch (OpenGLException e) {
			e.printStackTrace();
		}
	}
}
