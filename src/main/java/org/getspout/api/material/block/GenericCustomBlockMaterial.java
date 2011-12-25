package org.getspout.api.material.block;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.block.BlockFace;
import org.getspout.api.geo.World;
import org.getspout.api.material.BlockMaterial;
import org.getspout.api.material.CustomBlockMaterial;
import org.getspout.api.material.CustomItemMaterial;
import org.getspout.api.material.MaterialData;
import org.getspout.api.material.item.GenericCustomItemMaterial;
import org.getspout.api.entity.Entity;
import org.getspout.api.packet.PacketUtil;
import org.getspout.unchecked.api.inventory.ItemStack;
import org.getspout.api.plugin.Plugin;
import org.getspout.api.render.BlockDesign;
import org.getspout.api.render.GenericBlockDesign;

public class GenericCustomBlockMaterial implements CustomBlockMaterial {
	public BlockDesign design = new GenericBlockDesign();
	private ItemStack drop = null;
	private String name;
	private String fullName;
	private int customId;
	private Plugin addon;
	private CustomItemMaterial item;
	private int blockId;
	private boolean opaque;
	private float hardness = 1.5F;
	private float friction = 0.6F;
	private int lightLevel = 0;

	/**
	 * Creates a GenericCustomBlock with no values, used for serialization
	 * purposes only.
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
	public GenericCustomBlockMaterial(Plugin addon, String name, boolean isOpaque) {
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
	public GenericCustomBlockMaterial(Plugin addon, String name, boolean isOpaque, CustomItemMaterial item) {
		opaque = isOpaque;
		blockId = isOpaque ? 1 : 20;
		this.addon = addon;
		this.item = item;
		this.name = item.getName();
		fullName = item.getFullName();
		customId = item.getCustomId();
		MaterialData.addCustomBlock(this);
		setItemDrop(new ItemStack(this, 1));
	}

	/**
	 * Creates a GenericCustomBlock with a specified Design and metadata
	 *
	 * @param addon creating the block
	 * @param name of the block
	 * @param isOpaque true if you want the block solid
	 * @param design to use for the block
	 */
	public GenericCustomBlockMaterial(Plugin addon, String name, boolean isOpaque, BlockDesign design) {
		this(addon, name, isOpaque);
		setBlockDesign(design);
	}

	/**
	 * Creates a basic GenericCustomblock with no design that is opaque/solid.
	 *
	 * @param plugin creating the block
	 * @param name of the block
	 */
	public GenericCustomBlockMaterial(Plugin addon, String name) {
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

	public Plugin getAddon() {
		return addon;
	}

	public CustomItemMaterial getBlockItem() {
		return item;
	}

	public int getRawId() {
		return item.getRawId();
	}

	public int getRawData() {
		return item.getCustomId();
	}

	public int getBlockId() {
		return blockId;
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

	public void onBlockPlace(World world, int x, int y, int z, Entity living) {
	}

	public void onBlockDestroyed(World world, int x, int y, int z) {
	}

	public void onBlockDestroyed(World world, int x, int y, int z, Entity living) {
	}

	public boolean onBlockInteract(World world, int x, int y, int z, Entity player) {
		return false;
	}

	public void onEntityMoveAt(World world, int x, int y, int z, Entity entity) {
	}

	public void onBlockClicked(World world, int x, int y, int z, Entity player) {
	}

	public boolean isProvidingPowerTo(World world, int x, int y, int z, BlockFace face) {
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
		PacketUtil.readString(input);
		//System.out.println("Block: " + getName()  + " Id: " + customId + " Addon: " + addonName);
		//addon = Spoutcraft.getAddonManager().getOrCreateAddon(addonName);
		fullName = addon.getDescription().getFullName() + "." + getName();
		opaque = input.readBoolean();
		setFriction(input.readFloat());
		setHardness(input.readFloat());
		setLightLevel(input.readInt());
		item = new GenericCustomItemMaterial(addon, name, customId);
		MaterialData.addCustomBlock(this);
		setItemDrop(new ItemStack(this, 1));
		blockId = isOpaque() ? 1 : 20;
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
