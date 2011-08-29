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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.inventory.ItemManager;
import org.getspout.spoutapi.packet.PacketItemName;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.util.UniqueItemStringMap;

public class SimpleItemManager implements ItemManager{
	private final HashMap<Integer, Integer> itemBlock = new HashMap<Integer,Integer>();
	private final HashMap<ItemData, String> itemNames;
	private final HashMap<ItemData, String> customNames;
	public SimpleItemManager() {
		itemNames = new HashMap<ItemData, String>(500);
		customNames = new HashMap<ItemData, String>(100);
		itemNames.put(new ItemData(1), "Stone");
		itemNames.put(new ItemData(2), "Grass");
		itemNames.put(new ItemData(3), "Dirt");
		itemNames.put(new ItemData(4), "Cobblestone");
		itemNames.put(new ItemData(5), "Wooden Planks");
		itemNames.put(new ItemData(6, 0), "Sapling");
		itemNames.put(new ItemData(6, 1), "Spruce Sapling");
		itemNames.put(new ItemData(6, 2), "Birch Sapling");
		itemNames.put(new ItemData(7), "Bedrock");
		itemNames.put(new ItemData(8), "Water");
		itemNames.put(new ItemData(9), "Stationary Water");
		itemNames.put(new ItemData(10), "Lava");
		itemNames.put(new ItemData(11), "Stationary Lava");
		itemNames.put(new ItemData(12), "Sand");
		itemNames.put(new ItemData(13), "Gravel");
		itemNames.put(new ItemData(14), "Gold Ore");
		itemNames.put(new ItemData(15), "Iron Ore");
		itemNames.put(new ItemData(16), "Coal Ore");
		itemNames.put(new ItemData(17), "Wood");
		itemNames.put(new ItemData(18), "Leaves");
		itemNames.put(new ItemData(19), "Spone");
		itemNames.put(new ItemData(20), "Glass");
		itemNames.put(new ItemData(21), "Lapis Lazuli Ore");
		itemNames.put(new ItemData(22), "Lapis Lazuli Block");
		itemNames.put(new ItemData(23), "Dispenser");
		itemNames.put(new ItemData(24), "SandStone");
		itemNames.put(new ItemData(25), "Note Block");
		itemNames.put(new ItemData(26), "Bed");
		itemNames.put(new ItemData(27), "Powered Rail");
		itemNames.put(new ItemData(28), "Detector Rail");
		itemNames.put(new ItemData(29), "Sticky Piston");
		itemNames.put(new ItemData(30), "Cobweb");
		itemNames.put(new ItemData(31), "Tall Grass");
		itemNames.put(new ItemData(32), "Dead Shrubs");
		itemNames.put(new ItemData(33), "Piston");
		itemNames.put(new ItemData(34), "Piston (Head)");
		itemNames.put(new ItemData(35, 0), "Wool");
		itemNames.put(new ItemData(35, 1), "Orange Wool");
		itemNames.put(new ItemData(35, 2), "Magenta Wool");
		itemNames.put(new ItemData(35, 3), "Light Blue Wool");
		itemNames.put(new ItemData(35, 4), "Yellow Wool");
		itemNames.put(new ItemData(35, 5), "Light Green Wool");
		itemNames.put(new ItemData(35, 6), "Pink Wool");
		itemNames.put(new ItemData(35, 7), "Gray Wool");
		itemNames.put(new ItemData(35, 8), "Light Gray Wool");
		itemNames.put(new ItemData(35, 9), "Cyan Wool");
		itemNames.put(new ItemData(35, 10), "Purple Wool");
		itemNames.put(new ItemData(35, 11), "Blue Wool");
		itemNames.put(new ItemData(35, 12), "Brown Wool");
		itemNames.put(new ItemData(35, 13), "Dark Green Wool");
		itemNames.put(new ItemData(35, 14), "Red Wool");
		itemNames.put(new ItemData(35, 15), "Black Wool");
		itemNames.put(new ItemData(37), "Dandelion");
		itemNames.put(new ItemData(38), "Rose");
		itemNames.put(new ItemData(39), "Brown Mushroom");
		itemNames.put(new ItemData(40), "Red Mushroom");
		itemNames.put(new ItemData(41), "Gold Block");
		itemNames.put(new ItemData(42), "Iron Block");
		itemNames.put(new ItemData(43, 0), "Stone Double Slab");
		itemNames.put(new ItemData(43, 1), "Sandstone Double Slabs");
		itemNames.put(new ItemData(43, 2), "Wooden Double Slab");
		itemNames.put(new ItemData(43, 3), "Stone Double Slab");
		itemNames.put(new ItemData(44, 0), "Stone Slab");
		itemNames.put(new ItemData(44, 1), "Sandstone Slab");
		itemNames.put(new ItemData(44, 2), "Wooden Slab");
		itemNames.put(new ItemData(44, 3), "Stone Slab");
		itemNames.put(new ItemData(45), "Brick Block");
		itemNames.put(new ItemData(46), "TNT");
		itemNames.put(new ItemData(47), "Bookshelf");
		itemNames.put(new ItemData(48), "Moss Stone");
		itemNames.put(new ItemData(49), "Obsidian");
		itemNames.put(new ItemData(50), "Torch");
		itemNames.put(new ItemData(51), "Fire");
		itemNames.put(new ItemData(52), "Monster Spawner");
		itemNames.put(new ItemData(53), "Wooden Stairs");
		itemNames.put(new ItemData(54), "Chest");
		itemNames.put(new ItemData(55), "Redstone Wire");
		itemNames.put(new ItemData(56), "Diamond Ore");
		itemNames.put(new ItemData(57), "Diamond Block");
		itemNames.put(new ItemData(58), "Crafting Table");
		itemNames.put(new ItemData(59), "Seeds");
		itemNames.put(new ItemData(60), "Farmland");
		itemNames.put(new ItemData(61), "Furnace");
		itemNames.put(new ItemData(62), "Burning Furnace");
		itemNames.put(new ItemData(63), "Sign Post");
		itemNames.put(new ItemData(64), "Wooden Door");
		itemNames.put(new ItemData(65), "Ladders");
		itemNames.put(new ItemData(66), "Rails");
		itemNames.put(new ItemData(67), "Cobblestone Stairs");
		itemNames.put(new ItemData(68), "Wall Sign");
		itemNames.put(new ItemData(69), "Lever");
		itemNames.put(new ItemData(70), "Stone Pressure Plate");
		itemNames.put(new ItemData(71), "Iron Door");
		itemNames.put(new ItemData(72), "Wooden Pressure Plate");
		itemNames.put(new ItemData(73), "Redstone Ore");
		itemNames.put(new ItemData(74), "Glowing Redstone Ore");
		itemNames.put(new ItemData(75), "Redstone Torch");
		itemNames.put(new ItemData(76), "Redstone Torch (On)");
		itemNames.put(new ItemData(77), "Stone Button");
		itemNames.put(new ItemData(78), "Snow");
		itemNames.put(new ItemData(79), "Ice");
		itemNames.put(new ItemData(80), "Snow Block");
		itemNames.put(new ItemData(81), "Cactus");
		itemNames.put(new ItemData(82), "Clay Block");
		itemNames.put(new ItemData(83), "Sugar Cane");
		itemNames.put(new ItemData(84), "Jukebox");
		itemNames.put(new ItemData(85), "Fence");
		itemNames.put(new ItemData(86), "Pumpkin");
		itemNames.put(new ItemData(87), "Netherrack");
		itemNames.put(new ItemData(88), "Soul Sand");
		itemNames.put(new ItemData(89), "Glowstone Block");
		itemNames.put(new ItemData(90), "Portal");
		itemNames.put(new ItemData(91), "Jack 'o' Lantern");
		itemNames.put(new ItemData(92), "Cake Block");
		itemNames.put(new ItemData(93), "Redstone Repeater");
		itemNames.put(new ItemData(94), "Redstone Repeater (On)");
		itemNames.put(new ItemData(95), "Locked Chest");
		itemNames.put(new ItemData(96), "Trapdoor");
		itemNames.put(new ItemData(256), "Iron Shovel");
		itemNames.put(new ItemData(257), "Iron Pickaxe");
		itemNames.put(new ItemData(258), "Iron Axe");
		itemNames.put(new ItemData(259), "Flint and Steel");
		itemNames.put(new ItemData(260), "Apple");
		itemNames.put(new ItemData(261), "Bow");
		itemNames.put(new ItemData(262), "Arrow");
		itemNames.put(new ItemData(263, 0), "Coal");
		itemNames.put(new ItemData(263, 1), "Charcoal");
		itemNames.put(new ItemData(264), "Diamond");
		itemNames.put(new ItemData(265), "Iron Ingot");
		itemNames.put(new ItemData(266), "Gold Ingot");
		itemNames.put(new ItemData(267), "Iron Sword");
		itemNames.put(new ItemData(268), "Wooden Sword");
		itemNames.put(new ItemData(269), "Wooden Shovel");
		itemNames.put(new ItemData(270), "Wooden Pickaxe");
		itemNames.put(new ItemData(271), "Wooden Axe");
		itemNames.put(new ItemData(272), "Stone Sword");
		itemNames.put(new ItemData(273), "Stone Shovel");
		itemNames.put(new ItemData(274), "Stone Pickaxe");
		itemNames.put(new ItemData(275), "Stone Axe");
		itemNames.put(new ItemData(276), "Diamond Sword");
		itemNames.put(new ItemData(277), "Diamond Shovel");
		itemNames.put(new ItemData(278), "Diamond Pickaxe");
		itemNames.put(new ItemData(279), "Diamond Axe");
		itemNames.put(new ItemData(280), "Stick");
		itemNames.put(new ItemData(281), "Bowl");
		itemNames.put(new ItemData(282), "Mushroom Soup");
		itemNames.put(new ItemData(283), "Gold Sword");
		itemNames.put(new ItemData(284), "Gold Shovel");
		itemNames.put(new ItemData(285), "Gold Pickaxe");
		itemNames.put(new ItemData(286), "Gold Axe");
		itemNames.put(new ItemData(287), "String");
		itemNames.put(new ItemData(288), "Feather");
		itemNames.put(new ItemData(289), "Gunpowder");
		itemNames.put(new ItemData(290), "Wooden Hoe");
		itemNames.put(new ItemData(291), "Stone Hoe");
		itemNames.put(new ItemData(292), "Iron Hoe");
		itemNames.put(new ItemData(293), "Diamond Hoe");
		itemNames.put(new ItemData(294), "Gold Hoe");
		itemNames.put(new ItemData(295), "Seeds");
		itemNames.put(new ItemData(296), "Wheat");
		itemNames.put(new ItemData(297), "Bread");
		itemNames.put(new ItemData(298), "Leather Cap");
		itemNames.put(new ItemData(299), "Leather Tunic");
		itemNames.put(new ItemData(300), "Leather Boots");
		itemNames.put(new ItemData(301), "Leather Boots");
		itemNames.put(new ItemData(302), "Chain Helmet");
		itemNames.put(new ItemData(303), "Chain Chestplate");
		itemNames.put(new ItemData(304), "Chain Leggings");
		itemNames.put(new ItemData(305), "Chain Boots");
		itemNames.put(new ItemData(306), "Iron Helmet");
		itemNames.put(new ItemData(307), "Iron Chestplate");
		itemNames.put(new ItemData(308), "Iron Leggings");
		itemNames.put(new ItemData(309), "Iron Boots");
		itemNames.put(new ItemData(310), "Diamond Helmet");
		itemNames.put(new ItemData(311), "Diamond Chestplate");
		itemNames.put(new ItemData(312), "Diamond Leggings");
		itemNames.put(new ItemData(313), "Diamond Boots");
		itemNames.put(new ItemData(314), "Gold Helmet");
		itemNames.put(new ItemData(315), "Gold Chestplate");
		itemNames.put(new ItemData(316), "Gold Leggings");
		itemNames.put(new ItemData(317), "Gold Boots");
		itemNames.put(new ItemData(318), "Flint");
		itemNames.put(new ItemData(319), "Raw Porkchop");
		itemNames.put(new ItemData(320), "Cooked Porkchop");
		itemNames.put(new ItemData(321), "Paintings");
		itemNames.put(new ItemData(322), "Golden Apple");
		itemNames.put(new ItemData(323), "Sign");
		itemNames.put(new ItemData(324), "Wooden Door");
		itemNames.put(new ItemData(325), "Bucket");
		itemNames.put(new ItemData(326), "Water Bucket");
		itemNames.put(new ItemData(327), "Lava Bucket");
		itemNames.put(new ItemData(328), "Minecart");
		itemNames.put(new ItemData(329), "Saddle");
		itemNames.put(new ItemData(330), "Iron Door");
		itemNames.put(new ItemData(331), "Redstone");
		itemNames.put(new ItemData(332), "Snowball");
		itemNames.put(new ItemData(333), "Boat");
		itemNames.put(new ItemData(334), "Leather");
		itemNames.put(new ItemData(335), "Milk");
		itemNames.put(new ItemData(336), "Brick");
		itemNames.put(new ItemData(337), "Clay");
		itemNames.put(new ItemData(338), "Sugar Canes");
		itemNames.put(new ItemData(339), "Paper");
		itemNames.put(new ItemData(340), "Book");
		itemNames.put(new ItemData(341), "Slimeball");
		itemNames.put(new ItemData(342), "Minecart with Chest");
		itemNames.put(new ItemData(343), "Minecart with Furnace");
		itemNames.put(new ItemData(344), "Egg");
		itemNames.put(new ItemData(345), "Compass");
		itemNames.put(new ItemData(346), "Fishing Rod");
		itemNames.put(new ItemData(347), "Clock");
		itemNames.put(new ItemData(348), "Glowstone Dust");
		itemNames.put(new ItemData(349), "Raw Fish");
		itemNames.put(new ItemData(350), "Cooked Fish");
		itemNames.put(new ItemData(351, 0), "Ink Sac");
		itemNames.put(new ItemData(351, 1), "Rose Red");
		itemNames.put(new ItemData(351, 2), "Cactus Green");
		itemNames.put(new ItemData(351, 3), "Cocoa Beans");
		itemNames.put(new ItemData(351, 4), "Lapis Lazuli");
		itemNames.put(new ItemData(351, 5), "Purple Dye");
		itemNames.put(new ItemData(351, 6), "Cyan Dye");
		itemNames.put(new ItemData(351, 7), "Light Gray Dye");
		itemNames.put(new ItemData(351, 8), "Gray Dye");
		itemNames.put(new ItemData(351, 9), "Pink Dye");
		itemNames.put(new ItemData(351, 10), "Lime Dye");
		itemNames.put(new ItemData(351, 11), "Dandelion Yellow");
		itemNames.put(new ItemData(351, 12), "Light Blue Dye");
		itemNames.put(new ItemData(351, 13), "Magenta Dye");
		itemNames.put(new ItemData(351, 14), "Orange Dye");
		itemNames.put(new ItemData(351, 15), "Bone Meal");
		itemNames.put(new ItemData(352), "Bone");
		itemNames.put(new ItemData(353), "Sugar");
		itemNames.put(new ItemData(354), "Cake");
		itemNames.put(new ItemData(355), "Bed");
		itemNames.put(new ItemData(356), "Redstone Repeater");
		itemNames.put(new ItemData(357), "Cookie");
		itemNames.put(new ItemData(358), "Map");
		itemNames.put(new ItemData(359), "Shears");
		itemNames.put(new ItemData(2256), "Music Disc");
		itemNames.put(new ItemData(2257), "Music Disc");
	}

