package org.bukkitcontrib;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.ServerListener;

public class PluginListener extends ServerListener{
	@Override
	public void onPluginDisable(PluginDisableEvent event) {
		BukkitContrib.getKeyboardManager().removeAllKeyBindings(event.getPlugin());
	}

}
