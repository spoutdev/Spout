package org.getspout.Spout.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public interface Widget{
	
	public int getNumBytes();
	
	public WidgetType getType();
	
	public UUID getId();
	
	public void render();
	
	public void readData(DataInputStream input) throws IOException;
	
	public void writeData(DataOutputStream output) throws IOException;
	
	public void setDirty(boolean dirty);
	
	public boolean isDirty();
	
	public RenderPriority getPriority();
	
	public Widget setPriority(RenderPriority priority);
	
	public int getWidth();
	
	public Widget setWidth(int width);
	
	public int getHeight();
	
	public Widget setHeight(int height);
	
	public Screen getScreen();
	
	public Widget setScreen(Screen screen);

	public int getX();
	
	public int getY();
	
	public Widget setX(int pos);
	
	public Widget setY(int pos);
	
	public Widget shiftXPos(int x);
	
	public Widget shiftYPos(int y);
	
	public boolean isVisible();
	
	public Widget setVisible(boolean enable);
	
	public void onTick();
}
