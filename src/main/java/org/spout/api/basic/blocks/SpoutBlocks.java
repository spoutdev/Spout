package org.spout.api.basic.blocks;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.spout.api.material.BlockMaterial;
import org.spout.api.material.Material;
import org.spout.api.material.MaterialData;


public final class SpoutBlocks {
	public static final BlockMaterial air = new SpoutBlock("air", 0);
	public static final BlockMaterial solid = new SpoutBlock("solid", 1).setHardness(1.f);
	public static final BlockMaterial unbreakable = new SpoutBlock("Unbreakable", 2).setHardness(100.f);
	
	public static void initialize() {
		Field[] fields = SpoutBlocks.class.getFields();
		for (Field f : fields) {
			if (Modifier.isStatic(f.getModifiers())) {
				try {
					Object value = f.get(null);
					if (value instanceof Material) {
						MaterialData.registerMaterial((Material) value);
					}
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				}
			}
		}
	}
}
