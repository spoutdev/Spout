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
import org.getspout.commons.material.item.GenericItemMaterial;
import org.getspout.commons.material.item.GenericTool;
import org.getspout.commons.material.item.GenericWeapon;
import org.getspout.commons.material.BlockMaterial;
import org.getspout.commons.material.CustomBlockMaterial;
import org.getspout.commons.material.CustomItem;
import org.getspout.commons.material.ItemMaterial;
import org.getspout.commons.material.Material;
import org.getspout.commons.material.MaterialData;

public class MaterialData {
	private final static Object[] idLookup = new Object[3200];
	private final static List<CustomBlockMaterial> customBlocks = new LinkedList<CustomBlockMaterial>();
	private final static List<CustomItem> customItems = new LinkedList<CustomItem>();
	private final static TIntObjectHashMap<CustomItem> customItemLookup = new TIntObjectHashMap<CustomItem>(250);
	private final static TIntObjectHashMap<CustomBlockMaterial> customBlockLookup = new TIntObjectHashMap<CustomBlockMaterial>(250);
	private final static HashMap<String, Material> nameLookup = new HashMap<String, Material>(1000);
	private final static int FLINT_ID = 318;
	public static final BlockMaterial air = new Air("Air");
	public static final BlockMaterial stone = new Solid("Stone", 1);
	public static final BlockMaterial grass = new Grass("Grass");
	public static final BlockMaterial dirt = new Solid("Dirt",3);
	public static final BlockMaterial cobblestone = new Solid("Cobblestone", 4);
	public static final BlockMaterial wood = new Solid("Wooden Planks", 5);
	public static final BlockMaterial sapling = new Sapling("Sapling", 0);
	public static final BlockMaterial spruceSapling = new Sapling("Spruce Sapling", 1);
	public static final BlockMaterial birchSapling = new Sapling("Birch Sapling", 2);
	public static final BlockMaterial bedrock = new Solid("Bedrock", 7);
	public static final BlockMaterial water = new GenericLiquid("Water", 8, true);
	public static final BlockMaterial stationaryWater = new GenericLiquid("Stationary Water", 9, false);
	public static final BlockMaterial lava = new GenericLiquid("Lava", 10, true);
	public static final BlockMaterial stationaryLava = new GenericLiquid("Stationary Lava", 11, false);
	public static final BlockMaterial sand = new Solid("Sand", 12, true);
	public static final BlockMaterial gravel = new Solid("Gravel", 13, true);
	public static final BlockMaterial goldOre = new Solid("Gold Ore", 14);
	public static final BlockMaterial ironOre = new Solid("Iron Ore", 15);
	public static final BlockMaterial coalOre = new Solid("Coal Ore", 16);
	public static final BlockMaterial log = new Tree("Wood", 17, 0);
	public static final BlockMaterial spruceLog = new Tree("Wood", 17, 1);
	public static final BlockMaterial birchLog = new Tree("Wood", 17, 2);
	public static final BlockMaterial leaves = new Tree("Leaves", 18, 0);
	public static final BlockMaterial spruceLeaves = new Tree("Leaves", 18, 1);
	public static final BlockMaterial birchLeaves= new Tree("Leaves", 18, 2);
	public static final BlockMaterial sponge = new Solid("Sponge", 19);
	public static final BlockMaterial glass = new Solid("Glass", 20);
	public static final BlockMaterial lapisOre = new Solid("Lapis Lazuli Ore", 21);
	public static final BlockMaterial lapisBlock = new Solid("Lapis Lazuli Block", 22);
	public static final BlockMaterial dispenser = new Solid("Dispenser", 23);
	public static final BlockMaterial sandstone = new Solid("Sandstone", 24);
	public static final BlockMaterial noteblock = new Solid("Note Block", 25);
	public static final BlockMaterial bedBlock = new Solid("Bed", 26);
	public static final BlockMaterial poweredRail = new Solid("Powered Rail", 27);
	public static final BlockMaterial detectorRail = new Solid("Detector Rail", 28);
	public static final BlockMaterial pistonStickyBase = new Solid("Sticky Piston", 29);
	public static final BlockMaterial web = new Solid("Cobweb", 30);
	public static final BlockMaterial deadShrub = new LongGrass("Dead Grass", 31, 0);
	public static final BlockMaterial tallGrass = new LongGrass("Tall Grass", 31, 1);
	public static final BlockMaterial fern = new LongGrass("Fern", 31, 2);
	public static final BlockMaterial deadBush = new LongGrass("Dead Shrubs", 32, 0);
	public static final BlockMaterial pistonBase = new Solid("Piston", 33);
	public static final BlockMaterial pistonExtension = new Solid("Piston (Head)", 34);
	public static final BlockMaterial whiteWool = new Wool("Wool", 35,0);
	public static final BlockMaterial orangeWool = new Wool("Orange Wool", 35,1);
	public static final BlockMaterial magentaWool = new Wool("Magenta Wool", 35,2);
	public static final BlockMaterial lightBlueWool = new Wool("Light Blue Wool", 35,3);
	public static final BlockMaterial yellowWool = new Wool("Yellow Wool", 35,4);
	public static final BlockMaterial limeWool = new Wool("Light Green Wool", 35,5);
	public static final BlockMaterial pinkWool = new Wool("Pink Wool", 35,6);
	public static final BlockMaterial greyWool = new Wool("Grey Wool", 35,7);
	public static final BlockMaterial lightGreyWool = new Wool("Light Grey Wool", 35,8);
	public static final BlockMaterial cyanWool = new Wool("Cyan Wool", 35,9);
	public static final BlockMaterial purpleWool = new Wool("Purple Wool", 35,10);
	public static final BlockMaterial blueWool = new Wool("Blue Wool", 35,11);
	public static final BlockMaterial brownWool = new Wool("Brown Wool", 35,12);
	public static final BlockMaterial greenWool = new Wool("Dark Green Wool", 35,13);
	public static final BlockMaterial redWool = new Wool("Red Wool", 35,14);
	public static final BlockMaterial blackWool = new Wool("Black Wool", 35,15);
	public static final BlockMaterial movedByPiston = new Solid("Moved By Piston", 36);
	public static final BlockMaterial dandelion = new Solid("Dandelion", 37);
	public static final BlockMaterial rose = new Solid("Rose", 38);
	public static final BlockMaterial brownMushroom = new Solid("Brown Mushroom", 39);
	public static final BlockMaterial redMushroom = new Solid("Red Mushroom", 40);
	public static final BlockMaterial goldBlock = new Solid("Gold Block", 41);
	public static final BlockMaterial ironBlock = new Solid("Iron Block", 42);
	public static final BlockMaterial stoneDoubleSlabs = new DoubleSlabs("Stone Double Slab", 43,0);
	public static final BlockMaterial sandstoneDoubleSlabs = new DoubleSlabs("Sandstone Double Slab", 43,1);
	public static final BlockMaterial woodenDoubleSlabs = new DoubleSlabs("Wooden Double Slab", 43,2);
	public static final BlockMaterial cobblestoneDoubleSlabs = new DoubleSlabs("Stone Double Slab", 43,3);
	public static final BlockMaterial brickDoubleSlabs = new DoubleSlabs("Brick Double Slab", 43,4);
	public static final BlockMaterial stoneBrickDoubleSlabs = new DoubleSlabs("Stone Brick Double Slab", 43,5);
	public static final BlockMaterial stoneSlab = new Slab("Stone Slab", 44,0);
	public static final BlockMaterial sandstoneSlab = new Slab("Sandstone Slab", 44,1);
	public static final BlockMaterial woodenSlab = new Slab("Wooden Slab", 44,2);
	public static final BlockMaterial cobblestoneSlab = new Slab("Stone Slab", 44,3);
	public static final BlockMaterial brickSlab = new Slab("Brick Slab", 44,4);
	public static final BlockMaterial stoneBrickSlab = new Slab("Stone Brick Slab", 44,5);
	public static final BlockMaterial brick = new Solid("Brick Block", 45);
	public static final BlockMaterial tnt = new Solid("TNT", 46);
	public static final BlockMaterial bookshelf = new Solid("Bookshelf", 47);
	public static final BlockMaterial mossStone = new Solid("Moss Stone", 48);
	public static final BlockMaterial obsidian = new Solid("Obsidian", 49);
	public static final BlockMaterial torch = new Solid("Torch", 50);
	public static final BlockMaterial fire = new Solid("Fire", 51);
	public static final BlockMaterial monsterSpawner = new Solid("Monster Spawner", 52);
	public static final BlockMaterial woodenStairs = new Solid("Wooden Stairs", 53);
	public static final BlockMaterial chest = new Solid("Chest", 54);
	public static final BlockMaterial redstoneWire = new Solid("Redstone Wire", 55);
	public static final BlockMaterial diamondOre = new Solid("Diamond Ore", 56);
	public static final BlockMaterial diamondBlock = new Solid("Diamond Block", 57);
	public static final BlockMaterial craftingTable = new Solid("Crafting Table", 58);
	public static final BlockMaterial crops = new Solid("Seeds", 59);
	public static final BlockMaterial farmland = new Solid("Farmland", 60);
	public static final BlockMaterial furnace = new Solid("Furance", 61);
	public static final BlockMaterial burningfurnace = new Solid("Burning Furnace", 62);
	public static final BlockMaterial signPost = new Solid("Sign Post", 63);
	public static final BlockMaterial woodenDoorBlock = new Solid("Wooden Door", 64);
	public static final BlockMaterial ladders = new Solid("Ladders", 65);
	public static final BlockMaterial rails = new Solid("Rails", 66);
	public static final BlockMaterial cobblestoneStairs = new Solid("Cobblestone Stairs", 67);
	public static final BlockMaterial wallSign = new Solid("Wall Sign", 68);
	public static final BlockMaterial lever = new Solid("Lever", 69);
	public static final BlockMaterial stonePressurePlate = new Solid("Stone Pressure Plate", 70);
	public static final BlockMaterial ironDoorBlock = new Solid("Iron Door", 71);
	public static final BlockMaterial woodenPressurePlate = new Solid("Wooden Pressure Plate", 72);
	public static final BlockMaterial redstoneOre = new Solid("Redstone Ore", 73);
	public static final BlockMaterial glowingRedstoneOre = new Solid("Glowing Redstone Ore", 74);
	public static final BlockMaterial redstoneTorchOff = new Solid("Redstone Torch", 75);
	public static final BlockMaterial redstoneTorchOn = new Solid("Redstone Torch (On)", 76);
	public static final BlockMaterial stoneButton = new Solid("Stone Button", 77);
	public static final BlockMaterial snow = new Solid("Snow", 78);
	public static final BlockMaterial ice = new Solid("Ice", 79);
	public static final BlockMaterial snowBlock = new Solid("Snow Block", 80);
	public static final BlockMaterial cactus = new Solid("Cactus", 81);
	public static final BlockMaterial clayBlock = new Solid("Clay Block", 82);
	public static final BlockMaterial sugarCaneBlock = new Solid("Sugar Cane", 83);
	public static final BlockMaterial jukebox = new Solid("Jukebox", 84);
	public static final BlockMaterial fence = new Solid("Fence", 85);
	public static final BlockMaterial pumpkin = new Solid("Pumpkin", 86);
	public static final BlockMaterial netherrack = new Solid("Netherrack", 87);
	public static final BlockMaterial soulSand = new Solid("Soul Sand", 88);
	public static final BlockMaterial glowstoneBlock = new Solid("Glowstone Block", 89);
	public static final BlockMaterial portal = new Solid("Portal", 90);
	public static final BlockMaterial jackOLantern = new Solid("Jack 'o' Lantern", 91);
	public static final BlockMaterial cakeBlock = new Solid("Cake Block", 92);
	public static final BlockMaterial redstoneRepeaterOff = new Solid("Redstone Repeater", 93);
	public static final BlockMaterial redstoneRepeaterOn = new Solid("Redstone Repeater (On)", 94);
	public static final BlockMaterial lockedChest = new Solid("Locked Chest", 95);
	public static final BlockMaterial trapdoor = new Solid("Trapdoor", 96);
	public static final BlockMaterial silverfishStone = new Solid("Silverfish Stone", 97);
	public static final BlockMaterial stoneBricks = new StoneBricks("Stone Brick", 98, 0);
	public static final BlockMaterial mossyStoneBricks = new StoneBricks("Mossy Stone Brick", 98, 1);
	public static final BlockMaterial crackedStoneBricks = new StoneBricks("Cracked Stone Brick", 98, 2);
	public static final BlockMaterial hugeBrownMushroom = new Solid("Huge Brown Mushroom", 99);
	public static final BlockMaterial hugeRedMushroom = new Solid("Huge Red Mushroom", 100);
	public static final BlockMaterial ironBars = new Solid("Iron Bars", 101);
	public static final BlockMaterial glassPane = new Solid("Glass Pane", 102);
	public static final BlockMaterial watermelon = new Solid("Watermelon", 103);
	public static final BlockMaterial pumpkinStem = new Solid("Pumpkin Stem", 104);
	public static final BlockMaterial melonStem = new Solid("Melon Stem", 105);
	public static final BlockMaterial vines = new Solid("Vines", 106);
	public static final BlockMaterial fenceGate = new Solid("Fence Gate", 107);
	public static final BlockMaterial brickStairs = new Solid("Brick Stairs", 108);
	public static final BlockMaterial stoneBrickStairs = new Solid("Stone Brick Stairs", 109);
	public static final BlockMaterial mycelium = new Solid("Mycelium", 110);
	public static final BlockMaterial lilyPad = new Solid("Lily Pad", 111);
	public static final BlockMaterial netherBrick = new Solid("Nether Brick", 112);
	public static final BlockMaterial netherBrickFence = new Solid("Nether Brick Fence", 113);
	public static final BlockMaterial netherBrickStairs = new Solid("Nether Brick Stairs", 114);
	public static final BlockMaterial netherWartBlock = new Solid("Nether Wart", 115);
	public static final BlockMaterial enchantmentTable = new Solid("Enchantment Table", 116);
	public static final BlockMaterial brewingStandBlock = new Solid("Brewing Stand", 117);
	public static final BlockMaterial cauldronBlock = new Solid("Cauldron", 118);
	public static final BlockMaterial endPortal = new Solid("End Portal", 119);
	public static final BlockMaterial endPortalFrame = new Solid("End Portal Frame", 120);
	public static final BlockMaterial endStone = new Solid("End Stone", 121);
	public static final BlockMaterial dragonEgg = new Solid("Dragon Egg", 122);
	
