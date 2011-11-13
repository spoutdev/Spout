package org.getspout.spout.inventory;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.design.BlockDesign;
import org.getspout.spoutapi.inventory.ItemManager;
import org.getspout.spoutapi.inventory.MaterialManager;
import org.getspout.spoutapi.material.Block;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.CustomItem;
import org.getspout.spoutapi.material.MaterialData;

@SuppressWarnings("deprecation")
public class SimpleItemManager implements ItemManager {
	MaterialManager mm;
	
	public SimpleItemManager() {
		mm = SpoutManager.getMaterialManager();
	}

	@Override
	public String getStepSound(int id, short data) {
		return mm.getStepSound(MaterialData.getBlock(id, data));
	}

	@Override
	public void setStepSound(int id, short data, String url) {
		mm.setStepSound(MaterialData.getBlock(id, data), url);
	}

	@Override
	public void resetStepSound(int id, short data) {
		mm.resetStepSound(MaterialData.getBlock(id, data));
	}

	@Override
	public float getFriction(int id, short data) {
		return mm.getFriction(MaterialData.getBlock(id, data));
	}

	@Override
	public void setFriction(int id, short data, float friction) {
		mm.setFriction(MaterialData.getBlock(id, data), friction);
	}

	@Override
	public void resetFriction(int id, short data) {
		mm.resetFriction(MaterialData.getBlock(id, data));
	}

	@Override
	public float getHardness(int id, short data) {
		return mm.getHardness(MaterialData.getBlock(id, data));
	}

	@Override
	public void setHardness(int id, short data, float hardness) {
		mm.setHardness(MaterialData.getBlock(id, data), hardness);
	}

	@Override
	public void resetHardness(int id, short data) {
		mm.resetHardness(MaterialData.getBlock(id, data));
	}

	@Override
	public boolean isOpaque(int id, short data) {
		return mm.isOpaque(MaterialData.getBlock(id, data));
	}

	@Override
	public void setOpaque(int id, short data, boolean opacity) {
		mm.setOpaque(MaterialData.getBlock(id, data), opacity);
	}

	@Override
	public void resetOpacity(int id, short data) {
		mm.resetOpacity(MaterialData.getBlock(id, data));
	}

	@Override
	public int getLightLevel(int id, short data) {
		return mm.getLightLevel(MaterialData.getBlock(id, data));
	}

	@Override
	public void setLightLevel(int id, short data, int level) {
		mm.setLightLevel(MaterialData.getBlock(id, data), level);
	}

	@Override
	public void resetLightLevel(int id, short data) {
		mm.resetLightLevel(MaterialData.getBlock(id, data));
	}

	@Override
	public Set<Block> getModifiedBlocks() {
		return mm.getModifiedBlocks();
	}

	@Override
	public String getItemName(Material item) {
		return getItemName(item, (short) 0);
	}

	@Override
	public String getItemName(Material item, short data) {
		return getItemName(item.getId(), data);
	}

	@Override
	public String getItemName(int item, short data) {
		return MaterialData.getMaterial(item, data).getName();
	}

	@Override
	public String getCustomItemName(Material item) {
		return getItemName(item, (short) 0);
	}

	@Override
	public String getCustomItemName(Material item, short data) {
		return getItemName(item, data);
	}

	@Override
	public void setItemName(Material item, String name) {
		setItemName(item.getId(), (short) 0, name);
	}

	@Override
	public void setItemName(Material item, short data, String name) {
		setItemName(item.getId(), data, name);
	}

	@Override
	public void setItemName(int item, short data, String name) {
		mm.setItemName(MaterialData.getMaterial(item, data), name);
	}

	@Override
	public void setItemName(CustomItem item, String name) {
		mm.setItemName(item, name);
	}

	@Override
	public void resetName(Material item) {
		resetName(item, (short) 0);
	}

	@Override
	public void resetName(Material item, short data) {
		mm.resetName(MaterialData.getMaterial(item.getId(), data));
	}

