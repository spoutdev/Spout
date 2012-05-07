package org.spout.api.gui;

import org.spout.api.generic.GenericType;
import org.spout.api.gui.layout.FreeLayout;
import org.spout.api.gui.layout.HorizontalLayout;
import org.spout.api.gui.layout.VerticalLayout;

public class LayoutType extends GenericType<Layout> {
	
	public static final LayoutType FREELAYOUT = new LayoutType(FreeLayout.class, 0);
	public static final LayoutType VERTICALLAYOUT = new LayoutType(VerticalLayout.class, 1);
	public static final LayoutType HORIZONTALLAYOUT = new LayoutType(HorizontalLayout.class, 2);

	public LayoutType(Class<? extends Layout> clazz, int id) {
		super(clazz, id);
	}

}
