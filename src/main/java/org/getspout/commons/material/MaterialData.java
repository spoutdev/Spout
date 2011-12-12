package org.getspout.commons.material;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.getspout.commons.material.block.Air;
import org.getspout.commons.material.block.DoubleSlabs;
import org.getspout.commons.material.block.GenericLiquid;
import org.getspout.commons.material.block.Grass;
import org.getspout.commons.material.block.LongGrass;
import org.getspout.commons.material.block.Sapling;
import org.getspout.commons.material.block.Slab;
import org.getspout.commons.material.block.Solid;
import org.getspout.commons.material.block.StoneBricks;
import org.getspout.commons.material.block.Tree;
import org.getspout.commons.material.block.Wool;
import org.getspout.commons.material.item.Coal;
import org.getspout.commons.material.item.Dye;
import org.getspout.commons.material.item.GenericArmor;
import org.getspout.commons.material.item.GenericFood;
import org.getspout.commons.material.item.GenericItem;
import org.getspout.commons.material.item.GenericTool;
import org.getspout.commons.material.item.GenericWeapon;
import org.getspout.commons.material.Block;
import org.getspout.commons.material.CustomBlock;
import org.getspout.commons.material.CustomItem;
import org.getspout.commons.material.Item;
import org.getspout.commons.material.Material;
import org.getspout.commons.material.MaterialData;

