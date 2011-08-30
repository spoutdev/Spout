package org.getspout.spout.block.mcblock;

import net.minecraft.server.Block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.mcblock.MCBlock;

public class MCCraftBlock implements MCBlock{
	private int id;
	private short data;
	private Block parent;
	
	private boolean overridePowerSource = false;
	private boolean powerSource = false;
	
	
	protected MCCraftBlock(int id, short data, Block parent) {
		this.id = id;
		this.data = data;
		this.parent = parent;
	}
	

	@Override
	public String getName() {
		return SpoutManager.getItemManager().getItemName(Material.getMaterial(id), data);
	}

	@Override
	public void setName(String name) {
		SpoutManager.getItemManager().setItemName(Material.getMaterial(id), data, name);
	}

	@Override
	public boolean isPowerSource() {
		if (overridePowerSource) {
			return powerSource;
		}
		return parent.isPowerSource();
	}

	@Override
	public void setPowerSource(boolean power) {
		overridePowerSource = true;
		powerSource = power;
	}

	@Override
	public boolean isPowering(Location location) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPowering(Location location, boolean power) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isIndirectlyPowering(Location location) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setIndirectlyPowering(Location location, boolean power) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte getBrightness() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setBrightness(byte lightLevel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte getBrightness(Location location) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setBrightness(Location location, byte lightLevel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte getHardness() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setHardness(byte hardness) {
		// TODO Auto-generated method stub
		
	}

}
