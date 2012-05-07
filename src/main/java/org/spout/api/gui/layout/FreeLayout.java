package org.spout.api.gui.layout;

import org.spout.api.gui.LayoutType;


public class FreeLayout extends AbstractLayout {
	@Override
	public void relayout() {
		//TODO: layout based on left, top, right, bottom properties in the attributes. Waiting for the attribute system to be merged.
	}

	@Override
	public LayoutType getLayoutType() {
		return LayoutType.FREELAYOUT;
	}
}
