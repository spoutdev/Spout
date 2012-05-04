package org.spout.engine;

import java.awt.Color;
import java.io.File;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.entity.Entity;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.gui.Screen;
import org.spout.api.material.BlockMaterial;
import org.spout.api.math.Matrix;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.plugin.PluginStore;
import org.spout.api.render.BasicCamera;
import org.spout.api.render.Camera;
import org.spout.api.render.RenderMode;
import org.spout.api.render.Shader;
import org.spout.api.render.Texture;

import org.spout.engine.batcher.PrimitiveBatch;
import org.spout.engine.filesystem.FileSystem;
import org.spout.engine.renderer.BatchVertexRenderer;
import org.spout.engine.renderer.shader.ClientShader;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutWorld;
import org.spout.engine.batcher.PrimitiveBatch;
import org.spout.engine.filesystem.FileSystem;


import java.awt.Color;
import java.io.File;
import java.util.Stack;

public class SpoutClient extends SpoutEngine implements Client {
	private final String name = "Spout Client";

	private Camera activeCamera;
	private final Vector2 resolution = new Vector2(854, 480);
	private final float aspectRatio = resolution.getX() / resolution.getY();
	private Stack<Screen> screenStack = new Stack<Screen>();

	public static void main(String[] args) {
		System.setProperty("org.lwjgl.librarypath", System.getProperty("user.dir") + "/natives/");
		SpoutClient c = new SpoutClient();
		Spout.setEngine(c);
		FileSystem.init();

		c.init(args);
		c.start();
	}

	public SpoutClient() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(String[] args) {
		super.init(args);
	}

	@Override
	public void start() {
		super.start();
		scheduler.startRenderThread();
	}

