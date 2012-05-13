/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.plugin;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class PluginLogger extends Logger {
    ArrayList<PluginLoggerListener> localListeners = new ArrayList<PluginLoggerListener>();
    static ArrayList<PluginLoggerListener> globalListeners = new ArrayList<PluginLoggerListener>();
    
    private final String tag;
    private Plugin plugin;
    
    protected PluginLogger(Plugin plugin) {
        super(plugin.getClass().getCanonicalName(), null);
        setLevel(Level.ALL);
        setParent(plugin.getGame().getLogger());
        tag = "[" + plugin.getDescription().getName() + "] ";
        this.plugin = plugin;
    }

    @Override
    public void log(LogRecord logRecord) {
        for(PluginLoggerListener cur: localListeners){
            cur.onLogged(plugin, logRecord);
        }
        for(PluginLoggerListener cur: globalListeners){
            cur.onLogged(plugin, logRecord);
        }
        logRecord.setMessage(tag + logRecord.getMessage());
        super.log(logRecord);
    }
    
    /**
    * Register a local listener to this PluginLogger.
    * Local listeners forward output of a single plugin to a PluginLoggerListener.
    * @param listener The listener that the events will be sent to.
    */
    public void registerLocalListener(PluginLoggerListener listener){
        localListeners.add(listener);
    }
    
    /**
    * Remove a local listener.
    * Local listeners forward output of a single plugin to a PluginLoggerListener.
    * @param listener The listener that will be removed.
    */
    public void unRegisterLocalListener(PluginLoggerListener listener){
        localListeners.remove(listener);
    }
    
    /**
    * Register a global listener.
    * Global listeners forward output of all PluginLoggers to a PluginLoggerListener.
    * @param listener The listener that the events will be sent to.
    */
    public static void registerGlobalListener(PluginLoggerListener listener){
        globalListeners.add(listener);
    }
    
    /**
    * Remove a global listener.
    * Global listeners forward output of all PluginLoggers to a PluginLoggerListener.
    * @param listener The listener that will be removed.
    */
    public static void unRegisterGlobalListener(PluginLoggerListener listener){
        globalListeners.remove(listener);
    }
}
