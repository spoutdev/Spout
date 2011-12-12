package org.getspout.commons.material.block;

import org.getspout.commons.addon.Addon;
import org.getspout.commons.block.design.GenericCubeBlockDesign;
import org.getspout.commons.material.block.GenericCustomBlock;

public abstract class GenericCubeCustomBlock extends GenericCustomBlock {
	
	/**
	 * Creates a new cube block material
	 * 
	 * @param plugin making the block
	 * @param name of the block
	 * @param isOpaque true if you want the block solid
	 * @param design to use for the block
	 */
	public GenericCubeCustomBlock(Addon addon, String name, boolean isOpaque, GenericCubeBlockDesign design) {
		super(addon, name, isOpaque, design);
	}
	
	/**
	 * Creates a new opaque/solid cube block material
	 * 
	 * @param plugin making the block
	 * @param name of the block
	 * @param design to use for the block
	 */
	public GenericCubeCustomBlock(Addon addon, String name, GenericCubeBlockDesign design) {
		super(addon, name);
		this.setBlockDesign(design);
	}
	
	/**
	 * Creates a new basic opaque/solid cube block material
	 * 
	 * @param plugin making the block
	 * @param name of the block
	 * @param texture url to use for the block - must be a square PNG
	 * @param textureSize width and height of the texture in pixels
	 */
	public GenericCubeCustomBlock(Addon addon, String name, String texture, int textureSize) {
		super(addon, name);
		this.setBlockDesign(new GenericCubeBlockDesign(addon, texture, textureSize));
	}
}
