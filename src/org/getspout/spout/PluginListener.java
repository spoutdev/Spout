package org.getspout.spout;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.ServerListener;
import org.getspout.spoutapi.SpoutManager;

public class PluginListener extends ServerListener{
	@Override
	public void onPluginDisable(PluginDisableEvent event) {
		SpoutManager.getKeyboardManager().removeAllKeyBindings(event.getPlugin());
	}

}
