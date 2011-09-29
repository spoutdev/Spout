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

import gnu.trove.TIntIntHashMap;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TLongFloatHashMap;
import gnu.trove.TLongObjectHashMap;
import gnu.trove.TLongObjectIterator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import net.minecraft.server.Item;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockVector;
import org.getspout.spout.Spout;
import org.getspout.spout.block.SpoutCraftBlock;
import org.getspout.spout.block.SpoutCraftChunk;
import org.getspout.spout.block.mcblock.CustomMCBlock;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.inventory.ItemManager;
import org.getspout.spoutapi.material.block.GenericCustomBlock;
import org.getspout.spoutapi.packet.PacketCustomBlockDesign;
import org.getspout.spoutapi.packet.PacketCustomBlockOverride;
import org.getspout.spoutapi.packet.PacketCustomItem;
import org.getspout.spoutapi.packet.PacketItemName;
import org.getspout.spoutapi.packet.PacketItemTexture;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.util.UniqueItemStringMap;

public class SimpleItemManager implements ItemManager{
	private final TIntIntHashMap itemBlock = new TIntIntHashMap();
    private final TIntIntHashMap itemMetaData = new TIntIntHashMap();
	private final TIntObjectHashMap itemPlugin = new TIntObjectHashMap();
	
	private final TLongFloatHashMap originalHardness = new TLongFloatHashMap();
	private final TLongFloatHashMap originalFriction = new TLongFloatHashMap();
	private final TIntIntHashMap originalOpacity = new TIntIntHashMap();
	private final TIntIntHashMap originalLight = new TIntIntHashMap();
	
	private final TLongObjectHashMap itemNames = new TLongObjectHashMap(500);
	private final TLongObjectHashMap customNames = new TLongObjectHashMap(100);
	private final TLongObjectHashMap customTextures = new TLongObjectHashMap(100);
	private final TLongObjectHashMap customTexturesPlugin = new TLongObjectHashMap(100);
	
	private final TLongObjectHashMap customBlockDesigns = new TLongObjectHashMap(100);
	
	public final static String blockIdString = "org.spout.customblocks.blockid";
	public final static String metaDataString = "org.spout.customblocks.metadata";

