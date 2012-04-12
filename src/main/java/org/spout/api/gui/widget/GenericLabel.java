package org.spout.api.gui.widget;

import java.awt.Font;
import java.awt.Rectangle;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.UnicodeFont;
import org.spout.api.gui.GuiRenderUtils;
import org.spout.api.gui.TextProperties;
import org.spout.api.plugin.Plugin;

public class GenericLabel extends AbstractWidget implements Label {

	private String text;
	private TextProperties textProperties = new TextProperties();
	
	public GenericLabel(String text, Plugin plugin) {
		super(plugin);
		setText(text);
	}

	public GenericLabel(Plugin plugin) {
		this("", plugin);
	}

	@Override
	public void render() {
		GuiRenderUtils.renderText(getText(), getTextProperties(), new Rectangle(0, 0, getGeometry().width, getGeometry().height));
	}

	@Override
	public Label setText(String text) {
		this.text = text;
		return this;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public TextProperties getTextProperties() {
		return textProperties;
	}

	@Override
	public Label setTextProperties(TextProperties p) {
		this.textProperties = p;
		return this;
	}

}