public class MaterialData {
	private final static Object[] idLookup = new Object[3200];
	private final static List<CustomBlock> customBlocks = new LinkedList<CustomBlock>();
	private final static List<CustomItem> customItems = new LinkedList<CustomItem>();
	private final static TIntObjectHashMap<CustomItem> customItemLookup = new TIntObjectHashMap<CustomItem>(250);
	private final static TIntObjectHashMap<CustomBlock> customBlockLookup = new TIntObjectHashMap<CustomBlock>(250);
	private final static HashMap<String, Material> nameLookup = new HashMap<String, Material>(1000);
	private final static int FLINT_ID = 318;
	public static final Block air = new Air("Air");
	public static final Block stone = new Solid("Stone", 1);
	public static final Block grass = new Grass("Grass");
	public static final Block dirt = new Solid("Dirt",3);
	public static final Block cobblestone = new Solid("Cobblestone", 4);
	public static final Block wood = new Solid("Wooden Planks", 5);
	public static final Block sapling = new Sapling("Sapling", 0);
	public static final Block spruceSapling = new Sapling("Spruce Sapling", 1);
	public static final Block birchSapling = new Sapling("Birch Sapling", 2);
	public static final Block bedrock = new Solid("Bedrock", 7);
	public static final Block water = new GenericLiquid("Water", 8, true);
	public static final Block stationaryWater = new GenericLiquid("Stationary Water", 9, false);
	public static final Block lava = new GenericLiquid("Lava", 10, true);
	public static final Block stationaryLava = new GenericLiquid("Stationary Lava", 11, false);
	public static final Block sand = new Solid("Sand", 12, true);
	public static final Block gravel = new Solid("Gravel", 13, true);
	public static final Block goldOre = new Solid("Gold Ore", 14);
	public static final Block ironOre = new Solid("Iron Ore", 15);
	public static final Block coalOre = new Solid("Coal Ore", 16);
	public static final Block log = new Tree("Wood", 17, 0);
	public static final Block spruceLog = new Tree("Wood", 17, 1);
	public static final Block birchLog = new Tree("Wood", 17, 2);
	public static final Block leaves = new Tree("Leaves", 18, 0);
	public static final Block spruceLeaves = new Tree("Leaves", 18, 1);
	public static final Block birchLeaves= new Tree("Leaves", 18, 2);
	public static final Block sponge = new Solid("Sponge", 19);
	public static final Block glass = new Solid("Glass", 20);
	public static final Block lapisOre = new Solid("Lapis Lazuli Ore", 21);
	public static final Block lapisBlock = new Solid("Lapis Lazuli Block", 22);
	public static final Block dispenser = new Solid("Dispenser", 23);
	public static final Block sandstone = new Solid("Sandstone", 24);
	public static final Block noteblock = new Solid("Note Block", 25);
	public static final Block bedBlock = new Solid("Bed", 26);
	public static final Block poweredRail = new Solid("Powered Rail", 27);
	public static final Block detectorRail = new Solid("Detector Rail", 28);
	public static final Block pistonStickyBase = new Solid("Sticky Piston", 29);
	public static final Block web = new Solid("Cobweb", 30);
	public static final Block deadShrub = new LongGrass("Dead Grass", 31, 0);
	public static final Block tallGrass = new LongGrass("Tall Grass", 31, 1);
	public static final Block fern = new LongGrass("Fern", 31, 2);
	public static final Block deadBush = new LongGrass("Dead Shrubs", 32, 0);
	public static final Block pistonBase = new Solid("Piston", 33);
	public static final Block pistonExtension = new Solid("Piston (Head)", 34);
	public static final Block whiteWool = new Wool("Wool", 35,0);
	public static final Block orangeWool = new Wool("Orange Wool", 35,1);
	public static final Block magentaWool = new Wool("Magenta Wool", 35,2);
	public static final Block lightBlueWool = new Wool("Light Blue Wool", 35,3);
	public static final Block yellowWool = new Wool("Yellow Wool", 35,4);
	public static final Block limeWool = new Wool("Light Green Wool", 35,5);
	public static final Block pinkWool = new Wool("Pink Wool", 35,6);
	public static final Block greyWool = new Wool("Grey Wool", 35,7);
	public static final Block lightGreyWool = new Wool("Light Grey Wool", 35,8);
	public static final Block cyanWool = new Wool("Cyan Wool", 35,9);
	public static final Block purpleWool = new Wool("Purple Wool", 35,10);
	public static final Block blueWool = new Wool("Blue Wool", 35,11);
	public static final Block brownWool = new Wool("Brown Wool", 35,12);
	public static final Block greenWool = new Wool("Dark Green Wool", 35,13);
	public static final Block redWool = new Wool("Red Wool", 35,14);
	public static final Block blackWool = new Wool("Black Wool", 35,15);
	public static final Block movedByPiston = new Solid("Moved By Piston", 36);
	public static final Block dandelion = new Solid("Dandelion", 37);
	public static final Block rose = new Solid("Rose", 38);
	public static final Block brownMushroom = new Solid("Brown Mushroom", 39);
	public static final Block redMushroom = new Solid("Red Mushroom", 40);
	public static final Block goldBlock = new Solid("Gold Block", 41);
	public static final Block ironBlock = new Solid("Iron Block", 42);
	public static final Block stoneDoubleSlabs = new DoubleSlabs("Stone Double Slab", 43,0);
	public static final Block sandstoneDoubleSlabs = new DoubleSlabs("Sandstone Double Slab", 43,1);
	public static final Block woodenDoubleSlabs = new DoubleSlabs("Wooden Double Slab", 43,2);
	public static final Block cobblestoneDoubleSlabs = new DoubleSlabs("Stone Double Slab", 43,3);
	public static final Block brickDoubleSlabs = new DoubleSlabs("Brick Double Slab", 43,4);
	public static final Block stoneBrickDoubleSlabs = new DoubleSlabs("Stone Brick Double Slab", 43,5);
	public static final Block stoneSlab = new Slab("Stone Slab", 44,0);
	public static final Block sandstoneSlab = new Slab("Sandstone Slab", 44,1);
	public static final Block woodenSlab = new Slab("Wooden Slab", 44,2);
	public static final Block cobblestoneSlab = new Slab("Stone Slab", 44,3);
	public static final Block brickSlab = new Slab("Brick Slab", 44,4);
	public static final Block stoneBrickSlab = new Slab("Stone Brick Slab", 44,5);
	public static final Block brick = new Solid("Brick Block", 45);
	public static final Block tnt = new Solid("TNT", 46);
	public static final Block bookshelf = new Solid("Bookshelf", 47);
	public static final Block mossStone = new Solid("Moss Stone", 48);
	public static final Block obsidian = new Solid("Obsidian", 49);
	public static final Block torch = new Solid("Torch", 50);
	public static final Block fire = new Solid("Fire", 51);
	public static final Block monsterSpawner = new Solid("Monster Spawner", 52);
	public static final Block woodenStairs = new Solid("Wooden Stairs", 53);
	public static final Block chest = new Solid("Chest", 54);
	public static final Block redstoneWire = new Solid("Redstone Wire", 55);
	public static final Block diamondOre = new Solid("Diamond Ore", 56);
	public static final Block diamondBlock = new Solid("Diamond Block", 57);
	public static final Block craftingTable = new Solid("Crafting Table", 58);
	public static final Block crops = new Solid("Seeds", 59);
	public static final Block farmland = new Solid("Farmland", 60);
	public static final Block furnace = new Solid("Furance", 61);
	public static final Block burningfurnace = new Solid("Burning Furnace", 62);
	public static final Block signPost = new Solid("Sign Post", 63);
	public static final Block woodenDoorBlock = new Solid("Wooden Door", 64);
	public static final Block ladders = new Solid("Ladders", 65);
	public static final Block rails = new Solid("Rails", 66);
	public static final Block cobblestoneStairs = new Solid("Cobblestone Stairs", 67);
	public static final Block wallSign = new Solid("Wall Sign", 68);
	public static final Block lever = new Solid("Lever", 69);
	public static final Block stonePressurePlate = new Solid("Stone Pressure Plate", 70);
	public static final Block ironDoorBlock = new Solid("Iron Door", 71);
	public static final Block woodenPressurePlate = new Solid("Wooden Pressure Plate", 72);
	public static final Block redstoneOre = new Solid("Redstone Ore", 73);
	public static final Block glowingRedstoneOre = new Solid("Glowing Redstone Ore", 74);
	public static final Block redstoneTorchOff = new Solid("Redstone Torch", 75);
	public static final Block redstoneTorchOn = new Solid("Redstone Torch (On)", 76);
	public static final Block stoneButton = new Solid("Stone Button", 77);
	public static final Block snow = new Solid("Snow", 78);
	public static final Block ice = new Solid("Ice", 79);
	public static final Block snowBlock = new Solid("Snow Block", 80);
	public static final Block cactus = new Solid("Cactus", 81);
	public static final Block clayBlock = new Solid("Clay Block", 82);
	public static final Block sugarCaneBlock = new Solid("Sugar Cane", 83);
	public static final Block jukebox = new Solid("Jukebox", 84);
	public static final Block fence = new Solid("Fence", 85);
	public static final Block pumpkin = new Solid("Pumpkin", 86);
	public static final Block netherrack = new Solid("Netherrack", 87);
	public static final Block soulSand = new Solid("Soul Sand", 88);
	public static final Block glowstoneBlock = new Solid("Glowstone Block", 89);
	public static final Block portal = new Solid("Portal", 90);
	public static final Block jackOLantern = new Solid("Jack 'o' Lantern", 91);
	public static final Block cakeBlock = new Solid("Cake Block", 92);
	public static final Block redstoneRepeaterOff = new Solid("Redstone Repeater", 93);
	public static final Block redstoneRepeaterOn = new Solid("Redstone Repeater (On)", 94);
	public static final Block lockedChest = new Solid("Locked Chest", 95);
	public static final Block trapdoor = new Solid("Trapdoor", 96);
	public static final Block silverfishStone = new Solid("Silverfish Stone", 97);
	public static final Block stoneBricks = new StoneBricks("Stone Brick", 98, 0);
	public static final Block mossyStoneBricks = new StoneBricks("Mossy Stone Brick", 98, 1);
	public static final Block crackedStoneBricks = new StoneBricks("Cracked Stone Brick", 98, 2);
	public static final Block hugeBrownMushroom = new Solid("Huge Brown Mushroom", 99);
	public static final Block hugeRedMushroom = new Solid("Huge Red Mushroom", 100);
	public static final Block ironBars = new Solid("Iron Bars", 101);
	public static final Block glassPane = new Solid("Glass Pane", 102);
	public static final Block watermelon = new Solid("Watermelon", 103);
	public static final Block pumpkinStem = new Solid("Pumpkin Stem", 104);
	public static final Block melonStem = new Solid("Melon Stem", 105);
	public static final Block vines = new Solid("Vines", 106);
	public static final Block fenceGate = new Solid("Fence Gate", 107);
	public static final Block brickStairs = new Solid("Brick Stairs", 108);
	public static final Block stoneBrickStairs = new Solid("Stone Brick Stairs", 109);
	public static final Block mycelium = new Solid("Mycelium", 110);
	public static final Block lilyPad = new Solid("Lily Pad", 111);
	public static final Block netherBrick = new Solid("Nether Brick", 112);
	public static final Block netherBrickFence = new Solid("Nether Brick Fence", 113);
	public static final Block netherBrickStairs = new Solid("Nether Brick Stairs", 114);
	public static final Block netherWartBlock = new Solid("Nether Wart", 115);
	public static final Block enchantmentTable = new Solid("Enchantment Table", 116);
	public static final Block brewingStandBlock = new Solid("Brewing Stand", 117);
	public static final Block cauldronBlock = new Solid("Cauldron", 118);
	public static final Block endPortal = new Solid("End Portal", 119);
	public static final Block endPortalFrame = new Solid("End Portal Frame", 120);
	public static final Block endStone = new Solid("End Stone", 121);
	public static final Block dragonEgg = new Solid("Dragon Egg", 122);
	
