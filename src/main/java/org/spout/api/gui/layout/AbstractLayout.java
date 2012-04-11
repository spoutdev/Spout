package org.spout.api.gui.layout;

import java.util.LinkedList;

import org.lwjgl.opengl.GL11;
import org.spout.api.gui.Container;
import org.spout.api.gui.Layout;
import org.spout.api.gui.Widget;

public abstract class AbstractLayout implements Layout {
	private Container parent = null;
	protected LinkedList<Widget> attachedWidgets;

	@Override
	public Widget[] getWidgets() {
		return attachedWidgets.toArray(new Widget[0]);
	}

	@Override
	public void addWidgets(Widget... widgets) {
		for (Widget widget : widgets) {
			widget.setLayout(this);
			attachedWidgets.addLast(widget);
		}
		relayout();
	}

	@Override
	public void clear() {
		for (Widget widget : attachedWidgets) {
			widget.setLayout(null);
		}
		attachedWidgets.clear();
		relayout();
	}

	@Override
	public void removeWidgets(Widget... widgets) {
		for (Widget widget : widgets) {
			widget.setLayout(null);
			attachedWidgets.remove(widget);
		}
		relayout();
	}

	@Override
	public void setParent(Container container) {
		parent = container;
	}

	@Override
	public Container getParent() {
		return parent;
	}

	@Override
	public void render() {
		for(Widget widget : attachedWidgets) {
			double x = widget.getGeometry().getX();
			double y = widget.getGeometry().getY();
			GL11.glTranslated(x, y, 0);
			GL11.glColor4d(1, 1, 1, 1);
			GL11.glPushMatrix();
			widget.render();
			GL11.glPopMatrix();
			GL11.glTranslated(-x, -y, 0);
		}
	}
}
