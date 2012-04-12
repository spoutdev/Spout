package org.spout.api.gui.layout;

import java.awt.Point;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;
import org.spout.api.gui.Container;
import org.spout.api.gui.Control;
import org.spout.api.gui.Layout;
import org.spout.api.gui.MouseButton;
import org.spout.api.gui.MouseEventHandler;
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
			widget.setParent(this);
			attachedWidgets.addLast(widget);
		}
		relayout();
	}

	@Override
	public void clear() {
		for (Widget widget : attachedWidgets) {
			widget.setParent(null);
		}
		attachedWidgets.clear();
		relayout();
	}

	@Override
	public void removeWidgets(Widget... widgets) {
		for (Widget widget : widgets) {
			widget.setParent(null);
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

	@Override
	public void onMouseDown(Point position, MouseButton button) {
		Widget clicked = getWidgetAt(position);
		if(canReceiveClick(clicked)) {
			((MouseEventHandler) clicked).onMouseDown(position, button);
		}
	}

	@Override
	public void onMouseMove(Point position) {
		Widget clicked = getWidgetAt(position);
		if(canReceiveClick(clicked)) {
			((MouseEventHandler) clicked).onMouseMove(position);
		}
	}

	@Override
	public void onMouseUp(Point position, MouseButton button) {
		Widget clicked = getWidgetAt(position);
		if(canReceiveClick(clicked)) {
			((MouseEventHandler) clicked).onMouseUp(position, button);
		}
	}
	
	protected static boolean canReceiveClick(Widget w) {
		if(w instanceof Control) {
			return ((Control) w).isEnabled();
		} else if(w instanceof MouseEventHandler) {
			return true;
		}
		return false;
	}
	
	public Widget getWidgetAt(Point position) {
		for(Widget widget : attachedWidgets) {
			if(widget.getGeometry().contains(position)) {
				return widget;
			}
		}
		return null;
	}
}