	public static final Item ironShovel = new GenericTool("Iron Shovel", 256);
	public static final Item ironPickaxe = new GenericTool("Iron Pickaxe", 257);
	public static final Item ironAxe = new GenericTool("Iron Axe", 258);
	public static final Item flintAndSteel = new GenericTool("Flint and Steel", 259);
	public static final Item redApple = new GenericFood("Apple", 260, 4);
	public static final Item bow = new GenericWeapon("Bow", 261);
	public static final Item arrow = new GenericItem("Arrow", 262);
	public static final Item coal = new Coal("Coal", 263,0);
	public static final Item charcoal = new Coal("Charcoal", 263,1);
	public static final Item diamond = new GenericItem("Diamond", 264);
	public static final Item ironIngot = new GenericItem("Iron Ingot", 265);
	public static final Item goldIngot = new GenericItem("Gold Ingot", 266);
	public static final Item ironSword = new GenericWeapon("Iron Sword", 267);
	public static final Item woodenSword = new GenericWeapon("Wooden Sword", 268);
	public static final Item woodenShovel = new GenericTool("Wooden Shovel", 269);
	public static final Item woodenPickaxe = new GenericTool("Wooden Pickaxe", 270);
	public static final Item woodenAxe = new GenericTool("Wooden Axe", 271);
	public static final Item stoneSword = new GenericWeapon("Stone Sword", 272);
	public static final Item stoneShovel = new GenericTool("Stone Shovel", 273);
	public static final Item stonePickaxe = new GenericTool("Stone Pickaxe", 274);
	public static final Item stoneAxe = new GenericTool("Stone Axe", 275);
	public static final Item diamondSword = new GenericWeapon("Diamond Sword", 276);
	public static final Item diamondShovel = new GenericTool("Diamond Shovel", 277);
	public static final Item diamondPickaxe = new GenericTool("Diamond Pickaxe", 278);
	public static final Item diamondAxe = new GenericTool("Diamond Axe", 279);
	public static final Item stick = new GenericItem("Stick", 280);
	public static final Item bowl = new GenericItem("Bowl", 281);
	public static final Item mushroomSoup = new GenericFood("Mushroom Soup", 282, 8);
	public static final Item goldSword = new GenericWeapon("Gold Sword", 283);
	public static final Item goldShovel = new GenericTool("Gold Shovel", 284);
	public static final Item goldPickaxe = new GenericTool("Gold Pickaxe", 285);
	public static final Item goldAxe = new GenericTool("Gold Axe", 286);
	public static final Item string = new GenericItem("String", 287);
	public static final Item feather = new GenericItem("Feather", 288);
	public static final Item gunpowder = new GenericItem("Gunpowder", 289);
	public static final Item woodenHoe = new GenericTool("Wooden Hoe", 290);
	public static final Item stoneHoe = new GenericTool("Stone Hoe", 291);
	public static final Item ironHoe = new GenericTool("Iron Hoe", 292);
	public static final Item diamondHoe = new GenericTool("Diamond Hoe", 293);
	public static final Item goldHoe = new GenericTool("Gold Hoe", 294);
	public static final Item seeds = new GenericItem("Seeds", 295);
	public static final Item wheat = new GenericItem("Wheat", 296);
	public static final Item bread = new GenericFood("Bread", 297, 5);
	public static final Item leatherCap = new GenericArmor("Leather Cap", 298);
	public static final Item leatherTunic = new GenericArmor("Leather Tunic", 299);
	public static final Item leatherPants = new GenericArmor("Leather Pants", 300);
	public static final Item leatherBoots = new GenericArmor("Leather Boots", 301);
	public static final Item chainHelmet = new GenericArmor("Chain Helmet", 302);
	public static final Item chainChestplate = new GenericArmor("Chain Chestplate", 303);
	public static final Item chainLeggings = new GenericArmor("Chain Leggings", 304);
	public static final Item chainBoots = new GenericArmor("Chain Boots", 305);
	public static final Item ironHelmet = new GenericArmor("Iron Helmet", 306);
	public static final Item ironChestplate = new GenericArmor("Iron Chestplate", 307);
	public static final Item ironLeggings = new GenericArmor("Iron Leggings", 308);
	public static final Item ironBoots = new GenericArmor("Iron Boots", 309);
	public static final Item diamondHelmet = new GenericArmor("Diamond Helmet", 310);
	public static final Item diamondChestplate = new GenericArmor("Diamond Chestplate", 311);
	public static final Item diamondLeggings = new GenericArmor("Diamond Leggings", 312);
	public static final Item diamondBoots = new GenericArmor("Diamond Boots", 313);
	public static final Item goldHelmet = new GenericArmor("Gold Helmet", 314);
	public static final Item goldChestplate = new GenericArmor("Gold Chestplate", 315);
	public static final Item goldLeggings = new GenericArmor("Gold Leggings", 316);
	public static final Item goldBoots = new GenericArmor("Gold Boots", 317);
	public static final Item flint = new GenericItem("Flint", 318, 0, true);
	public static final Item rawPorkchop = new GenericFood("Raw Porkchop", 319, 3);
	public static final Item cookedPorkchop = new GenericFood("Cooked Porkchop", 320, 8);
	public static final Item paintings = new GenericItem("Paintings", 321);
	public static final Item goldenApple = new GenericFood("Golden Apple", 322, 10);
	public static final Item sign = new GenericItem("Sign", 323);
	public static final Item woodenDoor = new GenericItem("Wooden Door", 324);
	public static final Item bucket = new GenericItem("Bucket", 325);
	public static final Item waterBucket = new GenericItem("Water Bucket", 326);
	public static final Item lavaBucket = new GenericItem("Lava Bucket", 327);
	public static final Item minecart = new GenericItem("Minecart", 328);
	public static final Item saddle = new GenericItem("Saddle", 329);
	public static final Item ironDoor = new GenericItem("Iron Door", 330);
	public static final Item redstone = new GenericItem("Redstone", 331);
	public static final Item snowball = new GenericItem("Snowball", 332);
	public static final Item boat = new GenericItem("Boat", 333);
	public static final Item leather = new GenericItem("Leather", 334);
	public static final Item milk = new GenericItem("Milk", 335);
	public static final Item clayBrick = new GenericItem("Brick", 336);
	public static final Item clay = new GenericItem("Clay", 337);
	public static final Item sugarCane = new GenericItem("Sugar Cane", 338);
	public static final Item paper = new GenericItem("Paper", 339);
	public static final Item book = new GenericItem("Book", 340);
	public static final Item slimeball = new GenericItem("Slimeball", 341);
	public static final Item minecartChest = new GenericItem("Minecart with Chest", 342);
	public static final Item minecartFurnace = new GenericItem("Minecart with Furnace", 343);
	public static final Item egg = new GenericItem("Egg", 344);
	public static final Item compass = new GenericItem("Compass", 345);
	public static final Item fishingRod = new GenericTool("Fishing Rod", 346);
	public static final Item clock = new GenericItem("Clock", 347);
	public static final Item glowstoneDust = new GenericItem("Glowstone Dust", 348);
	public static final Item rawFish = new GenericFood("Raw Fish", 349, 2);
	public static final Item cookedFish = new GenericFood("Cooked Fish", 350, 5);
	public static final Item inkSac = new Dye("Ink Sac", 351,0);
	public static final Item roseRed = new Dye("Rose Red", 351,1);
	public static final Item cactusGreen = new Dye("Cactus Green", 351,2);
	public static final Item cocoaBeans = new Dye("Cocoa Beans", 351,3);
	public static final Item lapisLazuli = new Dye("Lapis Lazuli", 351,4);
	public static final Item purpleDye = new Dye("Purple Dye", 351,5);
	public static final Item cyanDye = new Dye("Cyan Dye", 351,6);
	public static final Item lightGrayDye = new Dye("Light Gray Dye", 351,7);
	public static final Item grayDye = new Dye("Gray Dye", 351,8);
	public static final Item pinkDye = new Dye("Pink Dye", 351,9);
	public static final Item limeDye = new Dye("Lime Dye", 351,10);
	public static final Item dandelionYellow = new Dye("Dandelion Yellow", 351,11);
	public static final Item lightBlueDye = new Dye("Light Blue Dye", 351,12);
	public static final Item magentaDye = new Dye("Magenta Dye", 351,13);
	public static final Item orangeDye = new Dye("Orange Dye", 351,14);
	public static final Item boneMeal = new Dye("Bone Meal", 351,15);
	public static final Item bone = new GenericItem("Bone", 352);
	public static final Item sugar = new GenericItem("Sugar", 353);
	public static final Item cake = new GenericItem("Cake", 354);
	public static final Item bed = new GenericItem("Bed", 355);
	public static final Item redstoneRepeater = new GenericItem("Redstone Repeater", 356);
	public static final Item cookie = new GenericFood("Cookie", 357, 1);
	public static final Item map = new GenericItem("Map", 358);
	public static final Item shears = new GenericTool("Shears", 359);
	public static final Item melonSlice = new GenericFood("Melon Slice", 360, 2);
	public static final Item pumpkinSeeds = new GenericItem("Pumpkin Seeds", 361);
	public static final Item melonSeeds = new GenericItem("Melon Seeds", 362);
	public static final Item rawBeef = new GenericFood("Raw Beef", 363, 3);
	public static final Item steak = new GenericFood("Steak", 364, 8);
	public static final Item rawChicken = new GenericFood("Raw Chicken", 365, 2);
	public static final Item cookedChicken = new GenericFood("Cooked Chicken", 366, 6);
	public static final Item rottenFlesh = new GenericFood("Rotten Flesh", 367, 4);
	public static final Item enderPearl = new GenericItem("Ender Pearl", 368);
	public static final Item blazeRod = new GenericItem("Blaze Rod", 369);
	public static final Item ghastTear = new GenericItem("Ghast Tear", 370);
	public static final Item goldNugget = new GenericItem("Gold Nugget", 371);
	public static final Item netherWart = new GenericItem("Nether Wart", 372);
	public static final Item potion = new GenericItem("Potion", 373);
	public static final Item glassBottle = new GenericItem("Glass Bottle", 374);
	public static final Item spiderEye = new GenericFood("Spider Eye", 375, 2);
	public static final Item fermentedSpiderEye = new GenericItem("Fermented Spider Eye", 376);
	public static final Item blazePowder = new GenericItem("Blaze Powder", 377);
	public static final Item magmaCream = new GenericItem("Magma Cream", 378);
	public static final Item brewingStand = new GenericItem("Brewing Stand", 379);
	public static final Item cauldron = new GenericItem("Cauldron", 380);
	public static final Item eyeOfEnder = new GenericItem("Eye of Ender", 381);

