package org.bukkitcontrib.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class GenericWidget implements Widget{
	protected int upperRightX;
	protected int upperRightY;
	protected int width;
	protected int height;
	protected boolean visible = true;
	
	public GenericWidget(){
		
	}
	
	public GenericWidget(int upperRightX, int upperRightY, int width, int height) {
		this.upperRightX = upperRightX;
		this.upperRightY = upperRightY;
		this.width = width;
		this.height = height;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		upperRightX = input.readInt();
		upperRightY = input.readInt();
		width = input.readInt();
		height = input.readInt();
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		output.writeInt(upperRightX);
		output.writeInt(upperRightY);
		output.writeInt(width);
		output.writeInt(height);
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public Widget setWidth(int width) {
		this.width = width;
		return this;
	}

	@Override
	public int getHeight() {
		return height;
	}
	
	@Override
	public Widget setHeight(int height) {
		this.height = height;
		return this;
	}

	@Override
	public int getUpperRightX() {
		return upperRightX;
	}

	@Override
	public int getUpperRightY() {
		return upperRightY;
	}

	@Override
	public int getUpperLeftX() {
		return getUpperRightX() - getWidth();
	}

	@Override
	public int getUpperLeftY() {
		return getUpperRightY() - getWidth();
	}

	@Override
	public int getLowerRightX() {
		return getUpperRightX() - getHeight();
	}

	@Override
	public int getLowerRightY() {
		return getUpperRightY() - getHeight();
	}

	@Override
	public int getLowerLeftX() {
		return getLowerRightX() - getWidth();
	}

	@Override
	public int getLowerLeftY() {
		return getLowerRightY() - getWidth();
	}

	@Override
	public Widget setUpperRightX(int pos) {
		this.upperRightX = pos;
		return this;
	}

	@Override
	public Widget setUpperRightY(int pos) {
		this.upperRightY = pos;
		return this;
	}

	@Override
	public Widget setUpperLeftX(int pos) {
		setUpperRightX(pos + getWidth());
		return this;
	}

	@Override
	public Widget setUpperLeftY(int pos) {
		setUpperRightY(pos + getWidth());
		return this;
	}

	@Override
	public Widget setLowerRightX(int pos) {
		setUpperRightX(pos + getHeight());
		return this;
	}

	@Override
	public Widget setLowerRightY(int pos) {
		setUpperRightY(pos + getHeight());
		return this;
	}

	@Override
	public Widget setLowerLeftX(int pos) {
		setUpperRightX(pos + getWidth());
		return this;
	}

	@Override
	public Widget setLowerLeftY(int pos) {
		setUpperRightY(pos + getWidth());
		return this;
	}
	
	public Widget shiftXPos(int x) {
		setUpperRightX(getUpperRightX() + x);
		return this;
	}
	
	public Widget shiftYPos(int y) {
		setUpperRightY(getUpperRightY() + y);
		return this;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public Widget setVisible(boolean enable) {
		visible = enable;
		return this;
	}
}
