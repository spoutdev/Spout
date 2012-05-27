package org.spout.engine.mesh;

import java.util.ArrayList;
import java.util.Iterator;

import org.spout.api.model.Mesh;
import org.spout.api.model.ModelFace;
import org.spout.api.model.PositionNormalTexture;
import org.spout.api.render.RenderEffect;
import org.spout.api.render.Renderer;
import org.spout.api.resource.Resource;


public class BaseMesh extends Resource implements Mesh, Iterable<ModelFace> {
	ArrayList<ModelFace> faces;
	ArrayList<RenderEffect> effects = new ArrayList<RenderEffect>();
	boolean dirty = false;

	
	public BaseMesh(){
		faces = new ArrayList<ModelFace>();
		
	}
	
	public BaseMesh(ArrayList<ModelFace> faces){
		this.faces = faces;
	}
	
	
	
	public void addRenderEffect(RenderEffect effect) {
		effects.add(effect);
	}

	public void removeRenderEffect(RenderEffect effect) {
		effects.remove(effect);
	}

	public RenderEffect[] getEffects() {
		return effects.toArray(new RenderEffect[effects.size()]);
	}

	private void preBatch(Renderer batcher) {
		for (RenderEffect effect : effects) {
			effect.preBatch(batcher);
		}
	}

	private void postBatch(Renderer batcher) {
		for (RenderEffect effect : effects) {
			effect.postBatch(batcher);
		}
	}

	protected void batch(Renderer batcher) {
		for (ModelFace face : faces) {
			for(PositionNormalTexture vert : face){
				//batcher.addTexCoord(vert.uv);
				//batcher.addNormal(vert.normal);
				batcher.addVertex(vert.position);
			}
		}
	}

	private void preRender(Renderer batcher) {
		for (RenderEffect effect : effects) {
			effect.preDraw(batcher);
		}
	}

	private void postRender(Renderer batcher) {
		for (RenderEffect effect : effects) {
			effect.postDraw(batcher);
		}
	}

	protected void render(Renderer batcher) {
		batcher.render();
	}

	public void draw(Renderer batcher) {
		this.preBatch(batcher);
		this.batch(batcher);
		this.postBatch(batcher);

		this.preRender(batcher);
		this.render(batcher);
		this.postRender(batcher);
	}

	@Override
	public Iterator<ModelFace> iterator() {
		return faces.iterator();
	}
}
