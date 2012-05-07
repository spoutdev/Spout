package org.spout.api.gui.layout;

import org.spout.api.gui.BoundsI;

public abstract class ManagedLayout extends AbstractLayout {
	private BoundsI margin = new BoundsI();

	public BoundsI getMargin() {
		return margin;
	}

	public void setMargin(BoundsI margin) {
		this.margin = margin;
	}
}
