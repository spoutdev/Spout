package org.getspout.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.lwjgl.opengl.GL11;
import org.getspout.packet.PacketUtil;
import net.minecraft.src.BukkitContrib;
import net.minecraft.src.FontRenderer;

public class GenericLabel extends GenericWidget implements Label{
	protected String text = "";
	protected boolean center = false;
	protected int hexColor = 0x000000;
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
		return super.getNumBytes() + PacketUtil.getNumBytes(getText()) + 5;
	}
	
	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		this.setText(PacketUtil.readString(input));
		this.setCentered(input.readBoolean());
		this.setHexColor(input.readInt());
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		PacketUtil.writeString(output, getText());
		output.writeBoolean(isCentered());
		output.writeInt(getHexColor());
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

	@Override
	public boolean isCentered() {
		return center;
	}

	@Override
	public Label setCentered(boolean center) {
		this.center = center;
		return this;
	}

	@Override
	public int getHexColor() {
		return hexColor;
	}

	@Override
	public Label setHexColor(int hex) {
		hexColor = hex;
		return this;
	}
	
	public void render() {
		FontRenderer font = BukkitContrib.getGameInstance().fontRenderer;
		String lines[] = getText().split("\\n");
		for (int i = 0; i < lines.length; i++) {
			font.drawStringWithShadow(lines[i], getX() - (isCentered() ? font.getStringWidth(lines[i]) / 2 : 0), getY() - (i * 10), getHexColor());
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