	public static final ItemMaterial ironShovel = new GenericTool("Iron Shovel", 256);
	public static final ItemMaterial ironPickaxe = new GenericTool("Iron Pickaxe", 257);
	public static final ItemMaterial ironAxe = new GenericTool("Iron Axe", 258);
	public static final ItemMaterial flintAndSteel = new GenericTool("Flint and Steel", 259);
	public static final ItemMaterial redApple = new GenericFood("Apple", 260, 4);
	public static final ItemMaterial bow = new GenericWeapon("Bow", 261);
	public static final ItemMaterial arrow = new GenericItemMaterial("Arrow", 262);
	public static final ItemMaterial coal = new Coal("Coal", 263,0);
	public static final ItemMaterial charcoal = new Coal("Charcoal", 263,1);
	public static final ItemMaterial diamond = new GenericItemMaterial("Diamond", 264);
	public static final ItemMaterial ironIngot = new GenericItemMaterial("Iron Ingot", 265);
	public static final ItemMaterial goldIngot = new GenericItemMaterial("Gold Ingot", 266);
	public static final ItemMaterial ironSword = new GenericWeapon("Iron Sword", 267);
	public static final ItemMaterial woodenSword = new GenericWeapon("Wooden Sword", 268);
	public static final ItemMaterial woodenShovel = new GenericTool("Wooden Shovel", 269);
	public static final ItemMaterial woodenPickaxe = new GenericTool("Wooden Pickaxe", 270);
	public static final ItemMaterial woodenAxe = new GenericTool("Wooden Axe", 271);
	public static final ItemMaterial stoneSword = new GenericWeapon("Stone Sword", 272);
	public static final ItemMaterial stoneShovel = new GenericTool("Stone Shovel", 273);
	public static final ItemMaterial stonePickaxe = new GenericTool("Stone Pickaxe", 274);
	public static final ItemMaterial stoneAxe = new GenericTool("Stone Axe", 275);
	public static final ItemMaterial diamondSword = new GenericWeapon("Diamond Sword", 276);
	public static final ItemMaterial diamondShovel = new GenericTool("Diamond Shovel", 277);
	public static final ItemMaterial diamondPickaxe = new GenericTool("Diamond Pickaxe", 278);
	public static final ItemMaterial diamondAxe = new GenericTool("Diamond Axe", 279);
	public static final ItemMaterial stick = new GenericItemMaterial("Stick", 280);
	public static final ItemMaterial bowl = new GenericItemMaterial("Bowl", 281);
	public static final ItemMaterial mushroomSoup = new GenericFood("Mushroom Soup", 282, 8);
	public static final ItemMaterial goldSword = new GenericWeapon("Gold Sword", 283);
	public static final ItemMaterial goldShovel = new GenericTool("Gold Shovel", 284);
	public static final ItemMaterial goldPickaxe = new GenericTool("Gold Pickaxe", 285);
	public static final ItemMaterial goldAxe = new GenericTool("Gold Axe", 286);
	public static final ItemMaterial string = new GenericItemMaterial("String", 287);
	public static final ItemMaterial feather = new GenericItemMaterial("Feather", 288);
	public static final ItemMaterial gunpowder = new GenericItemMaterial("Gunpowder", 289);
	public static final ItemMaterial woodenHoe = new GenericTool("Wooden Hoe", 290);
	public static final ItemMaterial stoneHoe = new GenericTool("Stone Hoe", 291);
	public static final ItemMaterial ironHoe = new GenericTool("Iron Hoe", 292);
	public static final ItemMaterial diamondHoe = new GenericTool("Diamond Hoe", 293);
	public static final ItemMaterial goldHoe = new GenericTool("Gold Hoe", 294);
	public static final ItemMaterial seeds = new GenericItemMaterial("Seeds", 295);
	public static final ItemMaterial wheat = new GenericItemMaterial("Wheat", 296);
	public static final ItemMaterial bread = new GenericFood("Bread", 297, 5);
	public static final ItemMaterial leatherCap = new GenericArmor("Leather Cap", 298);
	public static final ItemMaterial leatherTunic = new GenericArmor("Leather Tunic", 299);
	public static final ItemMaterial leatherPants = new GenericArmor("Leather Pants", 300);
	public static final ItemMaterial leatherBoots = new GenericArmor("Leather Boots", 301);
	public static final ItemMaterial chainHelmet = new GenericArmor("Chain Helmet", 302);
	public static final ItemMaterial chainChestplate = new GenericArmor("Chain Chestplate", 303);
	public static final ItemMaterial chainLeggings = new GenericArmor("Chain Leggings", 304);
	public static final ItemMaterial chainBoots = new GenericArmor("Chain Boots", 305);
	public static final ItemMaterial ironHelmet = new GenericArmor("Iron Helmet", 306);
	public static final ItemMaterial ironChestplate = new GenericArmor("Iron Chestplate", 307);
	public static final ItemMaterial ironLeggings = new GenericArmor("Iron Leggings", 308);
	public static final ItemMaterial ironBoots = new GenericArmor("Iron Boots", 309);
	public static final ItemMaterial diamondHelmet = new GenericArmor("Diamond Helmet", 310);
	public static final ItemMaterial diamondChestplate = new GenericArmor("Diamond Chestplate", 311);
	public static final ItemMaterial diamondLeggings = new GenericArmor("Diamond Leggings", 312);
	public static final ItemMaterial diamondBoots = new GenericArmor("Diamond Boots", 313);
	public static final ItemMaterial goldHelmet = new GenericArmor("Gold Helmet", 314);
	public static final ItemMaterial goldChestplate = new GenericArmor("Gold Chestplate", 315);
	public static final ItemMaterial goldLeggings = new GenericArmor("Gold Leggings", 316);
	public static final ItemMaterial goldBoots = new GenericArmor("Gold Boots", 317);
	public static final ItemMaterial flint = new GenericItemMaterial("Flint", 318, 0, true);
	public static final ItemMaterial rawPorkchop = new GenericFood("Raw Porkchop", 319, 3);
	public static final ItemMaterial cookedPorkchop = new GenericFood("Cooked Porkchop", 320, 8);
	public static final ItemMaterial paintings = new GenericItemMaterial("Paintings", 321);
	public static final ItemMaterial goldenApple = new GenericFood("Golden Apple", 322, 10);
	public static final ItemMaterial sign = new GenericItemMaterial("Sign", 323);
	public static final ItemMaterial woodenDoor = new GenericItemMaterial("Wooden Door", 324);
	public static final ItemMaterial bucket = new GenericItemMaterial("Bucket", 325);
	public static final ItemMaterial waterBucket = new GenericItemMaterial("Water Bucket", 326);
	public static final ItemMaterial lavaBucket = new GenericItemMaterial("Lava Bucket", 327);
	public static final ItemMaterial minecart = new GenericItemMaterial("Minecart", 328);
	public static final ItemMaterial saddle = new GenericItemMaterial("Saddle", 329);
	public static final ItemMaterial ironDoor = new GenericItemMaterial("Iron Door", 330);
	public static final ItemMaterial redstone = new GenericItemMaterial("Redstone", 331);
	public static final ItemMaterial snowball = new GenericItemMaterial("Snowball", 332);
	public static final ItemMaterial boat = new GenericItemMaterial("Boat", 333);
	public static final ItemMaterial leather = new GenericItemMaterial("Leather", 334);
	public static final ItemMaterial milk = new GenericItemMaterial("Milk", 335);
	public static final ItemMaterial clayBrick = new GenericItemMaterial("Brick", 336);
	public static final ItemMaterial clay = new GenericItemMaterial("Clay", 337);
	public static final ItemMaterial sugarCane = new GenericItemMaterial("Sugar Cane", 338);
	public static final ItemMaterial paper = new GenericItemMaterial("Paper", 339);
	public static final ItemMaterial book = new GenericItemMaterial("Book", 340);
	public static final ItemMaterial slimeball = new GenericItemMaterial("Slimeball", 341);
	public static final ItemMaterial minecartChest = new GenericItemMaterial("Minecart with Chest", 342);
	public static final ItemMaterial minecartFurnace = new GenericItemMaterial("Minecart with Furnace", 343);
	public static final ItemMaterial egg = new GenericItemMaterial("Egg", 344);
	public static final ItemMaterial compass = new GenericItemMaterial("Compass", 345);
	public static final ItemMaterial fishingRod = new GenericTool("Fishing Rod", 346);
	public static final ItemMaterial clock = new GenericItemMaterial("Clock", 347);
	public static final ItemMaterial glowstoneDust = new GenericItemMaterial("Glowstone Dust", 348);
	public static final ItemMaterial rawFish = new GenericFood("Raw Fish", 349, 2);
	public static final ItemMaterial cookedFish = new GenericFood("Cooked Fish", 350, 5);
	public static final ItemMaterial inkSac = new Dye("Ink Sac", 351,0);
	public static final ItemMaterial roseRed = new Dye("Rose Red", 351,1);
	public static final ItemMaterial cactusGreen = new Dye("Cactus Green", 351,2);
	public static final ItemMaterial cocoaBeans = new Dye("Cocoa Beans", 351,3);
	public static final ItemMaterial lapisLazuli = new Dye("Lapis Lazuli", 351,4);
	public static final ItemMaterial purpleDye = new Dye("Purple Dye", 351,5);
	public static final ItemMaterial cyanDye = new Dye("Cyan Dye", 351,6);
	public static final ItemMaterial lightGrayDye = new Dye("Light Gray Dye", 351,7);
	public static final ItemMaterial grayDye = new Dye("Gray Dye", 351,8);
	public static final ItemMaterial pinkDye = new Dye("Pink Dye", 351,9);
	public static final ItemMaterial limeDye = new Dye("Lime Dye", 351,10);
	public static final ItemMaterial dandelionYellow = new Dye("Dandelion Yellow", 351,11);
	public static final ItemMaterial lightBlueDye = new Dye("Light Blue Dye", 351,12);
	public static final ItemMaterial magentaDye = new Dye("Magenta Dye", 351,13);
	public static final ItemMaterial orangeDye = new Dye("Orange Dye", 351,14);
	public static final ItemMaterial boneMeal = new Dye("Bone Meal", 351,15);
	public static final ItemMaterial bone = new GenericItemMaterial("Bone", 352);
	public static final ItemMaterial sugar = new GenericItemMaterial("Sugar", 353);
	public static final ItemMaterial cake = new GenericItemMaterial("Cake", 354);
	public static final ItemMaterial bed = new GenericItemMaterial("Bed", 355);
	public static final ItemMaterial redstoneRepeater = new GenericItemMaterial("Redstone Repeater", 356);
	public static final ItemMaterial cookie = new GenericFood("Cookie", 357, 1);
	public static final ItemMaterial map = new GenericItemMaterial("Map", 358);
	public static final ItemMaterial shears = new GenericTool("Shears", 359);
	public static final ItemMaterial melonSlice = new GenericFood("Melon Slice", 360, 2);
	public static final ItemMaterial pumpkinSeeds = new GenericItemMaterial("Pumpkin Seeds", 361);
	public static final ItemMaterial melonSeeds = new GenericItemMaterial("Melon Seeds", 362);
	public static final ItemMaterial rawBeef = new GenericFood("Raw Beef", 363, 3);
	public static final ItemMaterial steak = new GenericFood("Steak", 364, 8);
	public static final ItemMaterial rawChicken = new GenericFood("Raw Chicken", 365, 2);
	public static final ItemMaterial cookedChicken = new GenericFood("Cooked Chicken", 366, 6);
	public static final ItemMaterial rottenFlesh = new GenericFood("Rotten Flesh", 367, 4);
	public static final ItemMaterial enderPearl = new GenericItemMaterial("Ender Pearl", 368);
	public static final ItemMaterial blazeRod = new GenericItemMaterial("Blaze Rod", 369);
	public static final ItemMaterial ghastTear = new GenericItemMaterial("Ghast Tear", 370);
	public static final ItemMaterial goldNugget = new GenericItemMaterial("Gold Nugget", 371);
	public static final ItemMaterial netherWart = new GenericItemMaterial("Nether Wart", 372);
	public static final ItemMaterial potion = new GenericItemMaterial("Potion", 373);
	public static final ItemMaterial glassBottle = new GenericItemMaterial("Glass Bottle", 374);
	public static final ItemMaterial spiderEye = new GenericFood("Spider Eye", 375, 2);
	public static final ItemMaterial fermentedSpiderEye = new GenericItemMaterial("Fermented Spider Eye", 376);
	public static final ItemMaterial blazePowder = new GenericItemMaterial("Blaze Powder", 377);
	public static final ItemMaterial magmaCream = new GenericItemMaterial("Magma Cream", 378);
	public static final ItemMaterial brewingStand = new GenericItemMaterial("Brewing Stand", 379);
	public static final ItemMaterial cauldron = new GenericItemMaterial("Cauldron", 380);
	public static final ItemMaterial eyeOfEnder = new GenericItemMaterial("Eye of Ender", 381);