	public static final Item goldMusicDisc = new GenericItem("Music Disc", 2256);
	public static final Item greenMusicDisc = new GenericItem("Music Disc", 2257);
	public static final Item orangeMusicDisc = new GenericItem("Music Disc", 2258);
	public static final Item redMusicDisc = new GenericItem("Music Disc", 2259);
	public static final Item cyanMusicDisc = new GenericItem("Music Disc", 2260);
	public static final Item blueMusicDisc = new GenericItem("Music Disc", 2261);
	public static final Item purpleMusicDisc = new GenericItem("Music Disc", 2262);
	public static final Item blackMusicDisc = new GenericItem("Music Disc", 2263);
	public static final Item whiteMusicDisc = new GenericItem("Music Disc", 2264);
	public static final Item forestGreenMusicDisc = new GenericItem("Music Disc", 2265);
	public static final Item brokenMusicDisc = new GenericItem("Music Disc", 2266);
	
	static {
		reset();
	}
	
	public static void reset() {
		//reset all values
		for (int i = 0; i < idLookup.length; i++) {
			idLookup[i] = null;
		}
		nameLookup.clear();
		customBlocks.clear();
		customBlockLookup.clear();
		customItems.clear();
		customItemLookup.clear();

		Field[] fields = MaterialData.class.getFields();
		for (Field f : fields) {
			if (Modifier.isStatic(f.getModifiers())) {
				try {
					Object value = f.get(null);
					if (value instanceof Material) {
						Material mat = (Material)value;
						mat.setName(mat.getNotchianName());
						
						int id = mat.getRawId();
						int data = mat.getRawData();
						
						insertItem(id, data, mat);
					}
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				}
			}
		}
	}
	
