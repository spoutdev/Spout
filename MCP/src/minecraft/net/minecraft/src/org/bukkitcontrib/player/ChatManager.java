package org.bukkitcontrib.player;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiChat;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.bukkit.ChatColor;
import java.util.HashMap;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import net.minecraft.src.BukkitContrib;
import net.minecraft.src.ChatAllowedCharacters;

public class ChatManager {
	public int commandScroll = 0;
	public int chatScroll = 0;
	private HashMap<Character, String> boundCommands = new HashMap<Character, String>();
	public ArrayList<String> pastCommands = new ArrayList<String>(1000);
	public boolean onChatKeyTyped(char character, int key, GuiChat chat) {
		try {
			boolean control = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
			String message = chat.message;
			int cursor = chat.cursorPosition;
			if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) && cursor > 0) {
				updateCursor(--cursor, chat);
			}
			
			else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && cursor < message.length()) {
				updateCursor(++cursor, chat);
			}
			
			else if (Keyboard.isKeyDown(Keyboard.KEY_DELETE) && cursor < message.length()) {
				message = message.substring(0, cursor) + message.substring(cursor + 1);
				updateMessage(message, chat);
			}
			
			else if (Keyboard.isKeyDown(Keyboard.KEY_PRIOR) && chatScroll < getChatHistorySize()) {
				chatScroll += 20;
				chatScroll = Math.min(getChatHistorySize() - 1, chatScroll);
			}
			
			else if (Keyboard.isKeyDown(Keyboard.KEY_NEXT) && chatScroll > 0) {
				chatScroll -= 20;
				chatScroll = Math.max(0, chatScroll);
			}
			
			else if (Keyboard.isKeyDown(Keyboard.KEY_HOME)) {
				chatScroll = 0;
			}
			
			else if (Keyboard.isKeyDown(Keyboard.KEY_END)) {
				chatScroll = getChatHistorySize() - 2;
			}
			
			else if (Keyboard.isKeyDown(Keyboard.KEY_UP) && commandScroll < pastCommands.size()) {
				commandScroll++;
				message = updateCommandScroll(chat);
				updateMessage(message, chat);
			}
			
			else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && commandScroll > 0) {
				commandScroll--;
				message = updateCommandScroll(chat);
				updateMessage(message, chat);
			}

			else if (control && Keyboard.isKeyDown(Keyboard.KEY_V)) {
				String paste = paste();
				message = (message.substring(0, cursor) + paste + message.substring(cursor));
				cursor += paste.length();
				updateMessage(message, chat);
				updateCursor(cursor, chat);
			}
			
			else if (control && Keyboard.isKeyDown(Keyboard.KEY_C)) {
				copy(message);
			}
			
			else if (Keyboard.isKeyDown(Keyboard.KEY_BACK) && message.length() > 0 && cursor > 0) {
				if (message.length() == 1) {
					message = "";
				}
				else {
					String text = message;
					if (cursor > text.length()) {
						cursor = text.length();
					}
					if (cursor > 1) {
						text = message.substring(0, cursor - 1);
					}
					else {
						text = "";
					}
					if (cursor > 0) {
						text += message.substring(cursor);
					}
					message = text;
				}
				updateCursor(--cursor, chat);
				updateMessage(message, chat);
			}
			
			//Messages are infinite length, commands's are limited to 100 chars
			else if (ChatAllowedCharacters.allowedCharacters.indexOf(character) > -1 && (!message.startsWith("/") || message.length() < 100)) {
				if (cursor == 0) {
					message = character + message;
				}
				else if (cursor > 0) {
					message = (message.substring(0, cursor) + character + message.substring(cursor));
				}
				else {
					message += character;
				}
				updateMessage(message, chat);
				updateCursor(++cursor, chat);
			}

			//Not handled
			else {
				return false;
			}
			
			//Handled
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public int getChatHistorySize() {
		return BukkitContrib.getGameInstance().ingameGUI.chatMessageList.size();
	}
	
	public int checkCursor(String message, int cursor) {
		if (cursor > message.length()) {
			cursor = message.length();
		}
		else if (cursor < 0) {
			cursor = 0;
		}
		return cursor;
	}
	
	public String updateCommandScroll(GuiChat chat) {
		String command;
		if (commandScroll == 0) {
			command = "";
		}
		else {
			command = (String)pastCommands.get(pastCommands.size() - commandScroll);
		}
		updateMessage(command, chat);
		updateCursor(command.length(), chat);
		return command;
	}

	public void updateCursor(int position, GuiChat chat) {
		chat.cursorPosition = checkCursor(chat.message, position);
	}
	
	public void updateMessage(String message, GuiChat chat) {
		chat.message = message;
		updateCursor(chat.cursorPosition, chat);
	}
	
	public void sendChat(String message) {
		ArrayList<String> lines = formatChat(message);
		for (String chat : lines) {
			BukkitContrib.getGameInstance().thePlayer.sendChatMessage(chat);
		}
	}
	
	public String formatChatColors(String message) {
		String text = "";
		ChatColor last = null;
		for (int i = 0; i < message.length(); i++) {
			char ch = message.charAt(i);
			if (ch == '&' && i < message.length() - 2) {
				char next = message.charAt(i + 1);
				int number = -1;
				String temp = "" + next;
				if (Character.isDigit(next)) {
					number = Integer.parseInt(temp);
					i++;
				}
				else if (next >= 'a' && next <= 'f') {
					number = 10 + next - 'a';
					i++;
				}
				if (number > -1 && number < 16) {
					last = ChatColor.getByCode(number);
				}
				if (last != null) {
					text += last.toString();
				}
			}
			else {
				text += ch;
			}
		}
		return text;
	}
	
	public ArrayList<String> formatChat(String message) {
		ArrayList<String> lines = new ArrayList<String>();
		int line = 0;
		ChatColor last = null; //TODO this is not implemented
		String text = "";
		message = 
		for (int i = 0; i < message.length(); i++) {
			char ch = message.charAt(i);
			boolean newline = ch == '\n';
			if (!newline && i < message.length() - 1 && ch == '\\' && message.charAt(i + 1) == 'n') {
				i++;
				newline = true;
			}
			if ((line > 90 && Character.isWhitespace(ch)) || line > 95 || newline) {
				lines.add(text);
				line = 0;
				text = "";
				if (last != null) {
					text += last.toString();
				}
			}
			else {
				text += ch;
				line++;
			}
		}
		lines.add(text);
		return lines;
	}
	
	public void handleMouseWheel() {
		int wheel = Mouse.getDWheel();
		if (wheel > 0) {
			if (chatScroll < getChatHistorySize()) {
				chatScroll++;
			}
		} 
		else if (wheel < 0) {
			if (chatScroll > 0) {
				chatScroll--;
			}
		}
	}
	
	public void unbind(char ch) {
		boundCommands.remove(ch);
	}
	
	public void bindCommand(char ch, String command) {
		boundCommands.put(ch, command);
	}
	
	public String getBoundCommand(char ch) {
		return boundCommands.get(ch);
	}
	
	public boolean handleCommand(String command) {
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
					char key = command.toUpperCase().charAt(0);
					unbind(key);
					BukkitContrib.getGameInstance().ingameGUI.addChatMessage(ChatColor.GREEN.toString() + "Successfully unbound key '" + key + "'");
					return true;
				}
			}
		}
		catch (Exception e) {}
		return false;
	}
	
	public static void copy(String a) {
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(a), null);
	}

	public static String paste() {
		String str = "";
		Transferable localTransferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		if (localTransferable != null && localTransferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
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