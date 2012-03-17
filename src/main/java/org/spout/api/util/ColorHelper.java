package org.spout.api.util;

import java.awt.Color;

public class ColorHelper {
	
	public static final Color invalid = new Color(0, 1, 0, 0);
	public static final Color override = new Color(0, 2, 0, 0);

	public static int toInt(Color c){
		return (c.getAlpha() & 0xFF) << 24 | (c.getRed() & 0xFF) << 16 | (c.getGreen() & 0xFF) << 8 | c.getBlue() & 0xFF;
	}
	
}