	private static void insertItem(int id, int data, Material mat) {
		if (id < idLookup.length && id > -1) {
			nameLookup.put(mat.getNotchianName().toLowerCase(), mat);
			if (idLookup[mat.getRawId()] == null) {
				idLookup[mat.getRawId()] = mat;
			}
			else if (idLookup[mat.getRawId()] instanceof Material[]) {
				Material[] multiple = (Material[])idLookup[mat.getRawId()];
				int size = mat.getRawData() * 2 + 1;
				if (multiple.length < size) {
					multiple = adjust(multiple, size);
				}
				multiple[mat.getRawData()] =  mat;
				idLookup[mat.getRawId()] = multiple;
			}
			else if (idLookup[mat.getRawId()] instanceof Material) {
				Material existing = (Material) idLookup[mat.getRawId()];
				int size = Math.max(existing.getRawData(), mat.getRawData()) * 2 + 1;
				Material[] multiple = new Material[size];
				multiple[existing.getRawData()] = existing;
				multiple[mat.getRawData()] = mat;
				idLookup[mat.getRawId()] = multiple;
			}
			else {
				System.out.println("WARNING! Unknown lookup contents, " + idLookup[mat.getRawId()]);
			}
		}
		else {
			System.out.println("WARNING! Material " + mat.getNotchianName() + " Could Not Fit " + id + ", " + data + " into the lookup array!");
		}
	}
	