	public static final ItemMaterial goldMusicDisc = new GenericItemMaterial("Music Disc", 2256);
	public static final ItemMaterial greenMusicDisc = new GenericItemMaterial("Music Disc", 2257);
	public static final ItemMaterial orangeMusicDisc = new GenericItemMaterial("Music Disc", 2258);
	public static final ItemMaterial redMusicDisc = new GenericItemMaterial("Music Disc", 2259);
	public static final ItemMaterial cyanMusicDisc = new GenericItemMaterial("Music Disc", 2260);
	public static final ItemMaterial blueMusicDisc = new GenericItemMaterial("Music Disc", 2261);
	public static final ItemMaterial purpleMusicDisc = new GenericItemMaterial("Music Disc", 2262);
	public static final ItemMaterial blackMusicDisc = new GenericItemMaterial("Music Disc", 2263);
	public static final ItemMaterial whiteMusicDisc = new GenericItemMaterial("Music Disc", 2264);
	public static final ItemMaterial forestGreenMusicDisc = new GenericItemMaterial("Music Disc", 2265);
	public static final ItemMaterial brokenMusicDisc = new GenericItemMaterial("Music Disc", 2266);
	
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
	public static void addCustomBlock(CustomBlockMaterial block) {
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
	public static BlockMaterial getBlock(int id) {
		return getBlock(id, (short)0);
	}
	
	/**
	 * Gets the block at the given id and data, or null if none found
	 * @param id to get
	 * @param data to get
	 * @return block, or null if none found
	 */
	public static BlockMaterial getBlock(int id, short data) {
		Material mat = getMaterial(id, data);
		if (mat instanceof BlockMaterial) {
			return (BlockMaterial)mat;
		}
		return null;
	}

	/**
	 * Gets an array of all currently registered custom blocks
	 * @return all registered custom blocks
	 */
	public static CustomBlockMaterial[] getCustomBlocks() {
		CustomBlockMaterial[] blocks = new CustomBlockMaterial[customBlocks.size()];
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
	public static CustomBlockMaterial getCustomBlock(int customId) {
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
	public static ItemMaterial getItem(int id) {
		return getItem(id, (short)0);
	}
	
	/**
	 * Gets the item at the given id and data, or null if none found
	 * @param id to get
	 * @param data to get
	 * @return item or null if none found
	 */
	public static ItemMaterial getItem(int id, short data) {
		Material mat = getMaterial(id, data);
		if (mat instanceof ItemMaterial) {
			return (ItemMaterial)mat;
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
