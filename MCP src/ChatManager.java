package net.minecraft.src;
//BukkitContrib

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.bukkit.ChatColor;

public class ChatManager {
	public static int commandScroll = 0;
	public static int chatScroll = 0;
	public static ArrayList<String> pastCommands = new ArrayList<String>(1000);
	public static boolean onChatKeyTyped(char character, int key, GuiChat chat) {
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
				String paste = ImprovedChat.paste();
				message = (message.substring(0, cursor) + paste + message.substring(cursor));
				cursor += paste.length();
				updateMessage(message, chat);
				updateCursor(cursor, chat);
			}
			
			else if (control && Keyboard.isKeyDown(Keyboard.KEY_C)) {
				ImprovedChat.copy(message);
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
	
	public static int getChatHistorySize() {
		return BukkitContrib.getGameInstance().ingameGUI.chatMessageList.size();
	}
	
	public static int checkCursor(String message, int cursor) {
		if (cursor > message.length()) {
			cursor = message.length();
		}
		else if (cursor < 0) {
			cursor = 0;
		}
		return cursor;
	}
	
	public static String updateCommandScroll(GuiChat chat) {
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

	public static void updateCursor(int position, GuiChat chat) {
		chat.cursorPosition = checkCursor(chat.message, position);
	}
	
	public static void updateMessage(String message, GuiChat chat) {
		chat.message = message;
		updateCursor(chat.cursorPosition, chat);
	}
	
	public static void sendChat(String message) {
		ArrayList<String> lines = formatChat(message);
		for (String chat : lines) {
			BukkitContrib.getGameInstance().thePlayer.sendChatMessage(chat);
		}
	}
	
	public static String formatChatColors(String message) {
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
	
	public static ArrayList<String> formatChat(String message) {
		ArrayList<String> lines = new ArrayList<String>();
		int line = 0;
		ChatColor last = null;
		String text = "";
		for (int i = 0; i < message.length(); i++) {
			char ch = message.charAt(i);
			boolean newline = ch == '\n';
			if (!newline && i < message.length() - 1 && ch == '\\' && message.charAt(i + 1) == 'n') {
				i++;
				newline = true;
			}
			if ((line > 80 && Character.isWhitespace(ch)) || line > 99 || newline) {
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
	
	public static void handleMouseWheel() {
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
}