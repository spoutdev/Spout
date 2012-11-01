package org.spout.api.gui.component;

import java.util.ArrayList;
import java.util.List;

import org.spout.api.component.components.WidgetComponent;
import org.spout.api.gui.render.RenderPart;

public class RenderPartsHolderComponent extends WidgetComponent {
	private final List<RenderPart> parts = new ArrayList<RenderPart>();
	
	@Override
	public List<RenderPart> getRenderParts() {
		return parts;
	}
	
	public int add(RenderPart part) {
		// Last added on top
		return add(part, parts.size());
	}
	
	public int add(RenderPart part, int zIndex) {
		part.setZIndex(zIndex);
		parts.add(part);
		return parts.size()-1;
	}
	
	public RenderPart get(int index) {
		return parts.get(index);
	}
}
