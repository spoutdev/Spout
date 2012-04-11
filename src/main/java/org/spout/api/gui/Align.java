package org.spout.api.gui;

public class Align {
	public static int ALIGN_CENTER = 1;
	public static int ALIGN_LEFT = 2;
	public static int ALIGN_RIGHT = 4;
	public static int ALIGN_TOP = 8;
	public static int ALIGN_MIDDLE = 16;
	public static int ALIGN_BOTTOM = 32;
	
	public static int ALIGN_CENTER_MIDDLE = ALIGN_CENTER | ALIGN_MIDDLE;
	
	public static int HORIZONTAL_MASK = ALIGN_CENTER | ALIGN_LEFT | ALIGN_RIGHT;
	public static int VERTICAL_MASK = ALIGN_TOP | ALIGN_MIDDLE | ALIGN_BOTTOM;
}
