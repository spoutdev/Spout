/*
 * This file is part of Spout (http://wiki.getspout.org/).
 * 
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.spout.inventory;

import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import net.minecraft.server.Item;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockVector;
import org.getspout.spout.Spout;
import org.getspout.spout.block.SpoutCraftBlock;
import org.getspout.spout.block.SpoutCraftChunk;
import org.getspout.spout.block.mcblock.CustomMCBlock;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.inventory.BlockDesign;
import org.getspout.spoutapi.inventory.ItemManager;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.packet.PacketCustomBlockDesign;
import org.getspout.spoutapi.packet.PacketCustomBlockOverride;
import org.getspout.spoutapi.packet.PacketCustomItem;
import org.getspout.spoutapi.packet.PacketItemName;
import org.getspout.spoutapi.packet.PacketItemTexture;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.util.UniqueItemStringMap;
import org.getspout.spoutapi.util.map.TIntPairFloatHashMap;
import org.getspout.spoutapi.util.map.TIntPairHashSet;
import org.getspout.spoutapi.util.map.TIntPairObjectHashMap;

public class SimpleItemManager implements ItemManager {
	private final TIntIntHashMap itemBlock = new TIntIntHashMap();
	private final TIntIntHashMap itemMetaData = new TIntIntHashMap();
	private final TIntObjectHashMap<String> itemPlugin = new TIntObjectHashMap<String>();

	private final TIntPairFloatHashMap originalHardness = new TIntPairFloatHashMap();
	private final TIntPairFloatHashMap originalFriction = new TIntPairFloatHashMap();
	private final TIntIntHashMap originalOpacity = new TIntIntHashMap();
	private final TIntIntHashMap originalLight = new TIntIntHashMap();

	private final TIntPairObjectHashMap<String> itemNames = new TIntPairObjectHashMap<String>(500);
	private final TIntPairObjectHashMap<String> customNames = new TIntPairObjectHashMap<String>(100);
	private final TIntPairObjectHashMap<String> customTextures = new TIntPairObjectHashMap<String>(100);
	private final TIntPairObjectHashMap<String> customTexturesPlugin = new TIntPairObjectHashMap<String>(100);

	private final TIntPairObjectHashMap<BlockDesign> customBlockDesigns = new TIntPairObjectHashMap<BlockDesign>(100);

	public final static String blockIdString = "org.spout.customblocks.blockid";
	public final static String metaDataString = "org.spout.customblocks.metadata";
	
	public SimpleItemManager() {
		itemNames.put(1, 0, "Stone");
		itemNames.put(2, 0, "Grass");
		itemNames.put(3, 0, "Dirt");
		itemNames.put(4, 0, "Cobblestone");
		itemNames.put(5, 0, "Wooden Planks");
		itemNames.put(6, 0, "Sapling");
		itemNames.put(6, 1, "Spruce Sapling");
		itemNames.put(6, 2, "Birch Sapling");
		itemNames.put(7, 0, "Bedrock");
		itemNames.put(8, 0, "Water");
		itemNames.put(9, 0, "Stationary Water");
		itemNames.put(10, 0, "Lava");
		itemNames.put(11, 0, "Stationary Lava");
		itemNames.put(12, 0, "Sand");
		itemNames.put(13, 0, "Gravel");
		itemNames.put(14, 0, "Gold Ore");
		itemNames.put(15, 0, "Iron Ore");
		itemNames.put(16, 0, "Coal Ore");
		itemNames.put(17, 0, "Wood");
		itemNames.put(18, 0, "Leaves");
		itemNames.put(19, 0, "Spone");
		itemNames.put(20, 0, "Glass");
		itemNames.put(21, 0, "Lapis Lazuli Ore");
		itemNames.put(22, 0, "Lapis Lazuli Block");
		itemNames.put(23, 0, "Dispenser");
		itemNames.put(24, 0, "SandStone");
		itemNames.put(25, 0, "Note Block");
		itemNames.put(26, 0, "Bed");
		itemNames.put(27, 0, "Powered Rail");
		itemNames.put(28, 0, "Detector Rail");
		itemNames.put(29, 0, "Sticky Piston");
		itemNames.put(30, 0, "Cobweb");
		itemNames.put(31, 0, "Dead Grass");
		itemNames.put(31, 1, "Tall Grass");
		itemNames.put(31, 2, "Fern");
		itemNames.put(32, 0, "Dead Shrubs");
		itemNames.put(33, 0, "Piston");
		itemNames.put(34, 0, "Piston (Head)");
		itemNames.put(35, 0, "Wool");
		itemNames.put(35, 1, "Orange Wool");
		itemNames.put(35, 2, "Magenta Wool");
		itemNames.put(35, 3, "Light Blue Wool");
		itemNames.put(35, 4, "Yellow Wool");
		itemNames.put(35, 5, "Light Green Wool");
		itemNames.put(35, 6, "Pink Wool");
		itemNames.put(35, 7, "Gray Wool");
		itemNames.put(35, 8, "Light Gray Wool");
		itemNames.put(35, 9, "Cyan Wool");
		itemNames.put(35, 10, "Purple Wool");
		itemNames.put(35, 11, "Blue Wool");
		itemNames.put(35, 12, "Brown Wool");
		itemNames.put(35, 13, "Dark Green Wool");
		itemNames.put(35, 14, "Red Wool");
		itemNames.put(35, 15, "Black Wool");
		itemNames.put(37, 0, "Dandelion");
		itemNames.put(38, 0, "Rose");
		itemNames.put(39, 0, "Brown Mushroom");
		itemNames.put(40, 0, "Red Mushroom");
		itemNames.put(41, 0, "Gold Block");
		itemNames.put(42, 0, "Iron Block");
		itemNames.put(43, 0, "Stone Double Slab");
		itemNames.put(43, 1, "Sandstone Double Slabs");
		itemNames.put(43, 2, "Wooden Double Slabs");
		itemNames.put(43, 3, "Stone Double Slabs");
		itemNames.put(43, 4, "Brick Double Slabs");
		itemNames.put(43, 5, "Stone Brick Double Slabs");
		itemNames.put(44, 0, "Stone Slab");
		itemNames.put(44, 1, "Sandstone Slab");
		itemNames.put(44, 2, "Wooden Slab");
		itemNames.put(44, 3, "Stone Slab");
		itemNames.put(44, 4, "Brick Slab");
		itemNames.put(44, 5, "Stone Brick Slab");
		itemNames.put(45, 0, "Brick Block");
		itemNames.put(46, 0, "TNT");
		itemNames.put(47, 0, "Bookshelf");
		itemNames.put(48, 0, "Moss Stone");
		itemNames.put(49, 0, "Obsidian");
		itemNames.put(50, 0, "Torch");
		itemNames.put(51, 0, "Fire");
		itemNames.put(52, 0, "Monster Spawner");
		itemNames.put(53, 0, "Wooden Stairs");
		itemNames.put(54, 0, "Chest");
		itemNames.put(55, 0, "Redstone Wire");
		itemNames.put(56, 0, "Diamond Ore");
		itemNames.put(57, 0, "Diamond Block");
		itemNames.put(58, 0, "Crafting Table");
		itemNames.put(59, 0, "Seeds");
		itemNames.put(60, 0, "Farmland");
		itemNames.put(61, 0, "Furnace");
		itemNames.put(62, 0, "Burning Furnace");
		itemNames.put(63, 0, "Sign Post");
		itemNames.put(64, 0, "Wooden Door");
		itemNames.put(65, 0, "Ladders");
		itemNames.put(66, 0, "Rails");
		itemNames.put(67, 0, "Cobblestone Stairs");
		itemNames.put(68, 0, "Wall Sign");
		itemNames.put(69, 0, "Lever");
		itemNames.put(70, 0, "Stone Pressure Plate");
		itemNames.put(71, 0, "Iron Door");
		itemNames.put(72, 0, "Wooden Pressure Plate");
		itemNames.put(73, 0, "Redstone Ore");
		itemNames.put(74, 0, "Glowing Redstone Ore");
		itemNames.put(75, 0, "Redstone Torch");
		itemNames.put(76, 0, "Redstone Torch (On)");
		itemNames.put(77, 0, "Stone Button");
		itemNames.put(78, 0, "Snow");
		itemNames.put(79, 0, "Ice");
		itemNames.put(80, 0, "Snow Block");
		itemNames.put(81, 0, "Cactus");
		itemNames.put(82, 0, "Clay Block");
		itemNames.put(83, 0, "Sugar Cane");
		itemNames.put(84, 0, "Jukebox");
		itemNames.put(85, 0, "Fence");
		itemNames.put(86, 0, "Pumpkin");
		itemNames.put(87, 0, "Netherrack");
		itemNames.put(88, 0, "Soul Sand");
		itemNames.put(89, 0, "Glowstone Block");
		itemNames.put(90, 0, "Portal");
		itemNames.put(91, 0, "Jack 'o' Lantern");
		itemNames.put(92, 0, "Cake Block");
		itemNames.put(93, 0, "Redstone Repeater");
		itemNames.put(94, 0, "Redstone Repeater (On)");
		itemNames.put(95, 0, "Locked Chest");
		itemNames.put(96, 0, "Trapdoor");
		itemNames.put(97, 0, "Silverfish Stone");
		itemNames.put(98, 0, "Stone Brick");
		itemNames.put(99, 0, "Huge Red Mushroom");
		itemNames.put(100, 0, "Huge Brown Mushroom");
		itemNames.put(101, 0, "Iron Bars");
		itemNames.put(102, 0, "Glass Pane");
		itemNames.put(103, 0, "Watermelon");
		itemNames.put(104, 0, "Pumpkin Stem");
		itemNames.put(105, 0, "Melon Stem");
		itemNames.put(106, 0, "Vines");
		itemNames.put(107, 0, "Fence Gate");
		itemNames.put(108, 0, "Brick Stairs");
		itemNames.put(109, 0, "Stone Brick Stairs");

		itemNames.put(256, 0, "Iron Shovel");
		itemNames.put(257, 0, "Iron Pickaxe");
		itemNames.put(258, 0, "Iron Axe");
		itemNames.put(259, 0, "Flint and Steel");
		itemNames.put(260, 0, "Apple");
		itemNames.put(261, 0, "Bow");
		itemNames.put(262, 0, "Arrow");
		itemNames.put(263, 0, "Coal");
		itemNames.put(263, 1, "Charcoal");
		itemNames.put(264, 0, "Diamond");
		itemNames.put(265, 0, "Iron Ingot");
		itemNames.put(266, 0, "Gold Ingot");
		itemNames.put(267, 0, "Iron Sword");
		itemNames.put(268, 0, "Wooden Sword");
		itemNames.put(269, 0, "Wooden Shovel");
		itemNames.put(270, 0, "Wooden Pickaxe");
		itemNames.put(271, 0, "Wooden Axe");
		itemNames.put(272, 0, "Stone Sword");
		itemNames.put(273, 0, "Stone Shovel");
		itemNames.put(274, 0, "Stone Pickaxe");
		itemNames.put(275, 0, "Stone Axe");
		itemNames.put(276, 0, "Diamond Sword");
		itemNames.put(277, 0, "Diamond Shovel");
		itemNames.put(278, 0, "Diamond Pickaxe");
		itemNames.put(279, 0, "Diamond Axe");
		itemNames.put(280, 0, "Stick");
		itemNames.put(281, 0, "Bowl");
		itemNames.put(282, 0, "Mushroom Soup");
		itemNames.put(283, 0, "Gold Sword");
		itemNames.put(284, 0, "Gold Shovel");
		itemNames.put(285, 0, "Gold Pickaxe");
		itemNames.put(286, 0, "Gold Axe");
		itemNames.put(287, 0, "String");
		itemNames.put(288, 0, "Feather");
		itemNames.put(289, 0, "Gunpowder");
		itemNames.put(290, 0, "Wooden Hoe");
		itemNames.put(291, 0, "Stone Hoe");
		itemNames.put(292, 0, "Iron Hoe");
		itemNames.put(293, 0, "Diamond Hoe");
		itemNames.put(294, 0, "Gold Hoe");
		itemNames.put(295, 0, "Seeds");
		itemNames.put(296, 0, "Wheat");
		itemNames.put(297, 0, "Bread");
		itemNames.put(298, 0, "Leather Cap");
		itemNames.put(299, 0, "Leather Tunic");
		itemNames.put(300, 0, "Leather Boots");
		itemNames.put(301, 0, "Leather Boots");
		itemNames.put(302, 0, "Chain Helmet");
		itemNames.put(303, 0, "Chain Chestplate");
		itemNames.put(304, 0, "Chain Leggings");
		itemNames.put(305, 0, "Chain Boots");
		itemNames.put(306, 0, "Iron Helmet");
		itemNames.put(307, 0, "Iron Chestplate");
		itemNames.put(308, 0, "Iron Leggings");
		itemNames.put(309, 0, "Iron Boots");
		itemNames.put(310, 0, "Diamond Helmet");
		itemNames.put(311, 0, "Diamond Chestplate");
		itemNames.put(312, 0, "Diamond Leggings");
		itemNames.put(313, 0, "Diamond Boots");
		itemNames.put(314, 0, "Gold Helmet");
		itemNames.put(315, 0, "Gold Chestplate");
		itemNames.put(316, 0, "Gold Leggings");
		itemNames.put(317, 0, "Gold Boots");
		itemNames.put(318, 0, "Flint");
		itemNames.put(319, 0, "Raw Porkchop");
		itemNames.put(320, 0, "Cooked Porkchop");
		itemNames.put(321, 0, "Paintings");
		itemNames.put(322, 0, "Golden Apple");
		itemNames.put(323, 0, "Sign");
		itemNames.put(324, 0, "Wooden Door");
		itemNames.put(325, 0, "Bucket");
		itemNames.put(326, 0, "Water Bucket");
		itemNames.put(327, 0, "Lava Bucket");
		itemNames.put(328, 0, "Minecart");
		itemNames.put(329, 0, "Saddle");
		itemNames.put(330, 0, "Iron Door");
		itemNames.put(331, 0, "Redstone");
		itemNames.put(332, 0, "Snowball");
		itemNames.put(333, 0, "Boat");
		itemNames.put(334, 0, "Leather");
		itemNames.put(335, 0, "Milk");
		itemNames.put(336, 0, "Brick");
		itemNames.put(337, 0, "Clay");
		itemNames.put(338, 0, "Sugar Canes");
		itemNames.put(339, 0, "Paper");
		itemNames.put(340, 0, "Book");
		itemNames.put(341, 0, "Slimeball");
		itemNames.put(342, 0, "Minecart with Chest");
		itemNames.put(343, 0, "Minecart with Furnace");
		itemNames.put(344, 0, "Egg");
		itemNames.put(345, 0, "Compass");
		itemNames.put(346, 0, "Fishing Rod");
		itemNames.put(347, 0, "Clock");
		itemNames.put(348, 0, "Glowstone Dust");
		itemNames.put(349, 0, "Raw Fish");
		itemNames.put(350, 0, "Cooked Fish");
		itemNames.put(351, 0, "Ink Sac");
		itemNames.put(351, 1, "Rose Red");
		itemNames.put(351, 2, "Cactus Green");
		itemNames.put(351, 3, "Cocoa Beans");
		itemNames.put(351, 4, "Lapis Lazuli");
		itemNames.put(351, 5, "Purple Dye");
		itemNames.put(351, 6, "Cyan Dye");
		itemNames.put(351, 7, "Light Gray Dye");
		itemNames.put(351, 8, "Gray Dye");
		itemNames.put(351, 9, "Pink Dye");
		itemNames.put(351, 10, "Lime Dye");
		itemNames.put(351, 11, "Dandelion Yellow");
		itemNames.put(351, 12, "Light Blue Dye");
		itemNames.put(351, 13, "Magenta Dye");
		itemNames.put(351, 14, "Orange Dye");
		itemNames.put(351, 15, "Bone Meal");
		itemNames.put(352, 0, "Bone");
		itemNames.put(353, 0, "Sugar");
		itemNames.put(354, 0, "Cake");
		itemNames.put(355, 0, "Bed");
		itemNames.put(356, 0, "Redstone Repeater");
		itemNames.put(357, 0, "Cookie");
		itemNames.put(358, 0, "Map");
		itemNames.put(359, 0, "Shears");
		itemNames.put(2256, 0, "Music Disc");
		itemNames.put(2257, 0, "Music Disc");
	}

	public static void disableStoneStackMix() {

		try {
			Method a = Item.class.getDeclaredMethod("a", new Class[] { boolean.class });
			a.setAccessible(true);
			a.invoke(Item.byId[318], Boolean.TRUE);

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	@Override
	public String getItemName(Material item) {
		return getItemName(item, (short) 0);
	}

	@Override
	public String getItemName(int item, short data) {
		return getItemName(Material.getMaterial(item), data);
	}

	@Override
	public String getItemName(Material item, short data) {
		if (customNames.containsKey(item.getId(), data)) {
			return (String) customNames.get(item.getId(), data);
		}
		return (String) itemNames.get(item.getId(), data);
	}

	@Override
	public void setItemName(Material item, String name) {
		setItemName(item, (short) 0, name);
	}

	@Override
	public void setItemName(int item, short data, String name) {
		setItemName(Material.getMaterial(item), data, name);
	}

	@Override
	public void setItemName(Material item, short data, String name) {
		customNames.put(item.getId(), data, name);
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player instanceof SpoutCraftPlayer) {
				if (((SpoutPlayer) player).isSpoutCraftEnabled()) {
					((SpoutPlayer) player).sendPacket(new PacketItemName(item.getId(), data, name));
				}
			}
		}
	}

	@Override
	public void resetName(Material item) {
		resetName(item, (byte) 0);
	}

	@Override
	public void resetName(Material item, short data) {
		if (customNames.containsKey(item.getId(), data)) {
			customNames.remove(item.getId(), data);
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player instanceof SpoutCraftPlayer) {
					if (((SpoutPlayer) player).isSpoutCraftEnabled()) {
						((SpoutPlayer) player).sendPacket(new PacketItemName(item.getId(), data, "[reset]"));
					}
				}
			}
		}
	}

	@Override
	public void reset() {
		customNames.clear();
		customTextures.clear();
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player instanceof SpoutCraftPlayer) {
				if (((SpoutPlayer) player).isSpoutCraftEnabled()) {
					((SpoutPlayer) player).sendPacket(new PacketItemName(0, (short) 0, "[resetall]"));
				}
			}
		}
	}

	@Override
	public String getCustomItemName(Material item) {
		return getCustomItemName(item, (short) 0);
	}

	@Override
	public String getCustomItemName(Material item, short data) {
		if (customNames.containsKey(item.getId(), data)) {
			return (String) customNames.get(item.getId(), data);
		}
		return null;
	}

	public void onPlayerJoin(SpoutPlayer player) {
		if (((SpoutPlayer) player).isSpoutCraftEnabled()) {
			for (TLongObjectIterator<String> it = customNames.iterator(); it.hasNext();) {
				it.advance();
				((SpoutPlayer) player).sendPacket(new PacketItemName(TIntPairHashSet.longToKey1(it.key()), (short) TIntPairHashSet.longToKey2(it.key()), it.value()));
			}
			for (TLongObjectIterator<String> it = customTextures.iterator(); it.hasNext();) {
				it.advance();
				String pluginName = (String) customTexturesPlugin.get(TIntPairHashSet.longToKey1(it.key()), (short) TIntPairHashSet.longToKey2(it.key()));
				((SpoutPlayer) player).sendPacket(new PacketItemTexture(TIntPairHashSet.longToKey1(it.key()), (short) TIntPairHashSet.longToKey2(it.key()), pluginName, it.value()));
			}
		}
	}

	@Override
	public void setItemTexture(Material item, String texture) {
		setItemTexture(item, (short) 0, null, texture);
	}

	@Override
	public void setItemTexture(Material item, Plugin plugin, String texture) {
		setItemTexture(item, (short) 0, plugin, texture);
	}

	public void setItemTexture(Material item, short data, String texture) {
		setItemTexture(item, data, null, texture);
	}

	@Override
	public void setItemTexture(Material item, short data, Plugin plugin, String texture) {
		String pluginName;
		if (plugin == null) {
			pluginName = null;
		} else {
			pluginName = plugin.getDescription().getName();
		}
		customTextures.put(item.getId(), data, texture);
		if (pluginName == null) {
			customTexturesPlugin.remove(item.getId(), data);
		} else {
			customTexturesPlugin.put(item.getId(), data, pluginName);
		}
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player instanceof SpoutCraftPlayer) {
				if (((SpoutPlayer) player).isSpoutCraftEnabled()) {
					((SpoutPlayer) player).sendPacket(new PacketItemTexture(item.getId(), data, pluginName, texture));
				}
			}
		}
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
		if (customTextures.containsKey(item.getId(), data)) {
			return (String) customTextures.get(item.getId(), data);
		}
		return null;
	}

	public String getCustomItemTexturePlugin(Material item, short data) {
		if (customTexturesPlugin.containsKey(item.getId(), data)) {
			return (String) customTexturesPlugin.get(item.getId(), data);
		}
		return null;
	}

	@Override
	public void resetTexture(Material item) {
		resetTexture(item, (short) 0);
	}

	@Override
	public void resetTexture(Material item, short data) {
		if (customTextures.containsKey(item.getId(), data)) {
			customTextures.remove(item.getId(), data);
			String pluginName = (String) customTexturesPlugin.remove(item.getId(), data);
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player instanceof SpoutCraftPlayer) {
					if (((SpoutPlayer) player).isSpoutCraftEnabled()) {
						((SpoutPlayer) player).sendPacket(new PacketItemTexture(item.getId(), data, pluginName, "[reset]"));
					}
				}
			}
		}
	}

	public int getItemBlock(int damage) {
		return itemBlock.get(damage);
	}

	public short getItemMetaData(int damage) {
		return (short) itemMetaData.get(damage);
	}

	public int registerCustomItemName(Plugin plugin, String key) {
		int id = UniqueItemStringMap.getId(key);

		itemPlugin.put(id, plugin.getDescription().getName());

		return id;
	}

	public int getCustomItemId(Plugin plugin, String key) {
		return registerCustomItemName(plugin, key);
	}

	@Override
	public void setCustomItemBlock(int id, int blockId, short metaData) {
		if (blockId != 0 || metaData == 0) {
			itemBlock.put(id, blockId);
			itemMetaData.put(id, metaData);
		} else {
			itemBlock.remove(id);
			itemMetaData.remove(id);
		}
		updateCustomClientData(id);
	}

	public void updateCustomClientData(Player player) {
		Set<Integer> ids = UniqueItemStringMap.getIds();
		Player[] players = new Player[1];
		players[0] = player;
		for (Integer id : ids) {
			updateCustomClientData(players, id);
		}
	}

	private void updateCustomClientData(int id) {
		Player[] players = Spout.getInstance().getServer().getOnlinePlayers();
		updateCustomClientData(players, id);
	}

	private void updateCustomClientData(Player[] players, int id) {

		int blockId = itemBlock.get(id);

		short metaData = (short) itemMetaData.get(id);

		@SuppressWarnings("unused")
		String pluginName = (String) itemPlugin.get(id);

		PacketCustomItem p = new PacketCustomItem(id, blockId, metaData);

		for (Player player : players) {
			if (player instanceof SpoutCraftPlayer) {
				SpoutCraftPlayer sp = (SpoutCraftPlayer) player;
				if (sp.isSpoutCraftEnabled()) {
					sp.sendPacket(p);
				}
			}
		}
	}

	public ItemStack getCustomItemStack(CustomBlock block, int size) {
		return new ItemStack(318, size, (short) block.getCustomId());
	}

	public boolean overrideBlock(Block block, Integer blockId, Integer metaData) {

		if (!(block instanceof SpoutCraftBlock)) {
			return false;
		}

		SpoutCraftBlock scb = (SpoutCraftBlock) block;

		if (blockId == null || metaData == null) {
			scb.removeCustomBlockData();
		} else {
			scb.setCustomBlockId(blockId);
			scb.setCustomMetaData(metaData);
		}

		Player[] players = block.getWorld().getPlayers().toArray(new Player[0]);

		sendBlockOverrideToPlayers(players, block, blockId, metaData);

		return true;
	}

	@Override
	public boolean overrideBlock(Block block, CustomBlock customBlock) {
		block.setTypeId(customBlock.getRawId());
		return overrideBlock(block, customBlock.getCustomId(), customBlock.getCustomMetaData());
	}

	@Override
	public boolean overrideBlock(World world, int x, int y, int z, CustomBlock customBlock) {
		int blockId = customBlock.getCustomId();
		int metaData = customBlock.getCustomMetaData();

		SpoutManager.getChunkDataManager().setBlockData(blockIdString, world, x, y, z, blockId);
		SpoutManager.getChunkDataManager().setBlockData(metaDataString, world, x, y, z, metaData);

		Player[] players = world.getPlayers().toArray(new Player[0]);

		sendBlockOverrideToPlayers(players, new BlockVector(x, y, z), blockId, metaData);

		return true;
	}

	public void sendBlockOverrideToPlayers(Player[] players, World world) {

		Chunk[] chunks = world.getLoadedChunks();

		for (Chunk chunk : chunks) {
			sendBlockOverrideToPlayers(players, chunk);
		}

	}

	private SpoutCraftChunk getSpoutCraftChunk(Chunk chunk) {
		if (!(chunk instanceof SpoutCraftChunk)) {
			return null;
		} else {
			return (SpoutCraftChunk) chunk;
		}
	}

	private SpoutCraftBlock getSpoutCraftBlock(Block block) {
		if (!(block instanceof SpoutCraftBlock)) {
			return null;
		} else {
			return (SpoutCraftBlock) block;
		}
	}

	private BlockVector correctBlockVector(BlockVector vector, Chunk chunk) {

		vector.setX(vector.getBlockX() & 0xF + (chunk.getX() << 4));
		vector.setZ(vector.getBlockZ() & 0xF + (chunk.getZ() << 4));
		vector.setY(vector.getBlockY() & 0xFF);
		return vector;

	}

	public boolean sendBlockOverrideToPlayers(Player[] players, Chunk chunk) {

		SpoutCraftChunk scc = getSpoutCraftChunk(chunk);
		if (scc == null) {
			return false;
		}

		BlockVector[] blocks = scc.getTaggedBlocks();

		if (blocks == null) {
			return false;
		}

		boolean success = true;
		for (BlockVector block : blocks) {
			correctBlockVector(block, scc);

			SpoutCraftBlock scb = getSpoutCraftBlock(chunk.getWorld().getBlockAt(block.getBlockX(), block.getBlockY(), block.getBlockZ()));

			if (scb == null) {
				success = false;
				continue;
			}

			Integer blockId = scb.getCustomBlockId();
			Integer metaData = scb.getCustomMetaData();

			if (blockId != null && metaData != null) {
				sendBlockOverrideToPlayers(players, block, blockId, metaData);
			}
		}

		return success;

	}

	public void sendBlockOverrideToPlayers(Player[] players, Block block, Integer blockId, Integer metaData) {
		Location blockLocation = block.getLocation();
		BlockVector blockVector = new BlockVector(blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getBlockZ());
		sendBlockOverrideToPlayers(players, blockVector, blockId, metaData);
	}

	public void sendBlockOverrideToPlayers(Player[] players, BlockVector blockVector, Integer blockId, Integer metaData) {

		PacketCustomBlockOverride p = new PacketCustomBlockOverride(blockVector.getBlockX(), blockVector.getBlockY(), blockVector.getBlockZ(), blockId, metaData);

		for (Player player : players) {
			if (player instanceof SpoutCraftPlayer) {
				SpoutCraftPlayer sp = (SpoutCraftPlayer) player;
				if (sp.isSpoutCraftEnabled()) {
					sp.sendPacket(p);
				}
			}
		}
	}

	public void setCustomBlockDesign(int blockId, short metaData, BlockDesign design) {
		Player[] players = Spout.getInstance().getServer().getOnlinePlayers();

		if (design != null) {
			customBlockDesigns.put(blockId, metaData, design);
		} else {
			customBlockDesigns.remove(blockId, metaData);
		}

		updateCustomBlockDesigns(players, blockId, metaData, design);

	}

	public void updateAllCustomBlockDesigns(Player player) {
		Player[] players = new Player[1];
		players[0] = player;
		updateAllCustomBlockDesigns(players);
	}

	public void updateAllCustomBlockDesigns(Player[] players) {
		for (TLongObjectIterator<BlockDesign> it = customBlockDesigns.iterator(); it.hasNext();) {
			it.advance();
			updateCustomBlockDesigns(players, TIntPairHashSet.longToKey1(it.key()), (short) TIntPairHashSet.longToKey2(it.key()), (BlockDesign) it.value());
		}
	}

	private void updateCustomBlockDesigns(Player[] players, int blockId, int metaData, BlockDesign design) {

		PacketCustomBlockDesign p = new PacketCustomBlockDesign(blockId, metaData, design);

		for (Player player : players) {
			if (player instanceof SpoutCraftPlayer) {
				SpoutCraftPlayer sp = (SpoutCraftPlayer) player;
				if (sp.isSpoutCraftEnabled()) {
					sp.sendPacket(p);
				}
			}
		}
	}

	@Override
	public String getStepSound(int id, short data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStepSound(int id, short data, String url) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetStepSound(int id, short data) {
		// TODO Auto-generated method stub

	}

	@Override
	public float getFriction(int id, short data) {
		return net.minecraft.server.Block.byId[id].frictionFactor;
	}

	@Override
	public void setFriction(int id, short data, float friction) {
		if (!originalFriction.containsKey(id, data)) {
			originalFriction.put(id, data, getFriction(id, data));
		}
		net.minecraft.server.Block.byId[id].frictionFactor = friction;
	}

	@Override
	public void resetFriction(int id, short data) {
		if (originalFriction.containsKey(id, data)) {
			setFriction(id, data, originalFriction.get(id, data));
		}
	}

	@Override
	public float getHardness(int id, short data) {
		return net.minecraft.server.Block.byId[id].j();
	}

	@Override
	public void setHardness(int id, short data, float hardness) {
		if (!originalHardness.containsKey(id, data)) {
			originalHardness.put(id, data, getHardness(id, data));
		}
		net.minecraft.server.Block b = net.minecraft.server.Block.byId[id];
		if (b instanceof CustomMCBlock) {
			((CustomMCBlock) b).setHardness(hardness);
		}
	}

	@Override
	public void resetHardness(int id, short data) {
		if (originalHardness.containsKey(id, data)) {
			setHardness(id, data, originalHardness.get(id, data));
		}
	}

	@Override
	public boolean isOpaque(int id, short data) {
		return net.minecraft.server.Block.o[id];
	}

	@Override
	public void setOpaque(int id, short data, boolean opacity) {
		if (!originalOpacity.containsKey(id)) {
			originalOpacity.put(id, isOpaque(id, data) ? 1 : 0);
		}
		net.minecraft.server.Block.o[id] = opacity;
	}

	@Override
	public void resetOpacity(int id, short data) {
		if (originalOpacity.containsKey(id)) {
			setOpaque(id, data, originalOpacity.get(id) != 0);
		}
	}

	@Override
	public int getLightLevel(int id, short data) {
		return net.minecraft.server.Block.s[id];
	}

	@Override
	public void setLightLevel(int id, short data, int level) {
		if (!originalLight.containsKey(id)) {
			originalLight.put(id, getLightLevel(id, data));
		}
		net.minecraft.server.Block.s[id] = level;
	}

	@Override
	public void resetLightLevel(int id, short data) {
		if (originalLight.containsKey(id)) {
			setLightLevel(id, data, originalLight.get(id));
		}
	}

	@Override
	public boolean isCustomBlock(Block block) {

		if (!(block instanceof SpoutCraftBlock)) {
			return false;
		}

		boolean outcome = false;

		SpoutCraftBlock scb = (SpoutCraftBlock) block;

		Integer blockId = scb.getCustomBlockId();
		Integer metaData = scb.getCustomMetaData();

		if (blockId != null && metaData != null) {
			outcome = true;
		}

		return outcome;
	}

	@Override
	public SpoutBlock getSpoutBlock(Block block) {
		if (block instanceof SpoutBlock) {
			return (SpoutBlock) block;
		} else {
			return null;
		}
	}
}
