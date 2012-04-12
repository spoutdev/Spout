package org.spout.api.gui.widget;

import org.spout.api.gui.TextProperties;
import org.spout.api.gui.Widget;

public interface Label extends Widget {
	public TextProperties getTextProperties();
	public Label setTextProperties(TextProperties p);
	public Label setText(String text);
	public String getText();
}
