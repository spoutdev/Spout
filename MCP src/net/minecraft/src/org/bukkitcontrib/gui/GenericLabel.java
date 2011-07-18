package org.bukkitcontrib.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.lwjgl.opengl.GL11;
import org.bukkitcontrib.packet.*;

public class GenericLabel extends GenericWidget implements Label{
	protected String text = "";
	public GenericLabel(){
		
	}
	
	public GenericLabel(String text) {
		this.text = text;
	}
	
	@Override
	public WidgetType getType() {
		return WidgetType.Label;
	}
	
	@Override
	public int getNumBytes() {
		return super.getNumBytes() + getText().length();
	}
	
	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		this.setText(PacketUtil.readString(input));
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		PacketUtil.writeString(output, getText());
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public Label setText(String text) {
		this.text = text;
		return this;
	}
	
	public void render() {
		//GL11.glEnable(GL11.GL_TEXTURE_2D);
		net.minecraft.src.BukkitContrib.getGameInstance().ingameGUI.drawString(net.minecraft.src.BukkitContrib.getGameInstance().fontRenderer, getText(), getUpperRightX(), getUpperRightY(), 0xffffff);
		//GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
}
