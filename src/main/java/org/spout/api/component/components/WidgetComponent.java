package org.spout.api.component.components;

import java.util.Collections;
import java.util.List;

import org.spout.api.component.Component;
import org.spout.api.gui.FocusReason;
import org.spout.api.gui.Widget;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.keyboard.KeyEvent;
import org.spout.api.math.IntVector2;

public class WidgetComponent extends Component {
	@Override
	public Widget getOwner() {
		// TODO Auto-generated method stub
		return (Widget) super.getOwner();
	}
	
	/**
	 * Returns a list of RenderParts that are to be rendered for this widget <br/>
	 * Only called when the widget's internal cache isn't clean, that means, 
	 * you must call getOwner().update() to invoke a render update
	 * @return a list of RenderParts
	 */
	public List<RenderPart> getRenderParts() {
		return Collections.emptyList(); // Components which decide how this widget is rendered need to reimplement this method
	}
	
	/**
	 * Called when this widget was clicked on
	 * @param position the position on the widget (in pixels)
	 */
	public void onClicked(IntVector2 position) {
		
	}
	
	/**
	 * Called when this widget is focussed and a key was typed
	 * @param event the key event
	 */
	public void onKey(KeyEvent event) {
		
	}
	
	/**
	 * Called when this widget gains focus
	 * @param reason the reason why this focus was set
	 */
	public void onFocus(FocusReason reason) {
		
	}

	/**
	 * Called when this widget loses focus
	 */
	public void onFocusLost() {
		
	}
}
