package org.getspout.spout.item.mcitem;

import java.lang.reflect.Field;

import net.minecraft.server.Block;
import net.minecraft.server.EnumToolMaterial;
import net.minecraft.server.Item;
import net.minecraft.server.ItemPickaxe;
import net.minecraft.server.ItemTool;
import net.minecraft.server.Material;

public class CustomItemPickaxe extends ItemPickaxe{

	public CustomItemPickaxe(int i, EnumToolMaterial etm) {
		super(i, etm);
	}
	
	@Override
	public boolean a(Block block) {
		return block.id == Block.OBSIDIAN.id ? this.b.d() == 3 : (block.id != Block.DIAMOND_BLOCK.id && block.id != Block.DIAMOND_ORE.id ? (block.id != Block.GOLD_BLOCK.id && block.id != Block.GOLD_ORE.id ? (block.id != Block.IRON_BLOCK.id && block.id != Block.IRON_ORE.id ? (block.id != Block.LAPIS_BLOCK.id && block.id != Block.LAPIS_ORE.id ? (block.id != Block.REDSTONE_ORE.id && block.id != Block.GLOWING_REDSTONE_ORE.id ? (block.material == Material.STONE ? true : block.material == Material.ORE) : this.b.d() >= 2) : this.b.d() >= 1) : this.b.d() >= 1) : this.b.d() >= 2) : this.b.d() >= 2);
	}
	
	/**
	 * Fixes a bug in nms where Notch compares references to ores instead of ore ids
	 */
	public static void replacePickaxes() {
		for (int i = 0; i < Item.byId.length; i++) {
			if (Item.byId[i] != null) {
				if (Item.byId[i] instanceof ItemPickaxe) {
					ItemPickaxe pickaxe = (ItemPickaxe)Item.byId[i];
					EnumToolMaterial etm = null;
					try {
						Field tool = ItemTool.class.getDeclaredField("b");
						tool.setAccessible(true);
						etm = (EnumToolMaterial) tool.get(pickaxe);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					Item.byId[i] = null;
					Item.byId[i] = new CustomItemPickaxe(pickaxe.id-256, etm);
				}
			}
		}
	}
}
