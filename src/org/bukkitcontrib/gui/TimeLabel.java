package org.bukkitcontrib.gui;

import java.text.DateFormat;
import java.util.Date;

import org.bukkit.ChatColor;

public class TimeLabel extends GenericLabel implements Widget, Label{
    private String last = "";
    public TimeLabel(InGameScreen screen) {
        setHeight(10).setWidth(10).setScreen(screen).setLowerLeftX(screen.getWidth() / 2).setLowerLeftY(screen.getHeight() / 2);
    }

    @Override
    public String getText() {
        return ChatColor.YELLOW.toString() + DateFormat.getTimeInstance().format((new Date()));
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

}
