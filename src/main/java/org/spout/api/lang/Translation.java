/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
package org.spout.api.lang;

import org.spout.api.Spout;
import org.spout.api.command.CommandSource;
import org.spout.api.plugin.CommonClassLoader;
import org.spout.api.plugin.CommonPlugin;
import org.spout.api.plugin.Plugin;

/**
 * Provides helper methods for translation
 * <h2>Loading translation files</h2>
 * <p>Plugins can use translation by either putting translation files into the jar-package or in their data-directory.</p>
 * <h3>Jar-File</h3>
 * <p>Put your translation files into a folder called "lang" in the root directory:
 * <pre>&lt;jar&gt;/lang/lang-&lt;countrycode&gt;.yml</pre></p>
 * <h3>Data-Directory</h3>
 * <p>Put your translation files into a folder called "lang" in your plugins data-directory:
 * <pre>plugins/&lt;plugin-name&gt;/lang/lang-&lt;countrycode&gt;.yml</pre></p>
 * <p>
 * Translation files in the plugins data-directory will be preferred over files in the jar
 * </p>
 * <h2>Translating strings in the code</h2>
 * <p>To translate strings, use Translation.tr().</p>
 * <h3>Example</h3>
 * <pre>player.sendMessage(Translation.tr("You've been teleported to %1", player, target);</pre>
 * <p>You have to pass a CommandSource object so SpoutAPI can determine the preferred target-language.</p>
 * <p><strong>TIP: </strong> use a static import:
 * <pre>import static org.spout.api.lang.Translation.tr;
 *...
 *tr("hello", player);</pre></p>
 * 
 */

// TODO: file format, ChatStyle integration, tool for translation, %n as a placeholder for numbers
public class Translation {
	
	
	private static String foundClass = null;
	private static final String LANG_PACKAGE = "org.spout.api.lang";
	/**
	 * Returns the translation of source into the receivers preferred language
	 * @param source the string to translate
	 * @param receiver the receiver who will see the message
	 * @param args any object given will be inserted into the target string for each %0, %1 asf
	 * @return the translation
	 */
	public static String tr(String source, CommandSource receiver, Object ...args) {
		Plugin plugin = getPluginForStacktrace();
		PluginDictionary pldict = plugin.getDictionary();
		return pldict.tr(source, receiver, foundClass, args);
	}
	
	/**
	 * Broadcasts the source string to all players on the server.<br/>
	 * Will translate the source string into each players respective target language.
	 * @param source the string to translate
	 * @param args any object given will be inserted into the target string for each %0, %1 asf
	 */
	public static void broadcast(String source, Object ...args) {
		broadcast(source, Spout.getEngine().getOnlinePlayers(), args);
	}
	
	/**
	 * Broadcasts the source string to all CommandSources given in receivers<br/>
	 * Will translate the source string into each CommandSoruce's respective target language.
	 * @param source the string to translate
	 * @param receivers the receivers to send the message to
	 * @param args any object given will be inserted into the target string for each %0, %1 asf
	 */
	public static void broadcast(String source, CommandSource receivers[], Object ...args) {
		Plugin plugin = getPluginForStacktrace();
		PluginDictionary pldict = plugin.getDictionary();
		pldict.broadcast(source, receivers, foundClass, args);
	}
	
	private static Plugin getPluginForStacktrace() {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		boolean canSeePlugin = false;
		for (int i = 0; i < trace.length; i++) {
			String clazz = trace[i].getClassName();
			foundClass = clazz;
			if (clazz.startsWith(LANG_PACKAGE)) {
				// Skip all classes in the org.spout.api.lang package
				canSeePlugin = true;
				continue;
			}
			if (canSeePlugin) {
				CommonPlugin plugin = CommonClassLoader.getPlugin(clazz);
				if (plugin != null) {
					return plugin;
				} else {
					return Spout.getPluginManager().getPlugin("Spout");	
				}
			}
		}
		return Spout.getPluginManager().getPlugin("Spout");	
	}
}
