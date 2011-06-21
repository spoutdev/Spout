package org.bukkitcontrib.gui;

import java.text.DateFormat;
import java.util.Date;

import org.bukkit.ChatColor;

public class TimeLabel extends GenericLabel implements Widget, Label{

	@Override
	public String getText() {
		return ChatColor.YELLOW.toString() + DateFormat.getTimeInstance().format((new Date()));
	}

	@Override
	public Label setText(String text) {
		return this;
	}

}
