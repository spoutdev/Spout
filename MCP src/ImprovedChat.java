package net.minecraft.src;
//BukkitContrib

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import org.bukkit.ChatColor;

public class ImprovedChat {
	public static void copy(String a) {
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(a), null);
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