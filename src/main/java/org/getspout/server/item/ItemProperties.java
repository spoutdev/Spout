package org.getspout.server.item;

import java.util.Arrays;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import org.getspout.server.block.BlockID;
import org.getspout.server.item.physics.DefaultItemPhysics;
import org.getspout.server.item.physics.EmptyBucketPhysics;
import org.getspout.server.item.physics.FilledBucketPhysics;
import org.getspout.server.item.physics.GlassBottlePhysics;
import org.getspout.server.item.physics.ItemPhysics;
import org.getspout.server.item.physics.SignItemPhysics;

/**
 * An enum containing an entry for every block describing that block's physical
 * properties.
 */
public enum ItemProperties {
	IRON_SPADE(ItemID.IRON_SPADE, nbtData()),
	IRON_PICKAXE(ItemID.IRON_PICKAXE, nbtData()),
	IRON_AXE(ItemID.IRON_AXE, nbtData()),
	FLINT_AND_STEEL(ItemID.FLINT_AND_STEEL),
	APPLE(ItemID.APPLE),
	BOW(ItemID.BOW),
	ARROW(ItemID.ARROW),
	COAL(ItemID.COAL),
	DIAMOND(ItemID.DIAMOND),
	IRON_INGOT(ItemID.IRON_INGOT),
	GOLD_INGOT(ItemID.GOLD_INGOT),
	IRON_SWORD(ItemID.IRON_SWORD, nbtData()),
	WOOD_SWORD(ItemID.WOOD_SWORD, nbtData()),
	WOOD_SPADE(ItemID.WOOD_SPADE, nbtData()),
	WOOD_PICKAXE(ItemID.WOOD_PICKAXE, nbtData()),
	WOOD_AXE(ItemID.WOOD_AXE, nbtData()),
	STONE_SWORD(ItemID.STONE_SWORD, nbtData()),
	STONE_SPADE(ItemID.STONE_SPADE, nbtData()),
	STONE_PICKAXE(ItemID.STONE_PICKAXE, nbtData()),
	STONE_AXE(ItemID.STONE_AXE, nbtData()),
	DIAMOND_SWORD(ItemID.DIAMOND_SWORD, nbtData()),
	DIAMOND_SPADE(ItemID.DIAMOND_SPADE, nbtData()),
	DIAMOND_PICKAXE(ItemID.DIAMOND_PICKAXE, nbtData()),
	DIAMOND_AXE(ItemID.DIAMOND_AXE, nbtData()),
	STICK(ItemID.STICK),
	BOWL(ItemID.BOWL),
	MUSHROOM_SOUP(ItemID.MUSHROOM_SOUP),
	GOLD_SWORD(ItemID.GOLD_SWORD, nbtData()),
	GOLD_SPADE(ItemID.GOLD_SPADE, nbtData()),
	GOLD_PICKAXE(ItemID.GOLD_PICKAXE, nbtData()),
	GOLD_AXE(ItemID.GOLD_AXE, nbtData()),
	STRING(ItemID.STRING),
	FEATHER(ItemID.FEATHER),
	SULPHUR(ItemID.SULPHUR),
	WOOD_HOE(ItemID.WOOD_HOE, nbtData()),
	STONE_HOE(ItemID.STONE_HOE, nbtData()),
	IRON_HOE(ItemID.IRON_HOE, nbtData()),
	DIAMOND_HOE(ItemID.DIAMOND_HOE, nbtData()),
	GOLD_HOE(ItemID.GOLD_HOE, nbtData()),
	SEEDS(ItemID.SEEDS, placedBlock(new MaterialData(BlockID.CROPS, CropState.VERY_TALL.getData()))), // Tall crops are tall
	WHEAT(ItemID.WHEAT),
	BREAD(ItemID.BREAD),
	LEATHER_HELMET(ItemID.LEATHER_HELMET, nbtData()),
	LEATHER_CHESTPLATE(ItemID.LEATHER_CHESTPLATE, nbtData()),
	LEATHER_LEGGINGS(ItemID.LEATHER_LEGGINGS, nbtData()),
	LEATHER_BOOTS(ItemID.LEATHER_BOOTS, nbtData()),
	CHAINMAIL_HELMET(ItemID.CHAINMAIL_HELMET, nbtData()),
	CHAINMAIL_CHESTPLATE(ItemID.CHAINMAIL_CHESTPLATE, nbtData()),
	CHAINMAIL_LEGGINGS(ItemID.CHAINMAIL_LEGGINGS, nbtData()),
	CHAINMAIL_BOOTS(ItemID.CHAINMAIL_BOOTS, nbtData()),
	IRON_HELMET(ItemID.IRON_HELMET, nbtData()),
	IRON_CHESTPLATE(ItemID.IRON_CHESTPLATE, nbtData()),
	IRON_LEGGINGS(ItemID.IRON_LEGGINGS, nbtData()),
	IRON_BOOTS(ItemID.IRON_BOOTS, nbtData()),
	DIAMOND_HELMET(ItemID.DIAMOND_HELMET, nbtData()),
	DIAMOND_CHESTPLATE(ItemID.DIAMOND_CHESTPLATE, nbtData()),
	DIAMOND_LEGGINGS(ItemID.DIAMOND_LEGGINGS, nbtData()),
	DIAMOND_BOOTS(ItemID.DIAMOND_BOOTS, nbtData()),
	GOLD_HELMET(ItemID.GOLD_HELMET, nbtData()),
	GOLD_CHESTPLATE(ItemID.GOLD_CHESTPLATE, nbtData()),
	GOLD_LEGGINGS(ItemID.GOLD_LEGGINGS, nbtData()),
	GOLD_BOOTS(ItemID.GOLD_BOOTS, nbtData()),
	FLINT(ItemID.FLINT),
	PORK(ItemID.PORK),
	GRILLED_PORK(ItemID.GRILLED_PORK),
	PAINTING(ItemID.PAINTING),
	GOLDEN_APPLE(ItemID.GOLDEN_APPLE),
	SIGN(ItemID.SIGN, physics(new SignItemPhysics())),
	WOOD_DOOR(ItemID.WOOD_DOOR, placedBlock(new MaterialData(BlockID.WOODEN_DOOR))),
	BUCKET(ItemID.BUCKET, physics(new EmptyBucketPhysics(new int[] {BlockID.STATIONARY_LAVA, BlockID.STATIONARY_WATER}, new int[] {ItemID.LAVA_BUCKET, ItemID.WATER_BUCKET}))),
	WATER_BUCKET(ItemID.WATER_BUCKET, physics(new FilledBucketPhysics(ItemID.WATER_BUCKET, BlockID.WATER, true))),
	LAVA_BUCKET(ItemID.LAVA_BUCKET, physics(new FilledBucketPhysics(ItemID.LAVA_BUCKET, BlockID.LAVA, false))),
	MINECART(ItemID.MINECART),
	SADDLE(ItemID.SADDLE),
	IRON_DOOR(ItemID.IRON_DOOR, placedBlock(new MaterialData(BlockID.IRON_DOOR_BLOCK))),
	REDSTONE(ItemID.REDSTONE, placedBlock(new MaterialData(BlockID.REDSTONE_WIRE))),
	SNOW_BALL(ItemID.SNOW_BALL),
	BOAT(ItemID.BOAT),
	LEATHER(ItemID.LEATHER),
	MILK_BUCKET(ItemID.MILK_BUCKET, physics(new FilledBucketPhysics(ItemID.MILK_BUCKET, BlockID.AIR, false))),
	CLAY_BRICK(ItemID.CLAY_BRICK),
	CLAY_BALL(ItemID.CLAY_BALL),
	SUGAR_CANE(ItemID.SUGAR_CANE, placedBlock(new MaterialData(BlockID.SUGAR_CANE_BLOCK))),
	PAPER(ItemID.PAPER),
	BOOK(ItemID.BOOK),
	SLIME_BALL(ItemID.SLIME_BALL),
	STORAGE_MINECART(ItemID.STORAGE_MINECART),
	POWERED_MINECART(ItemID.POWERED_MINECART),
	EGG(ItemID.EGG),
	COMPASS(ItemID.COMPASS),
	FISHING_ROD(ItemID.FISHING_ROD, nbtData()),
	WATCH(ItemID.WATCH),
	GLOWSTONE_DUST(ItemID.GLOWSTONE_DUST),
	RAW_FISH(ItemID.RAW_FISH),
	COOKED_FISH(ItemID.COOKED_FISH),
	INK_SACK(ItemID.INK_SACK),
	BONE(ItemID.BONE),
	SUGAR(ItemID.SUGAR),
	CAKE(ItemID.CAKE, placedBlock(new MaterialData(BlockID.CAKE_BLOCK))),
	BED(ItemID.BED, placedBlock(new MaterialData(BlockID.BED_BLOCK))),
	DIODE(ItemID.DIODE, placedBlock(new MaterialData(BlockID.DIODE_BLOCK_OFF))),
	COOKIE(ItemID.COOKIE),
	MAP(ItemID.MAP),
	SHEARS(ItemID.SHEARS, nbtData()),
	MELON(ItemID.MELON),
	PUMPKIN_SEEDS(ItemID.PUMPKIN_SEEDS, placedBlock(new MaterialData(BlockID.PUMPKIN_STEM))),
	MELON_SEEDS(ItemID.MELON_SEEDS, placedBlock(new MaterialData(BlockID.MELON_STEM))),
	RAW_BEEF(ItemID.RAW_BEEF),
	COOKED_BEEF(ItemID.COOKED_BEEF),
	RAW_CHICKEN(ItemID.RAW_CHICKEN),
	COOKED_CHICKEN(ItemID.COOKED_CHICKEN),
	ROTTEN_FLESH(ItemID.ROTTEN_FLESH),
	ENDER_PEARL(ItemID.ENDER_PEARL),
	BLAZE_ROD(ItemID.BLAZE_ROD),
	GHAST_TEAR(ItemID.GHAST_TEAR),
	GOLD_NUGGET(ItemID.GOLD_NUGGET),
	NETHER_STALK(ItemID.NETHER_WART_SEED, placedBlock(new MaterialData(BlockID.NETHER_WART))),
	POTION(ItemID.POTION),
	GLASS_BOTTLE(ItemID.GLASS_BOTTLE, physics(new GlassBottlePhysics())),
	SPIDER_EYE(ItemID.SPIDER_EYE),
	FERMENTED_SPIDER_EYE(ItemID.FERMENTED_SPIDER_EYE),
	BLAZE_POWDER(ItemID.BLAZE_POWDER),
	MAGMA_CREAM(ItemID.MAGMA_CREAM),
	BREWING_STAND_ITEM(ItemID.BREWING_STAND, placedBlock(new MaterialData(BlockID.BREWING_STAND))),
	CAULDRON_ITEM(ItemID.CAULDRON, placedBlock(new MaterialData(BlockID.CAULDRON))),
	EYE_OF_ENDER(ItemID.EYE_OF_ENDER),
	GLISTERING_MELON(ItemID.GLISTERING_MELON),
	DISC_13(ItemID.DISC_13),
	DISC_CAT(ItemID.DISC_CAT),
	DISC_BLOCKS(ItemID.DISC_BLOCKS),
	DISC_CHIRP(ItemID.DISC_CHIRP),
	DISC_FAR(ItemID.DISC_FAR),
	DISC_MALL(ItemID.DISC_MALL),
	DISC_MELLOHI(ItemID.DISC_MELLOHI),
	DISC_STAL(ItemID.DISC_STAL),
	DISC_STRAD(ItemID.DISC_STRAD),
	DISC_WARD(ItemID.DISC_WARD),
	DISC_11(ItemID.DISC_11);

