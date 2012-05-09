package org.spout.api.gui;

import java.awt.Font;

import java.awt.Color;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class TextProperties {
	private int align = Align.ALIGN_CENTER_MIDDLE;
	private UnicodeFont font = null;
	private Color color = Color.white;
	
	public TextProperties() {
		try {
			font = new UnicodeFont("/Library/Fonts/Courier New.ttf", 12, false, false);
			font.getEffects().add(new ColorEffect(Color.white));
			font.addAsciiGlyphs();
			font.loadGlyphs();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public TextProperties(int align, UnicodeFont font, Color color) {
		this.align = align;
		this.font = font;
		this.color = color;
	}

	public int getAlign() {
		return align;
	}

	public void setAlign(int align) {
		this.align = align;
	}

	public UnicodeFont getFont() {
		return font;
	}

	public void setFont(UnicodeFont font) {
		this.font = font;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
