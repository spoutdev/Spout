package org.getspout.spout.item.mcitem;

import java.lang.reflect.Field;

import net.minecraft.server.Block;
import net.minecraft.server.EnumToolMaterial;
import net.minecraft.server.Item;
import net.minecraft.server.ItemSpade;
import net.minecraft.server.ItemTool;

public class CustomItemSpade extends ItemSpade{

	public CustomItemSpade(int i, EnumToolMaterial etm) {
		super(i, etm);
	}
	
	@Override
	public boolean a(Block paramBlock) {
		if (paramBlock.id == Block.SNOW.id) return true;
		return paramBlock.id == Block.SNOW_BLOCK.id;
	}
	
	/**
	 * Fixes a bug in nms where Notch compares reference to snow and snow blocks instead of id's for the snow and snow block
	 */
	public static void replaceSpades() {
		for (int i = 0; i < Item.byId.length; i++) {
			if (Item.byId[i] != null) {
				if (Item.byId[i] instanceof ItemSpade) {
					ItemSpade spade = (ItemSpade)Item.byId[i];
					EnumToolMaterial etm = null;
					try {
						Field tool = ItemTool.class.getDeclaredField("a");
						tool.setAccessible(true);
						etm = (EnumToolMaterial) tool.get(spade);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					Item.byId[i] = null;
					Item.byId[i] = new CustomItemSpade(spade.id-256, etm);
				}
			}
		}
	}
}
