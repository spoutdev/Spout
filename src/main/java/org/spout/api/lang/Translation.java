package org.spout.api.lang;

import org.spout.api.Spout;
import org.spout.api.command.CommandSource;

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
	
	/**
	 * Returns the translation of source into the receivers preferred language
	 * @param source the string to translate
	 * @param receiver the receiver who will see the message
	 * @param args any object given will be inserted into the target string for each %0, %1 asf
	 * @return the translation
	 */
	public static String tr(String source, CommandSource receiver, Object ...args) {
		
		return source;
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
		for (CommandSource receiver:receivers) {
			receiver.sendMessage(tr(source, receiver, args));
		}
	}
}
