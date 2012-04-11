package org.spout.api.gui.widget;

import java.awt.Font;

import org.newdawn.slick.UnicodeFont;
import org.spout.api.plugin.Plugin;

public class GenericLabel extends AbstractWidget implements Label {

	private String text;
	private UnicodeFont font = new UnicodeFont(new Font("SansSerif", Font.PLAIN, 12));

	public GenericLabel(String text, Plugin plugin) {
		super(plugin);
		setText(text);
	}

	public GenericLabel(Plugin plugin) {
		this("", plugin);
	}

	@Override
	public void render() {
		font.drawString(0, 0, getText());
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

}
