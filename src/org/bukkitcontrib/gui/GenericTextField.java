package org.bukkitcontrib.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkitcontrib.packet.PacketUtil;

public class GenericTextField extends GenericControl implements TextField{
	
	protected String text = "";
	protected int cursor = 0;
	protected int maxChars = 16;
	protected int fieldColor = -16777216;
	protected int borderColor = -6250336;
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

	@Override
	public void render() {
		
	}

}