	public SimpleItemManager() {
		itemNames.put(toLong(1), "Stone");
		itemNames.put(toLong(2), "Grass");
		itemNames.put(toLong(3), "Dirt");
		itemNames.put(toLong(4), "Cobblestone");
		itemNames.put(toLong(5), "Wooden Planks");
		itemNames.put(toLong(6, 0), "Sapling");
		itemNames.put(toLong(6, 1), "Spruce Sapling");
		itemNames.put(toLong(6, 2), "Birch Sapling");
		itemNames.put(toLong(7), "Bedrock");
		itemNames.put(toLong(8), "Water");
		itemNames.put(toLong(9), "Stationary Water");
		itemNames.put(toLong(10), "Lava");
		itemNames.put(toLong(11), "Stationary Lava");
		itemNames.put(toLong(12), "Sand");
		itemNames.put(toLong(13), "Gravel");
		itemNames.put(toLong(14), "Gold Ore");
		itemNames.put(toLong(15), "Iron Ore");
		itemNames.put(toLong(16), "Coal Ore");
		itemNames.put(toLong(17), "Wood");
		itemNames.put(toLong(18), "Leaves");
		itemNames.put(toLong(19), "Spone");
		itemNames.put(toLong(20), "Glass");
		itemNames.put(toLong(21), "Lapis Lazuli Ore");
		itemNames.put(toLong(22), "Lapis Lazuli Block");
		itemNames.put(toLong(23), "Dispenser");
		itemNames.put(toLong(24), "SandStone");
		itemNames.put(toLong(25), "Note Block");
		itemNames.put(toLong(26), "Bed");
		itemNames.put(toLong(27), "Powered Rail");
		itemNames.put(toLong(28), "Detector Rail");
		itemNames.put(toLong(29), "Sticky Piston");
		itemNames.put(toLong(30), "Cobweb");
		itemNames.put(toLong(31, 0), "Dead Grass");
		itemNames.put(toLong(31, 1), "Tall Grass");
		itemNames.put(toLong(31, 2), "Fern");
		itemNames.put(toLong(32), "Dead Shrubs");
		itemNames.put(toLong(33), "Piston");
		itemNames.put(toLong(34), "Piston (Head)");
		itemNames.put(toLong(35, 0), "Wool");
		itemNames.put(toLong(35, 1), "Orange Wool");
		itemNames.put(toLong(35, 2), "Magenta Wool");
		itemNames.put(toLong(35, 3), "Light Blue Wool");
		itemNames.put(toLong(35, 4), "Yellow Wool");
		itemNames.put(toLong(35, 5), "Light Green Wool");
		itemNames.put(toLong(35, 6), "Pink Wool");
		itemNames.put(toLong(35, 7), "Gray Wool");
		itemNames.put(toLong(35, 8), "Light Gray Wool");
		itemNames.put(toLong(35, 9), "Cyan Wool");
		itemNames.put(toLong(35, 10), "Purple Wool");
		itemNames.put(toLong(35, 11), "Blue Wool");
		itemNames.put(toLong(35, 12), "Brown Wool");
		itemNames.put(toLong(35, 13), "Dark Green Wool");
		itemNames.put(toLong(35, 14), "Red Wool");
		itemNames.put(toLong(35, 15), "Black Wool");
		itemNames.put(toLong(37), "Dandelion");
		itemNames.put(toLong(38), "Rose");
		itemNames.put(toLong(39), "Brown Mushroom");
		itemNames.put(toLong(40), "Red Mushroom");
		itemNames.put(toLong(41), "Gold Block");
		itemNames.put(toLong(42), "Iron Block");
		itemNames.put(toLong(43, 0), "Stone Double Slab");
		itemNames.put(toLong(43, 1), "Sandstone Double Slabs");
		itemNames.put(toLong(43, 2), "Wooden Double Slabs");
		itemNames.put(toLong(43, 3), "Stone Double Slabs");
		itemNames.put(toLong(43, 4), "Brick Double Slabs");
		itemNames.put(toLong(43, 5), "Stone Brick Double Slabs");
		itemNames.put(toLong(44, 0), "Stone Slab");
		itemNames.put(toLong(44, 1), "Sandstone Slab");
		itemNames.put(toLong(44, 2), "Wooden Slab");
		itemNames.put(toLong(44, 3), "Stone Slab");
		itemNames.put(toLong(44, 4), "Brick Slab");
		itemNames.put(toLong(44, 5), "Stone Brick Slab");
		itemNames.put(toLong(45), "Brick Block");
		itemNames.put(toLong(46), "TNT");
		itemNames.put(toLong(47), "Bookshelf");
		itemNames.put(toLong(48), "Moss Stone");
		itemNames.put(toLong(49), "Obsidian");
		itemNames.put(toLong(50), "Torch");
		itemNames.put(toLong(51), "Fire");
		itemNames.put(toLong(52), "Monster Spawner");
		itemNames.put(toLong(53), "Wooden Stairs");
		itemNames.put(toLong(54), "Chest");
		itemNames.put(toLong(55), "Redstone Wire");
		itemNames.put(toLong(56), "Diamond Ore");
		itemNames.put(toLong(57), "Diamond Block");
		itemNames.put(toLong(58), "Crafting Table");
		itemNames.put(toLong(59), "Seeds");
		itemNames.put(toLong(60), "Farmland");
		itemNames.put(toLong(61), "Furnace");
		itemNames.put(toLong(62), "Burning Furnace");
		itemNames.put(toLong(63), "Sign Post");
		itemNames.put(toLong(64), "Wooden Door");
		itemNames.put(toLong(65), "Ladders");
		itemNames.put(toLong(66), "Rails");
		itemNames.put(toLong(67), "Cobblestone Stairs");
		itemNames.put(toLong(68), "Wall Sign");
		itemNames.put(toLong(69), "Lever");
		itemNames.put(toLong(70), "Stone Pressure Plate");
		itemNames.put(toLong(71), "Iron Door");
		itemNames.put(toLong(72), "Wooden Pressure Plate");
		itemNames.put(toLong(73), "Redstone Ore");
		itemNames.put(toLong(74), "Glowing Redstone Ore");
		itemNames.put(toLong(75), "Redstone Torch");
		itemNames.put(toLong(76), "Redstone Torch (On)");
		itemNames.put(toLong(77), "Stone Button");
		itemNames.put(toLong(78), "Snow");
		itemNames.put(toLong(79), "Ice");
		itemNames.put(toLong(80), "Snow Block");
		itemNames.put(toLong(81), "Cactus");
		itemNames.put(toLong(82), "Clay Block");
		itemNames.put(toLong(83), "Sugar Cane");
		itemNames.put(toLong(84), "Jukebox");
		itemNames.put(toLong(85), "Fence");
		itemNames.put(toLong(86), "Pumpkin");
		itemNames.put(toLong(87), "Netherrack");
		itemNames.put(toLong(88), "Soul Sand");
		itemNames.put(toLong(89), "Glowstone Block");
		itemNames.put(toLong(90), "Portal");
		itemNames.put(toLong(91), "Jack 'o' Lantern");
		itemNames.put(toLong(92), "Cake Block");
		itemNames.put(toLong(93), "Redstone Repeater");
		itemNames.put(toLong(94), "Redstone Repeater (On)");
		itemNames.put(toLong(95), "Locked Chest");
		itemNames.put(toLong(96), "Trapdoor");
		itemNames.put(toLong(97), "Silverfish Stone");
		itemNames.put(toLong(98), "Stone Brick");
		itemNames.put(toLong(99), "Huge Red Mushroom");
		itemNames.put(toLong(100), "Huge Brown Mushroom");
		itemNames.put(toLong(101), "Iron Bars");
		itemNames.put(toLong(102), "Glass Pane");
		itemNames.put(toLong(103), "Watermelon");
		itemNames.put(toLong(104), "Pumpkin Stem");
		itemNames.put(toLong(105), "Melon Stem");
		itemNames.put(toLong(106), "Vines");
		itemNames.put(toLong(107), "Fence Gate");
		itemNames.put(toLong(108), "Brick Stairs");
		itemNames.put(toLong(109), "Stone Brick Stairs");
		
		itemNames.put(toLong(256), "Iron Shovel");
		itemNames.put(toLong(257), "Iron Pickaxe");
		itemNames.put(toLong(258), "Iron Axe");
		itemNames.put(toLong(259), "Flint and Steel");
		itemNames.put(toLong(260), "Apple");
		itemNames.put(toLong(261), "Bow");
		itemNames.put(toLong(262), "Arrow");
		itemNames.put(toLong(263, 0), "Coal");
		itemNames.put(toLong(263, 1), "Charcoal");
		itemNames.put(toLong(264), "Diamond");
		itemNames.put(toLong(265), "Iron Ingot");
		itemNames.put(toLong(266), "Gold Ingot");
		itemNames.put(toLong(267), "Iron Sword");
		itemNames.put(toLong(268), "Wooden Sword");
		itemNames.put(toLong(269), "Wooden Shovel");
		itemNames.put(toLong(270), "Wooden Pickaxe");
		itemNames.put(toLong(271), "Wooden Axe");
		itemNames.put(toLong(272), "Stone Sword");
		itemNames.put(toLong(273), "Stone Shovel");
		itemNames.put(toLong(274), "Stone Pickaxe");
		itemNames.put(toLong(275), "Stone Axe");
		itemNames.put(toLong(276), "Diamond Sword");
		itemNames.put(toLong(277), "Diamond Shovel");
		itemNames.put(toLong(278), "Diamond Pickaxe");
		itemNames.put(toLong(279), "Diamond Axe");
		itemNames.put(toLong(280), "Stick");
		itemNames.put(toLong(281), "Bowl");
		itemNames.put(toLong(282), "Mushroom Soup");
		itemNames.put(toLong(283), "Gold Sword");
		itemNames.put(toLong(284), "Gold Shovel");
		itemNames.put(toLong(285), "Gold Pickaxe");
		itemNames.put(toLong(286), "Gold Axe");
		itemNames.put(toLong(287), "String");
		itemNames.put(toLong(288), "Feather");
		itemNames.put(toLong(289), "Gunpowder");
		itemNames.put(toLong(290), "Wooden Hoe");
		itemNames.put(toLong(291), "Stone Hoe");
		itemNames.put(toLong(292), "Iron Hoe");
		itemNames.put(toLong(293), "Diamond Hoe");
		itemNames.put(toLong(294), "Gold Hoe");
		itemNames.put(toLong(295), "Seeds");
		itemNames.put(toLong(296), "Wheat");
		itemNames.put(toLong(297), "Bread");
		itemNames.put(toLong(298), "Leather Cap");
		itemNames.put(toLong(299), "Leather Tunic");
		itemNames.put(toLong(300), "Leather Boots");
		itemNames.put(toLong(301), "Leather Boots");
		itemNames.put(toLong(302), "Chain Helmet");
		itemNames.put(toLong(303), "Chain Chestplate");
		itemNames.put(toLong(304), "Chain Leggings");
		itemNames.put(toLong(305), "Chain Boots");
		itemNames.put(toLong(306), "Iron Helmet");
		itemNames.put(toLong(307), "Iron Chestplate");
		itemNames.put(toLong(308), "Iron Leggings");
		itemNames.put(toLong(309), "Iron Boots");
		itemNames.put(toLong(310), "Diamond Helmet");
		itemNames.put(toLong(311), "Diamond Chestplate");
		itemNames.put(toLong(312), "Diamond Leggings");
		itemNames.put(toLong(313), "Diamond Boots");
		itemNames.put(toLong(314), "Gold Helmet");
		itemNames.put(toLong(315), "Gold Chestplate");
		itemNames.put(toLong(316), "Gold Leggings");
		itemNames.put(toLong(317), "Gold Boots");
		itemNames.put(toLong(318), "Flint");
		itemNames.put(toLong(319), "Raw Porkchop");
		itemNames.put(toLong(320), "Cooked Porkchop");
		itemNames.put(toLong(321), "Paintings");
		itemNames.put(toLong(322), "Golden Apple");
		itemNames.put(toLong(323), "Sign");
		itemNames.put(toLong(324), "Wooden Door");
		itemNames.put(toLong(325), "Bucket");
		itemNames.put(toLong(326), "Water Bucket");
		itemNames.put(toLong(327), "Lava Bucket");
		itemNames.put(toLong(328), "Minecart");
		itemNames.put(toLong(329), "Saddle");
		itemNames.put(toLong(330), "Iron Door");
		itemNames.put(toLong(331), "Redstone");
		itemNames.put(toLong(332), "Snowball");
		itemNames.put(toLong(333), "Boat");
		itemNames.put(toLong(334), "Leather");
		itemNames.put(toLong(335), "Milk");
		itemNames.put(toLong(336), "Brick");
		itemNames.put(toLong(337), "Clay");
		itemNames.put(toLong(338), "Sugar Canes");
		itemNames.put(toLong(339), "Paper");
		itemNames.put(toLong(340), "Book");
		itemNames.put(toLong(341), "Slimeball");
		itemNames.put(toLong(342), "Minecart with Chest");
		itemNames.put(toLong(343), "Minecart with Furnace");
		itemNames.put(toLong(344), "Egg");
		itemNames.put(toLong(345), "Compass");
		itemNames.put(toLong(346), "Fishing Rod");
		itemNames.put(toLong(347), "Clock");
		itemNames.put(toLong(348), "Glowstone Dust");
		itemNames.put(toLong(349), "Raw Fish");
		itemNames.put(toLong(350), "Cooked Fish");
		itemNames.put(toLong(351, 0), "Ink Sac");
		itemNames.put(toLong(351, 1), "Rose Red");
		itemNames.put(toLong(351, 2), "Cactus Green");
		itemNames.put(toLong(351, 3), "Cocoa Beans");
		itemNames.put(toLong(351, 4), "Lapis Lazuli");
		itemNames.put(toLong(351, 5), "Purple Dye");
		itemNames.put(toLong(351, 6), "Cyan Dye");
		itemNames.put(toLong(351, 7), "Light Gray Dye");
		itemNames.put(toLong(351, 8), "Gray Dye");
		itemNames.put(toLong(351, 9), "Pink Dye");
		itemNames.put(toLong(351, 10), "Lime Dye");
		itemNames.put(toLong(351, 11), "Dandelion Yellow");
		itemNames.put(toLong(351, 12), "Light Blue Dye");
		itemNames.put(toLong(351, 13), "Magenta Dye");
		itemNames.put(toLong(351, 14), "Orange Dye");
		itemNames.put(toLong(351, 15), "Bone Meal");
		itemNames.put(toLong(352), "Bone");
		itemNames.put(toLong(353), "Sugar");
		itemNames.put(toLong(354), "Cake");
		itemNames.put(toLong(355), "Bed");
		itemNames.put(toLong(356), "Redstone Repeater");
		itemNames.put(toLong(357), "Cookie");
		itemNames.put(toLong(358), "Map");
		itemNames.put(toLong(359), "Shears");
		itemNames.put(toLong(2256), "Music Disc");
		itemNames.put(toLong(2257), "Music Disc");
	}
	
