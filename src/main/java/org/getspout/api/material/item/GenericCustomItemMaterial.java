package org.getspout.api.material.item;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.getspout.api.block.Block;
import org.getspout.api.block.BlockFace;
import org.getspout.api.entity.Player;
import org.getspout.api.inventory.ItemMap;
import org.getspout.api.material.CustomItemMaterial;
import org.getspout.api.material.MaterialData;
import org.getspout.api.packet.PacketUtil;
import org.getspout.api.plugin.Plugin;

public class GenericCustomItemMaterial implements CustomItemMaterial {
	private String name;
	private String fullName;
	private Plugin plugin;
	private int customId;
	public String texture;
	
	/**
	 * Creates a GenericCustomItem with no values, used for serialization purposes only.
	 */
	public GenericCustomItemMaterial() {
		
	}

	public GenericCustomItemMaterial(Plugin plugin, String name, int customId) {
		this.name = name;
		this.fullName = plugin.getDescription().getName() + name;
		this.customId = customId;
		this.plugin = plugin;
		this.setName(name);
		MaterialData.addCustomItem(this);
	}

	public GenericCustomItemMaterial(Plugin plugin, String name) {
		this(plugin, name, ItemMap.getRootMap().register(plugin.getDescription().getName() + name));
	}

	public GenericCustomItemMaterial(Plugin plugin, String name, String texture) {
		this(plugin, name);
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
		this.fullName = plugin.getDescription().getName() + name;
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

	public Plugin getPlugin() {
		return plugin;
	}

	public CustomItemMaterial setTexture(String texture) {
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
		return 4 + PacketUtil.getNumBytes(getName()) + PacketUtil.getNumBytes(getPlugin().getDescription().getName()) + PacketUtil.getNumBytes(getTexture());
	}

	public void readData(DataInputStream input) throws IOException {
		customId = input.readInt();
		name = PacketUtil.readString(input);
//		plugin = Spoutcraft.getPluginManager().getOrCreatePlugin(PacketUtil.readString(input));
		texture = PacketUtil.readString(input);
		setName(name);
		MaterialData.addCustomItem(this);
	}

	public void writeData(DataOutputStream output) throws IOException {
		output.write(customId);
		PacketUtil.writeString(output, getName());
		PacketUtil.writeString(output, getPlugin().getDescription().getName());
		PacketUtil.writeString(output, getTexture());
	}

	public int getVersion() {
		return 0;
	}
}
