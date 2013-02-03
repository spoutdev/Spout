package org.spout.api.gui;

import java.util.List;

import org.spout.api.gui.render.RenderPart;

public interface RenderPartContainer {
	/**
	 * Returns a list of RenderParts that are to be rendered for this widget <br/>
	 * Only called when the widget's internal cache isn't clean, that means,
	 * you must call getOwner().update() to invoke a render update
	 * @return a list of RenderParts
	 */
	public List<RenderPart> getRenderParts();
}
