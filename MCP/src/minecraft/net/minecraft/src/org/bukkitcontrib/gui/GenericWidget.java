package org.bukkitcontrib.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public abstract class GenericWidget implements Widget{
	protected int upperRightX = 0;
	protected int upperRightY = 0;
	protected int width = 0;
	protected int height = 0;
	protected boolean visible = true;
	protected transient boolean dirty = true;
	protected transient Screen screen = null;
	protected RenderPriority priority = RenderPriority.Normal;
	protected UUID id = UUID.randomUUID();
	
	public GenericWidget() {
		
	}
	
	public int getNumBytes() {
		return 37;
	}
	
	public GenericWidget(int upperRightX, int upperRightY, int width, int height) {
		this.upperRightX = upperRightX;
		this.upperRightY = upperRightY;
		this.width = width;
		this.height = height;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		setUpperRightX(input.readInt());
		setUpperRightY(input.readInt());
		setWidth(input.readInt());
		setHeight(input.readInt());
		setVisible(input.readBoolean());
		setPriority(RenderPriority.getRenderPriorityFromId(input.readInt()));
		long msb = input.readLong();
		long lsb = input.readLong();
		id = new UUID(msb, lsb);
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		output.writeInt(getUpperRightX());
		output.writeInt(getUpperRightY());
		output.writeInt(getWidth());
		output.writeInt(getHeight());
		output.writeBoolean(isVisible());
		output.writeInt(priority.getId());
		output.writeLong(getId().getMostSignificantBits());
		output.writeLong(getId().getLeastSignificantBits());
	}
	
	@Override
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	@Override
	public boolean isDirty() {
		return dirty;
	}
	
	@Override
	public UUID getId() {
		return id;
	}
	@Override
	public Screen getScreen() {
		return screen;
	}
	
	@Override
	public Widget setScreen(Screen screen) {
		this.screen = screen;
		return this;
	}
	
	@Override
	public RenderPriority getPriority() {
		return priority;
	}
	
	@Override
	public Widget setPriority(RenderPriority priority) {
		this.priority = priority;
		return this;
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
	public Widget shiftXPos(int modX) {
		setUpperRightX(getUpperRightX() + modX);
		return this;
	}
	
	@Override
	public Widget shiftYPos(int modY) {
		setUpperRightY(getUpperRightY() + modY);
		return this;
	}
	
	@Override
	public boolean isVisible() {
		return visible;
	}
	
	@Override
	public Widget setVisible(boolean enable) {
		visible = enable;
		return this;
	}
	
	@Override
	public int hashCode() {
		return getId().hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof Widget && other.hashCode() == hashCode();
	}
}