	// -----------------

	private static ItemProperties[] byId = new ItemProperties[3200];

	static {
		for (ItemProperties prop : values()) {
			if (byId.length > prop.id) {
				byId[prop.id] = prop;
			} else {
				byId = Arrays.copyOf(byId, prop.id + 2);
				byId[prop.id] = prop;
			}
		}
	}

	public static ItemProperties get(Material material) {
		return get(material.getId());
	}

	public static ItemProperties get(int id) {
		if (id >= 0 && byId.length > id) {
			return byId[id];
		} else {
			return null;
		}
	}

	// -----------------

	private final int id;
	private boolean nbtData;
	private MaterialData placedBlock;
	private ItemPhysics physics;

	private ItemProperties(int id, Property... props) {
		this.id = id;
		physics = new DefaultItemPhysics(id);

		for (Property p : props) {
			p.apply(this);
		}
	}

	public boolean hasNbtData() {
		return nbtData;
	}

	public int getId() {
		return id;
	}

	public ItemPhysics getPhysics() {
		return physics;
	}

	public MaterialData getPlacedBlock() {
		return placedBlock;
	}

	// -----------------

	private interface Property {
		void apply(ItemProperties prop);
	}

	private static Property nbtData() {
		return new Property() {
			@Override
			public void apply(ItemProperties p) {
				p.nbtData = true;
			}
		};
	}

	private static Property physics(final ItemPhysics physics) {
		return new Property() {
			@Override
			public void apply(ItemProperties p) {
				p.physics = physics;
			}
		};
	}

	private static Property placedBlock(final MaterialData placedBlock) {
		return new Property() {
			@Override
			public void apply(ItemProperties p) {
				p.placedBlock = placedBlock;
			}
		};
	}
}
