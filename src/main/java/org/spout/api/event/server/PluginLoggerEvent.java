package org.spout.api.event.server;

import java.util.logging.LogRecord;
import org.spout.api.event.Event;
import org.spout.api.event.HandlerList;
import org.spout.api.plugin.Plugin;

public class PluginLoggerEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    Plugin plugin;
    LogRecord record;

    public PluginLoggerEvent(Plugin plugin, LogRecord record) {
        this.plugin = plugin;
        this.record = record;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public LogRecord getRecord() {
        return record;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
