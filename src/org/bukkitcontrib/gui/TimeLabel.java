package org.bukkitcontrib.gui;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.bukkit.Location;
import org.bukkitcontrib.player.ContribPlayer;

public class TimeLabel extends GenericLabel implements Widget, Label{
	private String last = "";
	private final ContribPlayer player;
	public TimeLabel(InGameScreen screen, ContribPlayer player) {
		this.player = player;
		setHeight(10).setWidth(10).setScreen(screen).setUpperRightX(screen.getWidth() / 2).setUpperRightY(screen.getHeight() / 2);
	}

	@Override
	public String getText() {
		Location loc = player.getLocation();
		return DateFormat.getTimeInstance().format((new Date())) + "\n Day: " + (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) + "\n (" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")";
	}

	@Override
	public Label setText(String text) {
		return this;
	}
	
	public boolean isDirty() {
		String temp = last;
		last = getText();
		return !temp.equals(getText());
	}
	
	public int getHexColor() {
		return 0xCC6600;
	}

}
