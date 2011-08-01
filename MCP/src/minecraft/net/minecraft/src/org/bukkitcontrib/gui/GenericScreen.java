package org.getspout.Spout.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.lwjgl.opengl.GL11;
import org.getspout.Spout.packet.PacketWidget;

public abstract class GenericScreen extends GenericWidget implements Screen{
	protected List<Widget> widgets = new ArrayList<Widget>();
	protected int playerId;
	public GenericScreen() {
		
	}
	
	public GenericScreen(int playerId) {
		this.playerId = playerId;
	}

	@Override
	public Widget[] getAttachedWidgets() {
		Widget[] list = new Widget[widgets.size()];
		widgets.toArray(list);
		return list;
	}

	@Override
	public Screen attachWidget(Widget widget) {
		widgets.add(widget);
		widget.setScreen(this);
		return this;
	}

	@Override
	public Screen removeWidget(Widget widget) {
		widgets.remove(widget);
		widget.setScreen(null);
		return this;
	}
	
	@Override
	public boolean containsWidget(Widget widget) {
		return containsWidget(widget.getId());
	}
	
	@Override
	public boolean containsWidget(UUID id) {
		return getWidget(id) != null;
	}
	
	@Override
	public Widget getWidget(UUID id) {
		for (Widget w : widgets) {
			if (w.getId().equals(id)) {
				return w;
			}
		}
		return null;
	}
	
	@Override
	public boolean updateWidget(Widget widget) {
		int index = widgets.indexOf(widget);
		if (index > -1) {
			widgets.remove(index);
			widgets.add(index, widget);
			return true;
		}
		return false;
	}
	
	@Override
	public void onTick() {
		for (Widget widget : widgets) {
			widget.onTick();
		}
	}
	
	protected boolean canRender(Widget widget) {
		return widget.isVisible();
	}
	
	public void render() {
		for (RenderPriority priority : RenderPriority.values()) {	
			for (Widget widget : getAttachedWidgets()){
				if (widget.getPriority() == priority && canRender(widget)) {
					GL11.glPushMatrix();
					widget.render();
					GL11.glPopMatrix();
				}
			}
		}
	}
}
