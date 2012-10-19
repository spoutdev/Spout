package org.spout.api.gui;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import org.spout.api.gui.component.ControlComponent;
import org.spout.api.plugin.Plugin;
import org.spout.api.tickable.BasicTickable;

public class Screen extends BasicTickable implements Container {
	private HashMap<Widget, Plugin> widgets = new LinkedHashMap<Widget, Plugin>();
	private Widget focussedWidget = null;
	
	@Override
	public Set<Widget> getWidgets() {
		return widgets.keySet();
	}
	
	@Override
	public void attachWidget(Plugin plugin, Widget widget) {
		widgets.put(widget, plugin);
		widget.setScreen(this);
	}
	
	@Override
	public void removeWidget(Widget widget) {
		widgets.remove(widget);
	}
	
	@Override
	public void removeWidgets(Widget ...widgets) {
		for (Widget widget: widgets) {
			removeWidget(widget);
		}
	}
	
	@Override
	public void removeWidgets(Plugin plugin) {
		//TODO
	}
	
	public Widget getFocussedWidget() {
		return focussedWidget;
	}
	
	public void setFocussedWidget(Widget focussedWidget) {
		if (focussedWidget.has(ControlComponent.class)) {
			if (this.focussedWidget != null && this.focussedWidget != focussedWidget) {
				this.focussedWidget.onFocusLost();
			}
			this.focussedWidget = focussedWidget;
		} else {
			throw new IllegalStateException("Can only focus controls, add a ControlComponent to your widget!");
		}
	}

	@Override
	public void onTick(float dt) {
		for (Widget w:widgets.keySet()) {
			w.tick(dt);
		}
	}

	@Override
	public boolean canTick() {
		return true;
	}
}
