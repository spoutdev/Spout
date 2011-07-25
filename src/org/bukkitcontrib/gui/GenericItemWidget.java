package org.bukkitcontrib.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.inventory.ItemStack;

public class GenericItemWidget extends GenericWidget implements ItemWidget{
	protected int material = -1;
	protected short data = -1;
	protected int depth = 8;

	public GenericItemWidget() {
		
	}
	
	public GenericItemWidget(ItemStack item) {
		this.material = item.getTypeId();
		this.data = item.getDurability();
	}
	
	public int getNumBytes() {
		return super.getNumBytes() + 10;
	}
	
	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		this.setTypeId(input.readInt());
		this.setData(input.readShort());
		this.setDepth(input.readInt());
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		output.writeInt(getTypeId());
		output.writeShort(getData());
		output.writeInt(getDepth());
	}
	
	public ItemWidget setTypeId(int id) {
		this.material = id;
		return this;
	}
	
	public int getTypeId() {
		return material;
	}
	
	public ItemWidget setData(short data) {
		this.data = data;
		return this;
	}
	
	public short getData() {
		return data;
	}
	
	public ItemWidget setDepth(int depth) {
		this.depth = depth;
		return this;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public ItemWidget setHeight(int height) {
		super.setHeight(height);
		return this;
	}
	
	public ItemWidget setWidth(int width) {
		super.setWidth(width);
		return this;
	}

	@Override
	public WidgetType getType() {
		return WidgetType.ItemWidget;
	}

	@Override
	public void render() {}

}
