package org.getspout.Spout.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.getspout.Spout.packet.PacketUtil;
import net.minecraft.src.BukkitContrib;
import net.minecraft.src.GuiButton;

public class GenericTextField extends GenericControl implements TextField{
	
	protected String text = "";
	protected int cursor = 0;
	protected int maxChars = 16;
	protected int fieldColor = -16777216;
	protected int borderColor = -6250336;
	CustomTextField field = null;
	public GenericTextField() {

	}
	
	@Override
	public int getNumBytes() {
		return super.getNumBytes() + 16 + PacketUtil.getNumBytes(text);
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		setCursorPosition(input.readInt());
		setFieldColor(input.readInt());
		setBorderColor(input.readInt());
		setMaximumCharacters(input.readInt());
		setText(PacketUtil.readString(input));
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		output.writeInt(getCursorPosition());
		output.writeInt(getFieldColor());
		output.writeInt(getBorderColor());
		output.writeInt(getMaximumCharacters());
		PacketUtil.writeString(output, getText());
	}

	@Override
	public int getCursorPosition() {
		return cursor;
	}

	@Override
	public TextField setCursorPosition(int position) {
		this.cursor = position;
		return this;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public TextField setText(String text) {
		this.text = text;
		System.out.println(text + "  cool");
		return this;
	}
	
	@Override
	public int getMaximumCharacters() {
		return maxChars;
	}
	
	@Override
	public TextField setMaximumCharacters(int max) {
		this.maxChars = max;
		return this;
	}

	@Override
	public int getFieldColor() {
		return fieldColor;
	}

	@Override
	public TextField setFieldColor(int hex) {
		this.fieldColor = hex;
		return this;
	}

	@Override
	public int getBorderColor() {
		return borderColor;
	}

	@Override
	public TextField setBorderColor(int hex) {
		this.borderColor = hex;
		return this;
	}
	
	@Override
	public WidgetType getType() {
		return WidgetType.TextField;
	}
	
	protected void setup(int x, int y) {
		this.x = x;
		this.y = y;
	}
	private int x;
	private int y;

	@Override
	public void render() {
		if (field == null) {
			boolean success = false;
			if (BukkitContrib.getGameInstance().currentScreen instanceof CustomScreen) {
				CustomScreen popup = (CustomScreen)BukkitContrib.getGameInstance().currentScreen;
				for (GuiButton control : popup.getControlList()) {
					System.out.println(control);
					if (control instanceof CustomTextField) {
						if (((CustomTextField)control).isEqual((Widget)this)) {
							field = (CustomTextField)control;
							field.updateWidget(this);
							success = true;
							break;
						}
					}
				}
				if (!success) {
					field = new CustomTextField(getScreen(), this);
					popup.getControlList().add(field);
				}
			}
		}
		field.drawButton(BukkitContrib.getGameInstance(), x, y);
	}

}
