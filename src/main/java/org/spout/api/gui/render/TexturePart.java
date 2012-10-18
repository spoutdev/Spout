package org.spout.api.gui.render;

import org.spout.api.render.Texture;

public class TexturePart extends RenderPart {
	Texture texture;
	
	public Texture getTexture() {
		return texture;
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
}