	private static Material[] adjust(Material[] oldArray, int size) {
		Material[] newArray = new Material[size];
		for (int i = 0; i < oldArray.length; i++) {
			newArray[i] = oldArray[i];
		}
		return newArray;
	}

	/**
	 * Adds a custom item to the material list
	 * @param item to add
	 */
	public static void addCustomItem(CustomItem item) {
		customItemLookup.put(item.getCustomId(), item);
		customItems.add(item);
		nameLookup.put(item.getNotchianName().toLowerCase(), item);
	}
	
	/**
	 * Adds a custom block to the material list
	 * @param block to add
	 */
	public static void addCustomBlock(CustomBlock block) {
		customBlockLookup.put(block.getCustomId(), block);
		customBlocks.add(block);
		nameLookup.put(block.getNotchianName().toLowerCase(), block);
	}
	
	/**
	 * 
	 * @param Gets the material from the given id
	 * @return material, or null if none found
	 */
	public static Material getMaterial(int id) {
		return getMaterial(id, (short)0);
	}
	
	/**
	 * Gets the material from the given id and data.
	 * 
	 * If a non-zero data value is given for a material with no subtypes, the material at the id and data value of zero will be returned instead.
	 * @param id to get
	 * @param data to get
	 * @return material or null if none found
	 */
	public static Material getMaterial(int id, short data) {
		Object o = idLookup[id];
		if (id == FLINT_ID && data >= 1024){
			o = getCustomBlock(data);
			if (o == null) {
				o = getCustomItem(data);
			}
			return (Material)o;
		}
		if (o == null || o instanceof Material) {
			return (Material)o;
		}
		Material[] materials = (Material[])o;
		Material m = materials[0];
		if (m.hasSubtypes() && data < materials.length && data > -1)
			return materials[data];
		return m;
	}
	
