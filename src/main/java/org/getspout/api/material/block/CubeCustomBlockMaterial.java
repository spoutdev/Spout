package org.getspout.api.material.block;

import org.getspout.api.block.design.GenericCubeBlockDesign;
import org.getspout.api.plugin.Plugin;

public abstract class CubeCustomBlockMaterial extends GenericCustomBlockMaterial {

	/**
	 * Creates a new cube block material
	 *
	 * @param plugin making the block
	 * @param name of the block
	 * @param isOpaque true if you want the block solid
	 * @param design to use for the block
	 */
	public CubeCustomBlockMaterial(Plugin addon, String name, boolean isOpaque, GenericCubeBlockDesign design) {
		super(addon, name, isOpaque, design);
	}

	/**
	 * Creates a new opaque/solid cube block material
	 *
	 * @param plugin making the block
	 * @param name of the block
	 * @param design to use for the block
	 */
	public CubeCustomBlockMaterial(Plugin addon, String name, GenericCubeBlockDesign design) {
		super(addon, name);
		setBlockDesign(design);
	}

	/**
	 * Creates a new basic opaque/solid cube block material
	 *
	 * @param plugin making the block
	 * @param name of the block
	 * @param texture url to use for the block - must be a square PNG
	 * @param textureSize width and height of the texture in pixels
	 */
	public CubeCustomBlockMaterial(Plugin addon, String name, String texture, int textureSize) {
		super(addon, name);
		setBlockDesign(new GenericCubeBlockDesign(addon, texture, textureSize));
	}
}
