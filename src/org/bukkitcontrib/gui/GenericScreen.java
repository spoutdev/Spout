package org.bukkitcontrib.gui;

import java.util.ArrayList;
import java.util.List;

public class GenericScreen implements Screen{
	protected List<Widget> widgets = new ArrayList<Widget>();

	@Override
	public Widget[] getAttachedWidgets() {
		Widget[] list = new Widget[widgets.size()];
		widgets.toArray(list);
		return list;
	}

	@Override
	public Screen attachWidget(Widget widget) {
		widgets.add(widget);
		return this;
	}

	@Override
	public Screen removeWidget(Widget widget) {
		widgets.remove(widget);
		return this;
	}

}