	@Override
	public String getItemName(Material item) {
		return getItemName(item, (short)0);
	}

	@Override
	public String getItemName(Material item, short data) {
		ItemData info = new ItemData(item.getId(), data);
		if (customNames.containsKey(info)) {
			return customNames.get(info);
		}
		return itemNames.get(info);
	}

	@Override
	public void setItemName(Material item, String name) {
		setItemName(item, (short)0, name);
	}

	@Override
	public void setItemName(Material item, short data, String name) {
		customNames.put(new ItemData(item.getId(), data), name);
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player instanceof SpoutCraftPlayer){
				if (((SpoutPlayer)player).isSpoutCraftEnabled()) {
					((SpoutPlayer)player).sendPacket(new PacketItemName(item.getId(), (short) 0, name));
				}
			}
		}
	}
	
	public void setItemName(int id, String name) {
		setItemName(Material.STONE, (short)id, name);
	}
	
	@Override
	public void resetName(Material item) {
		resetName(item,(byte) 0);
	}

	@Override
	public void resetName(Material item, short data) {
		ItemData info = new ItemData(item.getId(), data);
		if (customNames.containsKey(info)) {
			customNames.remove(info);
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player instanceof SpoutCraftPlayer){
					if (((SpoutPlayer)player).isSpoutCraftEnabled()) {
						((SpoutPlayer)player).sendPacket(new PacketItemName(info.id, info.data, "[reset]"));
					}
				}
			}
		}
	}

	@Override
	public void reset() {
		customNames.clear();
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player instanceof SpoutCraftPlayer){
				if (((SpoutPlayer)player).isSpoutCraftEnabled()) {
					((SpoutPlayer)player).sendPacket(new PacketItemName(0, (short) 0, "[resetall]"));
				}
			}
		}
	}

	@Override
	public String getCustomItemName(Material item) {
		return getCustomItemName(item, (short)0);
	}
	
	public String getCustomItemName(int id) {
		return getCustomItemName(Material.STONE, (short)id);
	}

	@Override
	public String getCustomItemName(Material item, short data) {
		ItemData info = new ItemData(item.getId(), data);
		if (customNames.containsKey(info)) {
			return customNames.get(info);
		}
		return null;
	}
	
	public void onPlayerJoin(SpoutPlayer player) {
		if (((SpoutPlayer)player).isSpoutCraftEnabled()) {
			Iterator<Entry<ItemData, String>> i = customNames.entrySet().iterator();
			while (i.hasNext()) {
				Entry<ItemData, String> e = i.next();
				((SpoutPlayer)player).sendPacket(new PacketItemName(e.getKey().id, e.getKey().data, e.getValue()));
			}
		}
	}
	
	public Integer getItemBlock(int damage) {
		return itemBlock.get(damage);
	}
	
	public Integer registerCustomItemName(String key) {
		return UniqueItemStringMap.getId(key);
	}
	
	public void setCustomItemBlock(int id, Integer blockId) {
		if (blockId != null) {
			itemBlock.put(id, blockId);
		} else {
			itemBlock.remove(id);
		}
	}

	public ItemStack getCustomItemStack(int id, int size) {
		return new ItemStack(1, size, (short)id);
	}
}
