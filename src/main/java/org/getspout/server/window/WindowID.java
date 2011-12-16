package org.getspout.server.window;

public class WindowID {
	public static byte uniqueID = 1;

	public static final byte CHEST = 0;
	public static final byte WORKBENCH = 1;
	public static final byte FURNACE = 2;
	public static final byte DISPENSER = 3;
	public static final byte ENCHANTINGTABLE = 4;
	public static final byte BREWINGSTAND = 5;

	/*
	 * Get the title attributed to the window ID
	 * Returns an empty string if the title is not known
	 */
	public static String getWindowTitle(byte ID, byte slotcount)
	{
		String title = "";
		if(ID == CHEST)
		{
			if(slotcount > 27) title = "Large chest";
			else title = "Chest";
		}
		else if(ID == WORKBENCH) title = "Crafting";
		else if(ID == FURNACE) title = "Furnace";
		else if(ID == DISPENSER) title = "Dispenser";

		return title;
		//TODO: Figure out the correct titles for enchant/brewing stand
	}

	// The Notchian way of doing things
	public static void incrementUniqueID()
	{
		if(uniqueID >= 127) uniqueID = 1;
		else uniqueID++;
	}
}
