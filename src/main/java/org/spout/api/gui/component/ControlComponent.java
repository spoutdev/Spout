package org.spout.api.gui.component;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.spout.api.component.components.WidgetComponent;
import org.spout.api.gui.FocusReason;
import org.spout.api.gui.render.RectanglePart;
import org.spout.api.gui.render.RenderPart;

public class ControlComponent extends WidgetComponent {
	@Override
	public List<RenderPart> getRenderParts() {
		LinkedList<RenderPart> ret = new LinkedList<RenderPart>();
		if (getOwner().hasFocus()) {
			RectanglePart part = new RectanglePart();
			part.setColor(Color.BLUE);
			part.setSource(getOwner().getGeometry());
			part.setSprite(getOwner().getGeometry());
			part.setZIndex(-100);
			ret.add(part);
		}
		return ret;
	}
	
	@Override
	public void onFocus(FocusReason reason) {
		getOwner().update();
	}
	
	@Override
	public void onFocusLost() {
		getOwner().update();
	}
}
