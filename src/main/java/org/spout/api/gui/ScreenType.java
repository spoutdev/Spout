package org.spout.api.gui;

import org.spout.api.generic.GenericType;
import org.spout.api.gui.screen.FullScreen;

public class ScreenType extends GenericType<Screen> {
	
	public static final ScreenType GENERICSCREEN = new ScreenType(GenericScreen.class, 0);
	public static final ScreenType FULLSCREEN = new ScreenType(FullScreen.class, 1);

	public ScreenType(Class<? extends Screen> clazz, int id) {
		super(clazz, id);
	}

}
