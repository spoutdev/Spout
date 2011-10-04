package org.getspout.spout.inventory;

import java.util.HashMap;

import net.minecraft.server.CraftingManager;

import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.inventory.SpoutShapedRecipe;
import org.getspout.spoutapi.material.Material;

public class SimpleSpoutShapedRecipe extends SpoutShapedRecipe implements SpoutRecipe {
	public SimpleSpoutShapedRecipe(ItemStack result) {
		super(result);
	}

	public static SimpleSpoutShapedRecipe fromSpoutRecipe(SpoutShapedRecipe recipe) {
		if (recipe instanceof SimpleSpoutShapedRecipe) {
			return (SimpleSpoutShapedRecipe) recipe;
		}
		SimpleSpoutShapedRecipe ret = new SimpleSpoutShapedRecipe(recipe.getResult());
		String[] shape = recipe.getShape();
		ret.shape(shape);
		for (char c : recipe.getIngredientMap().keySet()) {
			ret.setIngredient(c, recipe.getIngredientMap().get(c));
		}
		return ret;
	}

	public void addToCraftingManager() {
		Object[] data;
		String[] shape = this.getShape();
		HashMap<Character, Material> ingred = this.getIngredientMap();
		int datalen = shape.length;
		datalen += ingred.size() * 2;
		int i = 0;
		data = new Object[datalen];
		for (; i < shape.length; i++) {
			data[i] = shape[i];
		}
		for (char c : ingred.keySet()) {
			data[i] = c;
			i++;
			Material mdata = ingred.get(c);
			
			int id = mdata.getRawId();
			int dmg = mdata.getRawData();
			
			data[i] = new net.minecraft.server.ItemStack(id, 1, dmg);
			i++;
		}
		int id = this.getResult().getTypeId();
		int amount = this.getResult().getAmount();
		short durability = this.getResult().getDurability();
		CraftingManager.getInstance().registerShapedRecipe(new net.minecraft.server.ItemStack(id, amount, durability), data);
	}
}
