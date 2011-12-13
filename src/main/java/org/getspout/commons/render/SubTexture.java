package org.getspout.commons.render;

public class SubTexture {
	Texture parent;
	int xLoc;
	int yLoc;
	
	int xTopLoc;
	int yTopLoc;
	
	public SubTexture(Texture parent, int xLoc, int yLoc, int spriteSize) {
		this.parent = parent;
		this.xLoc = xLoc;
		this.xTopLoc = xLoc + spriteSize;
		this.yLoc = yLoc;
		this.yTopLoc = yLoc + spriteSize;
	}
	
	/**
	 * Gets the left-sided X of this subtexture
	 * @return xLoc
	 */
	public int getXLoc() {
		return xLoc;
	}
	
	/*
	 * Gets the bottom-sided y of this subtexture
	 * @return yLoc
	 */
	public int getYLoc() {
		return yLoc;
	}
	
	/**
	 * Gets the right-sided x of this subtexture
	 * 
	 * @return xTopLoc
	 */
	public int getXTopLoc() {
		return xTopLoc;
	}
	
	/**
	 * Gets the top-sided y of this subtexture
	 * 
	 * @return yTopLoc
	 */
	public int getYTopLoc() {
		return yTopLoc;
	}
	
	/**
	 * Gets the parent texture of this subtexture
	 * 
	 * @return parent Texture
	 */
	public Texture getParent() {
		return parent;
	}
}