	public void initRenderer() {

		try {
			Display.setDisplayMode(new DisplayMode((int) resolution.getX(), (int) resolution.getY()));

			if (System.getProperty("os.name").toLowerCase().contains("mac")) {
				String[] ver = System.getProperty("os.version").split("\\.");
				if (Integer.parseInt(ver[1]) >= 7) {
					ContextAttribs ca = new ContextAttribs(3, 2).withProfileCore(true);
					Display.create(new PixelFormat(8, 24, 0), ca);
				} else {
					Display.create();
				}
			} else {
				Display.create();
			}

			Display.setTitle("Spout Client");

			System.out.println("OpenGL Information");
			System.out.println("Vendor: " + GL11.glGetString(GL11.GL_VENDOR));
			System.out.println("OpenGL Version: " + GL11.glGetString(GL11.GL_VERSION));
			System.out.println("GLSL Version: " + GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
			String extensions = "Extensions Supported: ";
			for (int i = 0; i < GL11.glGetInteger(GL30.GL_NUM_EXTENSIONS); i++) {
				extensions += GL30.glGetStringi(GL11.GL_EXTENSIONS, i) + " ";
			}
			System.out.println(extensions);
			System.out.println("Max Texture Units: " + GL11.glGetInteger(GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS));
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		activeCamera = new BasicCamera(Matrix.createPerspective(75, aspectRatio, 0.001f, 1000), Matrix.createLookAt(new Vector3(0, 0, -2), Vector3.ZERO, Vector3.UP));
		//Shader shader = new BasicShader();
		Shader shader = new ClientShader("fallback.330.vert", "fallback.330.frag");
		renderer = new PrimitiveBatch();
		//renderer.getRenderer().setShader(shader);

		GL11.glEnable(GL11.GL_DEPTH_TEST);

		texture = (Texture) FileSystem.getResource("texture://Vanilla/terrain.png");
		texture.load(); //Loads texture to GPU
		textureTest = (BatchVertexRenderer) BatchVertexRenderer.constructNewBatch(GL11.GL_TRIANGLES);
		textureTest.setShader(shader);
	}

	Texture texture;
	PrimitiveBatch renderer;
	final boolean[] sides = {true, true, true, true, true, true};
	long ticks = 0;
	BatchVertexRenderer textureTest;

	public void render(float dt) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(1, 1, 1, 1);

		ticks++;
		/*
		double cx = 20 * Math.sin(Math.toRadians(ticks));
		double cz = 20 * Math.cos(Math.toRadians(ticks));
		double cy = 20 * Math.sin(Math.toRadians(ticks));

		Matrix view = Matrix.createLookAt(new Vector3(cx,cy,cz), Vector3.ZERO, Vector3.UP);
		renderer.getRenderer().getShader().setUniform("View", view);*/
		//renderer.getRenderer().getShader().setUniform("Projection", activeCamera.getProjection());

		/*
		
		if(this.getLiveWorlds().size() > 0){
			Object[] worlds = this.getWorlds().toArray();
			SpoutWorld world = (SpoutWorld)worlds[0];
			renderVisibleChunks(world);
			
		}			
		else{
			renderer.addCube(Vector3.ZERO, Vector3.ONE, Color.red, sides);
		}
		
		
		renderer.draw();
		*/

		textureTest.getShader().setUniform("View", activeCamera.getView());
		textureTest.getShader().setUniform("Projection", activeCamera.getProjection());
		textureTest.getShader().setUniform("tex", texture);

		/*
		 * renderer.addColor(col);
		renderer.addVertex(a);
		renderer.addColor(col);		
		renderer.addVertex(b);
		renderer.addColor(col);		
		renderer.addVertex(c);
		
		renderer.addColor(col);		
		renderer.addVertex(c);
		renderer.addColor(col);
		renderer.addVertex(a);
		renderer.addColor(col);
		renderer.addVertex(d);
		*/

		textureTest.begin();
		//texture.bind();
		textureTest.addTexCoord(0, 0);
		textureTest.addVertex(0, 0);
		textureTest.addTexCoord(1, 0);
		textureTest.addVertex(1, 0);
		textureTest.addTexCoord(0, 1);
		textureTest.addVertex(0, 1);

		textureTest.addTexCoord(0, 1);
		textureTest.addVertex(0, 1);
		textureTest.addTexCoord(1, 1);
		textureTest.addVertex(1, 1);
		textureTest.addTexCoord(1, 0);
		textureTest.addVertex(1, 0);
		textureTest.end();
		//textureTest.dumpBuffers();
		textureTest.render();
	}

	@SuppressWarnings("unused")
	private void renderVisibleChunks(SpoutWorld world) {
		renderer.begin();

		for (int x = 0; x < 1; x++) {
			for (int y = 4; y < 5; y++) {
				for (int z = 0; z < 1; z++) {
					SpoutChunk c = world.getChunk(x, y, z);
					ChunkSnapshot snap = c.getSnapshot();
					renderChunk(snap, renderer);
				}
			}
		}
		renderer.end();
	}

	private void renderChunk(ChunkSnapshot snap, PrimitiveBatch batch) {
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					BlockMaterial m = snap.getBlockMaterial(x, y, z);

					Color col = getColor(m);
					if (m.isSolid()) {
						batch.addCube(new Vector3(x, y, z), Vector3.ONE, col, sides);
					}
				}
			}
		}
	}

	@Override
	public File getTemporaryCache() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getStatsFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity getActivePlayer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public World getWorld() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Camera getActiveCamera() {
		return activeCamera;
	}

	@Override
	public void setActiveCamera(Camera activeCamera) {
		this.activeCamera = activeCamera;
	}

	@Override
	public PluginStore getPluginStore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getResourcePackFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	private Color getColor(BlockMaterial m) {
		if (!m.isSolid()) {
			return new Color(0, 0, 0);
		}
		switch (m.getId()) {
			case 78:
				return new Color(255, 255, 255);
			case 24:
			case 12:
				return new Color(210, 210, 150);
			case 10:
				return new Color(200, 50, 50);
			case 9:
			case 8:
				return new Color(150, 150, 200);
			case 7:
				return new Color(50, 50, 50);
			case 4:
				return new Color(100, 100, 100);
			case 17:
			case 3:
				return new Color(110, 75, 35);
			case 18:
			case 2:
				return new Color(55, 140, 55);
			case 21:
			case 16:
			case 15:
			case 14:
			case 13:
			case 1:
			default:
				return new Color(150, 150, 150);
		}
	}

	@Override
	public RenderMode getRenderMode() {
		return RenderMode.GL30;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public Stack<Screen> getScreenStack() {
		return screenStack;
	}

	@Override
	public void openScreen(Screen screen) {
		screenStack.add(screen);
	}

	@Override
	public void closeScreen() {
		screenStack.pop();
	}

	@Override
	public void closeScreen(Screen screen) {
		screenStack.remove(screen);
	}

	@Override
	public Screen getFocusedScreen() {
		return screenStack.firstElement();
	}
}