	private static long toLong(int msw) {
		return toLong(msw, 0);
	}
	
	private static long toLong(int msw, int lsw) {
		return ((long) msw << 32) + lsw - Integer.MIN_VALUE;
	}
	
	private static int msw(long l) {
		return (int) (l >> 32);
	}

	private static int lsw(long l) {
		return (int) (l & 0xFFFFFFFF) + Integer.MIN_VALUE;
	}
	
	public static void disableStoneStackMix() {

			Method a;
			try {
				a = Item.class.getDeclaredMethod("a", new Class[] {boolean.class});
				a.setAccessible(true);
				a.invoke(Item.byId[1], new Object[] {Boolean.TRUE});
			} catch (SecurityException e) {
				e.printStackTrace();
				return;
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				return;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return;
			}
	}

	@Override
	public String getItemName(Material item) {
		return getItemName(item, (short)0);
	}
	
	@Override
	public String getItemName(int item, short data) {
		return getItemName(Material.getMaterial(item), data);
	}
	
	@Override
	public String getItemName(Material item, short data) {
		long key = toLong(item.getId(), data);
		if (customNames.containsKey(key)) {
			return (String) customNames.get(key);
		}
		return (String) itemNames.get(key);
	}

	@Override
	public void setItemName(Material item, String name) {
		setItemName(item, (short)0, name);
	}
	
