package org.spout.api.gui;

import org.spout.api.GenericType;
import org.spout.api.gui.layout.FreeLayout;

public class LayoutType extends GenericType<Layout> {
	
	public static final LayoutType FREELAYOUT = new LayoutType(FreeLayout.class, 0);

	public LayoutType(Class<? extends Layout> clazz, int id) {
		super(clazz, id);
	}

}
