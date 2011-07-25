package org.bukkitcontrib.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.src.ItemStack;
import net.minecraft.src.BukkitContrib;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.Block;
import net.minecraft.src.EntityItem;
import net.minecraft.src.RenderManager;
import org.lwjgl.opengl.GL11;

public class GenericItemWidget extends GenericWidget implements ItemWidget{
	protected int material = -1;
	protected short data = -1;
	protected int depth = 8;
	protected final RenderItemCustom renderer;
	protected ItemStack toRender = null;
	
	public GenericItemWidget() {
		renderer = new RenderItemCustom();
		renderer.setRenderManager(RenderManager.instance);
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
	public void render() {
		if (toRender == null){
			if (getTypeId() > 0) {
				if (getData() > -1){
					toRender = new ItemStack(getTypeId(), 1, getData());
				}
				else {
					toRender = new ItemStack(getTypeId(), 1, 0);
				}
			}
		}
		if (toRender != null) {
			GL11.glDepthFunc(515);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_COLOR_MATERIAL);
			GL11.glDisable(GL11.GL_LIGHTING);
			RenderHelper.enableStandardItemLighting();
			double oldX = 1;
			double oldY = 1;
			double oldZ = 1;
			Block block = null;
			if (getTypeId() < 255) {
				block = Block.blocksList[getTypeId()];
				oldX = block.maxX;
				oldY = block.maxY;
				oldZ = block.maxZ;
				block.maxX = block.maxX * (getWidth() / 8);
				block.maxY = block.maxY * (getHeight() / 8);
				block.maxZ = block.maxZ * (getDepth() / 8);
			}
			else {
				renderer.setScale(1 + (getWidth() / 200D), 1 + (getHeight() / 200D), 1);
			}
			renderer.renderItemIntoGUI(BukkitContrib.getGameInstance().fontRenderer, BukkitContrib.getGameInstance().renderEngine, toRender, getX(), getY());
			if (getTypeId() < 255){
				block.maxX = oldX;
				block.maxY = oldY;
				block.maxZ = oldZ;
			}
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_LIGHTING);
			RenderHelper.disableStandardItemLighting();
		}
	}

}
