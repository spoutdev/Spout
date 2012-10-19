package org.spout.api.gui.component;

import java.util.LinkedList;
import java.util.List;

import org.spout.api.component.components.WidgetComponent;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.gui.render.TextPart;
import org.spout.api.map.DefaultedKey;

public class LabelComponent extends WidgetComponent {
	private static final DefaultedKey<String> KEY_TEXT = new DefaultedKey<String>() {
		
		@Override
		public String getDefaultValue() {
			return "(your text here)";
		}
		
		@Override
		public String getKeyString() {
			return "button-text";
		}
		
	};
	
	@Override
	public List<RenderPart> getRenderParts() {
		List<RenderPart> ret = new LinkedList<RenderPart>();
		TextPart text = new TextPart();
		text.setSource(getOwner().getGeometry());
		text.setSprite(getOwner().getGeometry());
		text.setText(getText());
		ret.add(text);
		return ret;
	}


	public String getText() {
		return getData().get(KEY_TEXT);
	}

	public void setText(String text) {
		getData().put(KEY_TEXT, text);
		getOwner().update();
	}

}
