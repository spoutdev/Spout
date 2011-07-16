package net.minecraft.src;
//BukkitContrib

import java.util.HashMap;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import org.bukkit.ChatColor;

public class ImprovedChat {
	private static HashMap<Character, String> boundCommands = new HashMap<Character, String>();
	public static void copy(String a) {
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(a), null);
	}
	
	public static void unbind(char ch) {
		boundCommands.remove(ch);
	}
	
	public static void bindCommand(char ch, String command) {
		boundCommands.put(ch, command);
	}
	
	public static String getBoundCommand(char ch) {
		return boundCommands.get(ch);
	}
	
	public static boolean handleCommand(String command) {
		try {
			if (command.startsWith("~bind")) {
				command = command.substring(6); //eat the ~bind prefix
				String[] split = command.split(" ");
				char key = split[0].toUpperCase().charAt(0);
				if (key >= 'A' && key <= 'Z') {
					command = command.substring(2); //eat the key and the following space
					if (command.startsWith("/")) {
						bindCommand(key, command);
						BukkitContrib.getGameInstance().ingameGUI.addChatMessage(ChatColor.GREEN.toString() + "Successfully bound key '" + key + "' to the command '" + command + "'");
						return true;
					}
				}
			}
			else if (command.startsWith("~unbind")) {
				command = command.substring(8); //eat the ~unbind prefix
				if (command.length() == 1) {
					unbind(command.charAt(0));
					BukkitContrib.getGameInstance().ingameGUI.addChatMessage(ChatColor.GREEN.toString() + "Successfully unbound key '" + command.charAt(0) + "'");
					return true;
				}
			}
		}
		catch (Exception e) {}
		return false;
	}

	public static String paste() {
		String str = "";
		Transferable localTransferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		if ((localTransferable != null) && (localTransferable.isDataFlavorSupported(DataFlavor.stringFlavor))) {
			try {
				str = (String)localTransferable.getTransferData(DataFlavor.stringFlavor);
			}
			catch (Exception e) {}
		}
		return str;
	}
	
	public static String formatUrl(String message) {
		int start = -1;
		if (start == -1) {
			start = message.indexOf("http://");
		}
		if (start == -1) {
			start = message.indexOf("www.");
		}
		if (start != -1) {
			char end;
			int endPos = message.length();
			for (int i = start; i < message.length(); i++) {
				end = message.charAt(i);
				endPos = i;
				if (Character.isWhitespace(end)) {
					break;
				}
			}
			
			String begin = "";
			if (start > 0) {
				begin = message.substring(0, start);
			}
			String ending = "";
			if (endPos < message.length()) {
				ending = message.substring(endPos + 1);
			}
			StringBuffer format = new StringBuffer(begin).append(ChatColor.AQUA.toString()).append(message.substring(start, endPos + 1)).append(ChatColor.WHITE.toString()).append(ending);
			return format.toString();
		}
		return message;
	}
}