	@Override
	public void setItemName(int item, short data, String name) {
		setItemName(Material.getMaterial(item), data, name);
	}

	@Override
	public void setItemName(Material item, short data, String name) {
		customNames.put(toLong(item.getId(), data), name);
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player instanceof SpoutCraftPlayer){
				if (((SpoutPlayer)player).isSpoutCraftEnabled()) {
					((SpoutPlayer)player).sendPacket(new PacketItemName(item.getId(), data, name));
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
		long key = toLong(item.getId(), data);
		if (customNames.containsKey(key)) {
			customNames.remove(key);
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player instanceof SpoutCraftPlayer){
					if (((SpoutPlayer)player).isSpoutCraftEnabled()) {
						((SpoutPlayer)player).sendPacket(new PacketItemName(msw(key), (short) lsw(key), "[reset]"));
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
		long key = toLong(item.getId(), data);
		if (customNames.containsKey(key)) {
			return (String) customNames.get(key);
		}
		return null;
	}

	public void onPlayerJoin(SpoutPlayer player) {
		if (((SpoutPlayer) player).isSpoutCraftEnabled()) {
			for (TLongObjectIterator it = customNames.iterator(); it.hasNext();) {
				it.advance();
				((SpoutPlayer) player).sendPacket(new PacketItemName(msw(it.key()), (short) lsw(it.key()), (String)it.value()));
			}
			for (TLongObjectIterator it = customTextures.iterator(); it.hasNext();) {
				it.advance();
				String pluginName = (String) customTexturesPlugin.get(it.key());
				((SpoutPlayer) player).sendPacket(new PacketItemTexture(msw(it.key()), (short) lsw(it.key()), pluginName, (String)it.value()));
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
		long newKey = toLong(item.getId(), data);
		customTextures.put(newKey, texture);
		if (pluginName == null) {
			customTexturesPlugin.remove(newKey);
		} else {
			customTexturesPlugin.put(newKey, pluginName);
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
	public void setItemTexture(int id, String texture) {
		setItemTexture(Material.STONE, (short)id, texture);
	}
	
	@Override
	public void setItemTexture(int id, Plugin plugin, String texture) {
		setItemTexture(Material.STONE, (short)id, plugin, texture);
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
		long info = toLong(item.getId(), data);
		if (customTextures.containsKey(info)) {
			return (String) customTextures.get(info);
		}
		return null;
	}
	
	public String getCustomItemTexturePlugin(Material item, short data) {
		long info = toLong(item.getId(), data);
		if (customTexturesPlugin.containsKey(info)) {
			return (String) customTexturesPlugin.get(info);
		}
		return null;
	}
	
	@Override
	public String getCustomItemTexture(int id) {
		return getCustomItemTexture(Material.STONE, (short)id);
	}
	
	@Override 
	public String getCustomItemTexturePlugin(int id) {
		return getCustomItemTexturePlugin(Material.STONE, (short)id);
	}

	@Override
	public void resetTexture(Material item) {
		resetTexture(item, (short) 0);
	}

	@Override
	public void resetTexture(Material item, short data) {
		long info = toLong(item.getId(), data);
		if (customTextures.containsKey(info)) {
			customTextures.remove(info);
			String pluginName = (String) customTexturesPlugin.remove(info);
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player instanceof SpoutCraftPlayer) {
					if (((SpoutPlayer) player).isSpoutCraftEnabled()) {
						((SpoutPlayer) player).sendPacket(new PacketItemTexture(msw(info), (short) lsw(info), pluginName, "[reset]"));
					}
				}
			}
		}
	}
	
	@Override
	public void resetTexture(int id) {
		resetTexture(Material.STONE, (short)id);
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
	
	public void setCustomItemBlock(int id, Integer blockId, Short metaData) {
		if (blockId != null || metaData == null) {
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
				SpoutCraftPlayer sp = (SpoutCraftPlayer)player;
				if (sp.isSpoutCraftEnabled()) {
					sp.sendPacket(p);
				}
			}
		}
	}
	
	public ItemStack getCustomItemStack(int id, int size) {
		return new ItemStack(1, size, (short)id);
	}
	
	public boolean overrideBlock(Block block, Integer blockId, Integer metaData) {
		
		if (!(block instanceof SpoutCraftBlock)) {
			return false;
		}
		
		SpoutCraftBlock scb = (SpoutCraftBlock)block;

		if (blockId == null || metaData == null) {
			scb.removeData(blockIdString);
			scb.removeData(metaDataString);
		} else {
			scb.setData(blockIdString, blockId);
			scb.setData(metaDataString, metaData);
		}
		
		Player[] players = block.getWorld().getPlayers().toArray(new Player[0]);
		
		sendBlockOverrideToPlayers(players, block, blockId, metaData);
		
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
			return (SpoutCraftChunk)chunk;
		}
	}
	
	private SpoutCraftBlock getSpoutCraftBlock(Block block) {
		if (!(block instanceof SpoutCraftBlock)) {
			return null;
		} else {
			return (SpoutCraftBlock)block;
		}
	}
	
	private BlockVector correctBlockVector(BlockVector vector, Chunk chunk) {
		
		vector.setX(vector.getBlockX() & 0xF + (chunk.getX()<<4));
		vector.setZ(vector.getBlockZ() & 0xF + (chunk.getZ()<<4));
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
			
			Integer blockId = (Integer)scb.getData(blockIdString);
			Integer metaData = (Integer)scb.getData(metaDataString);
			
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
				SpoutCraftPlayer sp = (SpoutCraftPlayer)player;
				if (sp.isSpoutCraftEnabled()) {
					sp.sendPacket(p);
				}
			}
		}
	}
	
	@Override
	public void setCustomBlockDesign(Integer blockId, Integer metaData, GenericCustomBlock design) {
		Player[] players = Spout.getInstance().getServer().getOnlinePlayers();
		
		long info = toLong(blockId, metaData);
		
		if (design != null) {
			customBlockDesigns.put(info, design);
		} else {
			customBlockDesigns.remove(info);
		}
		
		updateCustomBlockDesigns(players, info, design);

	}
	
	public void updateAllCustomBlockDesigns(Player player) {
		Player[] players = new Player[1];
		players[0] = player;
		updateAllCustomBlockDesigns(players);
	}
	
	public void updateAllCustomBlockDesigns(Player[] players) {
		for (TLongObjectIterator it = customBlockDesigns.iterator(); it.hasNext();) {
			it.advance();
			updateCustomBlockDesigns(players, it.key(), (GenericCustomBlock) it.value());
		}
	}
		
	private void updateCustomBlockDesigns(Player[] players, long data, GenericCustomBlock design) {
		
		PacketCustomBlockDesign p = new PacketCustomBlockDesign(msw(data), lsw(data), design);
		
		for (Player player : players) {
			if (player instanceof SpoutCraftPlayer) {
				SpoutCraftPlayer sp = (SpoutCraftPlayer)player;
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
		long key = toLong(id, data);
		if (!originalFriction.containsKey(key)) {
			originalFriction.put(key, getFriction(id, data));
		}
		net.minecraft.server.Block.byId[id].frictionFactor = friction;
	}

	@Override
	public void resetFriction(int id, short data) {
		long key = toLong(id, data);
		if (originalFriction.containsKey(key)) {
			setFriction(id, data, originalFriction.get(key));
		}
	}

	@Override
	public float getHardness(int id, short data) {
		return net.minecraft.server.Block.byId[id].j();
	}

	@Override
	public void setHardness(int id, short data, float hardness) {
		long key = toLong(id, data);
		if (!originalHardness.containsKey(key)) {
			originalHardness.put(key, getHardness(id, data));
		}
		net.minecraft.server.Block b = net.minecraft.server.Block.byId[id];
		if (b instanceof CustomMCBlock) {
			((CustomMCBlock)b).setHardness(hardness);
		}
	}

	@Override
	public void resetHardness(int id, short data) {
		long key = toLong(id, data);
		if (originalHardness.containsKey(key)) {
			setHardness(id, data, originalHardness.get(key));
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
}
