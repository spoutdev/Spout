package org.spout.api.gui;

import java.util.Set;

import org.spout.api.plugin.Plugin;

public interface Container {

	public abstract void removeWidgets(Plugin plugin);

	public abstract void removeWidgets(Widget ...widgets);

	public abstract void removeWidget(Widget widget);

	public abstract void attachWidget(Plugin plugin, Widget widget);

	public abstract Set<Widget> getWidgets();

}
