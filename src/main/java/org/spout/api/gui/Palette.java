package org.spout.api.gui;

import java.awt.Color;
import java.util.HashMap;



/**
 * Defines a color palette for widgets to draw. Each widget can have its own palette, 
 * but by default each one has the same instance so the colors are consistent.
 */
public class Palette {
	private HashMap<Integer, Color> colors = new HashMap<Integer, Color>();
	
	public void setColor(WidgetState state, ColorType type, Color color) {
		colors.put(getHash(state, type), color);
	}
	
	public Color getColor(WidgetState state, ColorType type) {
		return colors.get(getHash(state, type));
	}
	
	private int getHash(WidgetState state, ColorType type) {
		return (state.getId() << 4) | type.getId();
	}
	
	public enum ColorType {
		BORDER(0),
		BACKGROUND(1),
		TEXT(2),
		
		;
		private final int num;
		private ColorType(int num) {
			this.num = num;
		}
		
		public int getId() {
			return num;
		}
	}
}
