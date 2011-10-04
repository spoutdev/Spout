package org.getspout.spout.inventory;

import java.util.ArrayList;

import net.minecraft.server.CraftingManager;

import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.inventory.SpoutShapelessRecipe;
import org.getspout.spoutapi.material.Material;

public class SimpleSpoutShapelessRecipe extends SpoutShapelessRecipe implements SpoutRecipe {
	
    public SimpleSpoutShapelessRecipe(ItemStack result) {
        super(result);
    }

    public static SimpleSpoutShapelessRecipe fromSpoutRecipe(SpoutShapelessRecipe recipe) {
        if (recipe instanceof SimpleSpoutShapelessRecipe) {
            return (SimpleSpoutShapelessRecipe) recipe;
        }
        SimpleSpoutShapelessRecipe ret = new SimpleSpoutShapelessRecipe(recipe.getResult());
        for (Material ingred : recipe.getIngredientList()) {
            ret.addIngredient(ingred);
        }
        return ret;
    }

    public void addToCraftingManager() {
        ArrayList<Material> ingred = this.getIngredientList();
        Object[] data = new Object[ingred.size()];
        int i = 0;
        for (Material mdata : ingred) {
            int id = mdata.getRawId();
            int dmg = mdata.getRawData();
            data[i] = new net.minecraft.server.ItemStack(id, 1, dmg);
            i++;
        }
        int id = this.getResult().getTypeId();
        int amount = this.getResult().getAmount();
        short durability = this.getResult().getDurability();
        CraftingManager.getInstance().registerShapelessRecipe(new net.minecraft.server.ItemStack(id, amount, durability), data);
    }
}