	/**
	 * Gets the material from the given id and data, or creates it if nessecary.
	 * 
	 * Creation occurs when a material exists at the given id, and zero data value, but does not have any subtypes.
	 * A new material that is a copy of the material at the given id and zero data value is created.
	 * If creation fails for any reason, null will be returned.
	 * If the material has subtypes normally, null will be returned if there is no subtype at the given data value
	 * @param id to get
	 * @param data to get
	 * @return material found, created, or null
	 */
	public static Material getOrCreateMaterial(int id, short data) {
		Object o = idLookup[id];
		Material[] materials;
		Material mat;
		if (o == null || o instanceof Material) {
			mat = (Material)o;
			materials = new Material[Math.max(mat.getRawData(), data) *2 + 1];
			materials[mat.getRawData()] = mat;
		}
		else {
			materials = (Material[])o;
			if (data > materials.length) {
				materials = adjust(materials, data * 2 + 1);
			}
			mat = materials[data];
		}
		idLookup[id] = materials;
		
		if (mat != null) {
			if (mat.getRawId() == id && mat.getRawData() == data) {
				return mat;
			}
			Material orig = mat;
			try {
				Class<?>[] params = {String.class, int.class, int.class};
				Constructor<? extends Material> constructor = orig.getClass().getConstructor(params);
				constructor.setAccessible(true);
				mat = constructor.newInstance(orig.getName(), id, data);
				insertItem(id, data, mat);
			}
			catch (Exception e) {
				System.out.println("[Spoutcraft] Failed to create a duplicate item in MaterialData.getOrCreateMaterial, for " + id + ", " + data);
			}
			return mat;
		}
		return null;
	}
	
