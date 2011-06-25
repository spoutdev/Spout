package net.minecraft.src;
//BukkitContrib
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class ClipboardThread extends Thread {
	public ClipboardThread(EntityClientPlayerMP player) {
		this.player = player;
	}
	EntityClientPlayerMP player;
	String prevClipboardText = "";
	public void run() {
		while(true) {
			try {
				sleep(50);
			} catch (InterruptedException e1) {
				
			}
			try {
				Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
				if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					String text = null;
					try {
						text = (String) contents.getTransferData(DataFlavor.stringFlavor);
					} catch (UnsupportedFlavorException e) {

					} catch (IOException e) {

					}
					if (text != null) {
						if (!text.equals(prevClipboardText)) {
							prevClipboardText = text;
							player.sendQueue.addToSendQueue(new CustomPacket(new PacketClipboardText(text)));
						}
					}
				}
			}
			catch (Exception e2) {
			
			}
		}
	}

}
