package org.getspout.commons.material.block;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.getspout.commons.Spoutcraft;
import org.getspout.commons.World;
import org.getspout.commons.addon.Addon;
import org.getspout.commons.block.BlockFace;
import org.getspout.commons.block.design.BlockDesign;
import org.getspout.commons.block.design.GenericBlockDesign;
import org.getspout.commons.entity.Entity;
import org.getspout.commons.entity.LivingEntity;
import org.getspout.commons.entity.Player;
import org.getspout.commons.inventory.ItemStack;
import org.getspout.commons.material.BlockMaterial;
import org.getspout.commons.material.CustomBlockMaterial;
import org.getspout.commons.material.CustomItem;
import org.getspout.commons.material.MaterialData;
import org.getspout.commons.material.item.GenericCustomItemMaterial;
import org.getspout.commons.packet.PacketUtil;

public class GenericCustomBlockMaterial implements CustomBlockMaterial {
	public BlockDesign design = new GenericBlockDesign();
	private ItemStack drop = null;
	private String name;
	private String fullName;
	private int customId;
	private Addon addon;
	private CustomItem item;
	private int blockId;
	private boolean opaque;
	private float hardness = 1.5F;
	private float friction = 0.6F;
	private int lightLevel = 0;
	
	/**
	 * Creates a GenericCustomBlock with no values, used for serialization purposes only.
	 */
	public GenericCustomBlockMaterial() {
		
	}

	/**
	 * Creates a GenericCustomBlock with no model yet.
	 * 
	 * @param addon creating the block
	 * @param name of the block
	 * @param isOpaque true if you want the block solid
	 */
	public GenericCustomBlockMaterial(Addon addon, String name, boolean isOpaque) {
		this(addon, name, isOpaque, new GenericCustomItemMaterial(addon, name));
	}
	
	/**
	 * Creates a GenericCustomBlock with a specified Design and metadata
	 * 
	 * @param addon creating the block
	 * @param name of the block
	 * @param isOpaque true if you want the block solid
	 * @param item to use for the block
	 */
	public GenericCustomBlockMaterial(Addon addon, String name, boolean isOpaque, CustomItem item) {
		opaque = isOpaque;
		this.blockId = isOpaque ? 1 :20;
		this.addon = addon;
		this.item = item;
		this.name = item.getName();
		this.fullName = item.getFullName();
		this.customId = item.getCustomId();
		MaterialData.addCustomBlock(this);
		this.setItemDrop(new ItemStack(this, 1));
	}

	/**
	 * Creates a GenericCustomBlock with a specified Design and metadata
	 * 
	 * @param addon creating the block
	 * @param name of the block
	 * @param isOpaque true if you want the block solid
	 * @param design to use for the block
	 */
	public GenericCustomBlockMaterial(Addon addon, String name, boolean isOpaque, BlockDesign design) {
		this(addon, name, isOpaque);
		setBlockDesign(design);
	}
	
	/**
	 * Creates a basic GenericCustomblock with no design that is opaque/solid.
	 * 
	 * @param plugin creating the block
	 * @param name of the block
	 */
	public GenericCustomBlockMaterial(Addon addon, String name) {
		this(addon, name, true);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		if (item != null) {
			item.setName(name);
		}
	}

	public BlockDesign getBlockDesign() {
		return design;
	}

	public CustomBlockMaterial setBlockDesign(BlockDesign design) {
		this.design = design;
		return this;
	}
	
	public boolean isOpaque() {
		return opaque;
	}
	
	public BlockMaterial setOpaque(boolean opaque) {
		this.opaque = opaque;
		return this;
	}

	public boolean hasSubtypes() {
		return true;
	}

	public int getCustomId() {
		return customId;
	}

	public String getFullName() {
		return fullName;
	}
	
	public String getNotchianName() {
		return getName();
	}

	public Addon getAddon() {
		return addon;
	}

	public CustomItem getBlockItem() {
		return item;
	}
	
	public int getRawId() {
		return this.item.getRawId();
	}
	
	public int getRawData() {
		return this.item.getCustomId();
	}
	
	public int getBlockId() {
		return this.blockId;
	}
	
	public ItemStack getItemDrop() {
		return drop.clone();
	}
	
	public CustomBlockMaterial setItemDrop(ItemStack item) {
		drop = item != null ? item.clone() : null;
		return this;
	}
	
	public float getHardness() {
		return hardness;
	}
	
	public CustomBlockMaterial setHardness(float hardness) {
		this.hardness = hardness;
		return this;
	}
	
	public float getFriction() {
		return friction;
	}
	
	public CustomBlockMaterial setFriction(float friction) {
		this.friction = friction;
		return this;
	}
	
	public int getLightLevel() {
		return lightLevel;
	}
	
	public CustomBlockMaterial setLightLevel(int level) {
		lightLevel = level;
		return this;
	}

	public void onNeighborBlockChange(World world, int x, int y, int z, int changedId) {
	}

	public void onBlockPlace(World world, int x, int y, int z) {
	}

	public void onBlockPlace(World world, int x, int y, int z, LivingEntity living) {
	}

	public void onBlockDestroyed(World world, int x, int y, int z) {
	}

	public void onBlockDestroyed(World world, int x, int y, int z, LivingEntity living) {
	}

	public boolean onBlockInteract(World world, int x, int y, int z, Player player) {
		return false;
	}

	public void onEntityMoveAt(World world, int x, int y, int z, Entity entity) {
	}

	public void onBlockClicked(World world, int x, int y, int z, Player player) {		
	}

	public boolean isProvidingPowerTo(World world, int x, int y, int z,	BlockFace face) {
		return false;
	}

	public boolean isIndirectlyProvidingPowerTo(World world, int x, int y, int z, BlockFace face) {
		return false;
	}

	public int getNumBytes() {
		return 4 + PacketUtil.getNumBytes(getName()) + PacketUtil.getNumBytes(getAddon().getDescription().getName()) + 1 + 4 + 4 + 4;
	}

	public void readData(DataInputStream input) throws IOException {
		customId = input.readInt();
		System.out.println("Reading Block: " + customId);
		setName(PacketUtil.readString(input));
		String addonName = PacketUtil.readString(input);
		//System.out.println("Block: " + getName()  + " Id: " + customId + " Addon: " + addonName);
		addon = Spoutcraft.getAddonManager().getOrCreateAddon(addonName);
		fullName = addon.getDescription().getFullName() + "." + getName();
		opaque = input.readBoolean();
		setFriction(input.readFloat());
		setHardness(input.readFloat());
		setLightLevel(input.readInt());
		item = new GenericCustomItemMaterial(addon, name, customId);
		MaterialData.addCustomBlock(this);
		this.setItemDrop(new ItemStack(this, 1));
		this.blockId = isOpaque() ? 1 :20;
	}

	public void writeData(DataOutputStream output) throws IOException {
		output.write(customId);
		PacketUtil.writeString(output, getName());
		PacketUtil.writeString(output, getAddon().getDescription().getName());
		output.writeBoolean(isOpaque());
		output.writeFloat(getFriction());
		output.writeFloat(getHardness());
		output.writeInt(getLightLevel());
	}

	public int getVersion() {
		return 0;
	}
}