	/**
	 * Gets the block at the given id, or null if none found
	 * @param id to get
	 * @return block, or null if none found
	 */
	public static Block getBlock(int id) {
		return getBlock(id, (short)0);
	}
	
	/**
	 * Gets the block at the given id and data, or null if none found
	 * @param id to get
	 * @param data to get
	 * @return block, or null if none found
	 */
	public static Block getBlock(int id, short data) {
		Material mat = getMaterial(id, data);
		if (mat instanceof Block) {
			return (Block)mat;
		}
		return null;
	}

	/**
	 * Gets an array of all currently registered custom blocks
	 * @return all registered custom blocks
	 */
	public static CustomBlock[] getCustomBlocks() {
		CustomBlock[] blocks = new CustomBlock[customBlocks.size()];
		for (int i = 0; i < blocks.length; i++) {
			blocks[i] = customBlocks.get(i);
		}
		return blocks;
	}

	/**
	 * Gets an array of all currently registered custom items
	 * @return all registered custom items
	 */
	public static CustomItem[] getCustomItems() {
		CustomItem[] items = new CustomItem[customItems.size()];
		for (int i = 0; i < items.length; i++) {
			items[i] = customItems.get(i);
		}
		return items;
	}

	/**
	 * Gets the custom block associated with the custom block id
	 * @param customId
	 * @return
	 */
	public static CustomBlock getCustomBlock(int customId) {
		return customBlockLookup.get(customId);
	}
	
	/**
	 * Gets the custom item associated with the given id
	 * @param customId to look up from
	 * @return custom item
	 */
	public static CustomItem getCustomItem(int customId) {
		return customItemLookup.get(customId);
	}
	
	/**
	 * Gets the item at the given id, or null if none found
	 * @param id to get
	 * @return item or null if none found
	 */
	public static Item getItem(int id) {
		return getItem(id, (short)0);
	}
	
	/**
	 * Gets the item at the given id and data, or null if none found
	 * @param id to get
	 * @param data to get
	 * @return item or null if none found
	 */
	public static Item getItem(int id, short data) {
		Material mat = getMaterial(id, data);
		if (mat instanceof Item) {
			return (Item)mat;
		}
		return null;
	}

	/**
	 * Returns a list of all the current materials in the game, notchian, custom, or otherwise
	 * @return a list of all materials
	 */
	public static List<Material> getMaterials() {
		LinkedList<Material> materials = new LinkedList<Material>();
		for (int i = 0; i < idLookup.length; i++) {
			if (idLookup[i] instanceof Material) {
				materials.add((Material)idLookup[i]);
			}
			else if (idLookup[i] instanceof Material[]) {
				for (Material mat : ((Material[])idLookup[i])) {
					if (mat != null)
						materials.add(mat);
				}
			}
		}
		materials.addAll(customBlocks);
		materials.addAll(customItems);
		return materials;
	}
	
	/**
	 * Gets the associated material with it's notchian name
	 * @param name to lookup
	 * @return material, or null if none found
	 */
	public static Material getMaterial(String notchianName) {
		return nameLookup.get(notchianName.toLowerCase());
	}
}
