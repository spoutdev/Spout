package org.getspout.commons.material.item;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.getspout.commons.Spoutcraft;
import org.getspout.commons.addon.Addon;
import org.getspout.commons.block.Block;
import org.getspout.commons.block.BlockFace;
import org.getspout.commons.entity.Player;
import org.getspout.commons.material.CustomItem;
import org.getspout.commons.material.MaterialData;
import org.getspout.commons.packet.PacketUtil;
import org.getspout.commons.util.UniqueItemStringMap;

public class GenericCustomItemMaterial implements CustomItem {
	private String name;
	private String fullName;
	private Addon addon;
	private int customId;
	public String texture;
	
	/**
	 * Creates a GenericCustomItem with no values, used for serialization purposes only.
	 */
	public GenericCustomItemMaterial() {
		
	}

	public GenericCustomItemMaterial(Addon addon, String name, int customId) {
		this.name = name;
		this.fullName = addon.getDescription().getName() + name;
		this.customId = customId;
		this.addon = addon;
		this.setName(name);
		MaterialData.addCustomItem(this);
	}

	public GenericCustomItemMaterial(Addon addon, String name) {
		this(addon, name, UniqueItemStringMap.getId(addon.getDescription().getName() + name));
	}

	public GenericCustomItemMaterial(Addon addon, String name, String texture) {
		this(addon, name);
		this.setTexture(texture);
	}
	
	public int getRawId() {
		return 318; //flint
	}

	public int getRawData() {
		return customId;
	}

	public boolean hasSubtypes() {
		return true;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		this.fullName = addon.getDescription().getName() + name;
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

	public CustomItem setTexture(String texture) {
		this.texture = texture;
		return this;
	}

	public String getTexture() {
		return texture;
	}

	public boolean onItemInteract(Player player, Block block, BlockFace face) {
		return true;
	}

	public int getNumBytes() {
		return 4 + PacketUtil.getNumBytes(getName()) + PacketUtil.getNumBytes(getAddon().getDescription().getName()) + PacketUtil.getNumBytes(getTexture());
	}

	public void readData(DataInputStream input) throws IOException {
		customId = input.readInt();
		name = PacketUtil.readString(input);
		addon = Spoutcraft.getAddonManager().getOrCreateAddon(PacketUtil.readString(input));
		texture = PacketUtil.readString(input);
		setName(name);
		MaterialData.addCustomItem(this);
	}

	public void writeData(DataOutputStream output) throws IOException {
		output.write(customId);
		PacketUtil.writeString(output, getName());
		PacketUtil.writeString(output, getAddon().getDescription().getName());
		PacketUtil.writeString(output, getTexture());
	}

	public int getVersion() {
		return 0;
	}
}