	@Override
	public void setItemTexture(Material item, String texture) {
		setItemTexture(item, (short) 0, texture);
	}

	@Override
	public void setItemTexture(Material item, Plugin plugin, String texture) {
		setItemTexture(item.getId(), (short) 0, plugin, texture);
	}

	@Override
	public void setItemTexture(Material item, short data, String texture) {
		setItemTexture(item.getId(), data, null, texture);
	}

	@Override
	public void setItemTexture(int id, short data, Plugin plugin, String texture) {
		mm.setItemTexture(MaterialData.getMaterial(id, data), plugin, texture);
	}

	@Override
	public void setItemTexture(CustomItem item, Plugin plugin, String texture) {
		mm.setItemTexture(item, plugin, texture);
	}

	@Override
	public String getCustomItemTexture(Material item) {
		return getCustomItemTexture(item, (short) 0);
	}

	@Override
	public String getCustomItemTexturePlugin(Material item) {
		return getCustomItemTexturePlugin(item, (short) 0);
	}

	@Override
	public String getCustomItemTexture(Material item, short data) {
		return mm.getCustomItemTexture(MaterialData.getMaterial(item.getId(), data));
	}

	@Override
	public String getCustomItemTexturePlugin(Material item, short data) {
		return mm.getCustomItemTexturePlugin(MaterialData.getMaterial(item.getId(), data));
	}

	@Override
	public void resetTexture(Material item) {
		resetTexture(item, (short) 0);
	}

	@Override
	public void resetTexture(Material item, short data) {
		mm.resetTexture(MaterialData.getMaterial(item.getId(), data));
	}

	@Override
	public void reset() {
		mm.reset();
	}

	@Override
	public int registerCustomItemName(Plugin plugin, String key) {
		return mm.registerCustomItemName(plugin, key);
	}

	@Override
	public int getCustomItemId(Plugin plugin, String key) {
		return mm.registerCustomItemName(plugin, key);
	}

	@Override
	public void setCustomItemBlock(CustomItem item, CustomBlock block) {
		mm.setCustomItemBlock(item, block);
	}

	@Override
	public ItemStack getCustomItemStack(CustomBlock block, int size) {
		return mm.getCustomItemStack(block, size);
	}

	@Override
	public ItemStack getCustomItemStack(CustomItem item, int size) {
		return mm.getCustomItemStack(item, size);
	}

	@Override
	public boolean overrideBlock(org.bukkit.block.Block block, CustomBlock customBlock) {
		return mm.overrideBlock(block, customBlock);
	}

	@Override
	public boolean overrideBlock(World world, int x, int y, int z, CustomBlock customBlock) {
		return mm.overrideBlock(world, x, y, z, customBlock);
	}

	@Override
	public void setCustomBlockDesign(int blockId, short metaData, BlockDesign design) {
		mm.setCustomBlockDesign(MaterialData.getMaterial(blockId, metaData), design);
	}

	@Override
	public boolean isCustomBlock(org.bukkit.block.Block block) {
		return mm.isCustomBlock(block);
	}

	@Override
	public SpoutBlock getSpoutBlock(org.bukkit.block.Block block) {
		return mm.getSpoutBlock(block);
	}

	@Override
	public boolean registerSpoutRecipe(Recipe recipe) {
		return mm.registerSpoutRecipe(recipe);
	}

	@Override
	public boolean isCustomItem(ItemStack item) {
		return mm.isCustomItem(item);
	}

	@Override
	public CustomItem getCustomItem(ItemStack item) {
		return mm.getCustomItem(item);
	}

	@Override
	public CustomBlock registerItemDrop(CustomBlock block, ItemStack item) {
		return mm.registerItemDrop(block, item);
	}

	@Override
	public boolean hasItemDrop(CustomBlock block) {
		return mm.hasItemDrop(block);
	}

	@Override
	public ItemStack getItemDrop(CustomBlock block) {
		return mm.getItemDrop(block);
	}
}
