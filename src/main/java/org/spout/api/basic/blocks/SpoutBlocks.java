package org.spout.api.basic.blocks;

import org.spout.api.material.BlockMaterial;

public final class SpoutBlocks {
	public static final BlockMaterial air = new SpoutBlock("air", 0);
	public static final BlockMaterial solid = new SpoutBlock("solid", 1).setHardness(1.f);
	public static final BlockMaterial unbreakable = new SpoutBlock("Unbreakable", 2).setHardness(100.f);
}
