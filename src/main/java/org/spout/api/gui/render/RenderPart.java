package org.spout.api.gui.render;

import java.awt.Color;

import org.spout.api.math.Rectangle;
import org.spout.api.render.RenderMaterial;

public abstract class RenderPart implements Comparable<RenderPart> {
	Rectangle source;
	Rectangle sprite;
	int zIndex = 0;
	Color color;
	RenderMaterial material;
	
	public void setSource(Rectangle source) {
		this.source = source;
	}
	
	public void setSprite(Rectangle sprite) {
		this.sprite = sprite;
	}
	
	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
	}
	
	public int getZIndex() {
		return zIndex;
	}
	
	public Rectangle getSource() {
		return source;
	}
	
	public Rectangle getSprite() {
		return sprite;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	@Override
	public int compareTo(RenderPart arg0) {
		return arg0.getZIndex() - getZIndex();
	}
	
	public RenderMaterial getRenderMaterial() {
		return material;
	}
	
	public void setRenderMaterial(RenderMaterial material) {
		this.material = material;
	}
}
