package org.spout.api.gui;

import org.spout.api.event.player.PlayerKeyEvent;
import org.spout.api.math.IntVector2;

public interface Focusable {
	public boolean canFocus();

	public boolean isFocused();

	public void onFocus(FocusReason reason);

	public void onFocusLost();

	public void onClicked(IntVector2 pos, boolean mouseDown);

	public void onKey(PlayerKeyEvent event);

	public void onMouseMove(IntVector2 position);
}
