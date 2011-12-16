package org.getspout.server.window;

public class WindowID {
	public static byte uniqueID = 1;

	public static final byte CHEST = 0;
	public static final byte WORKBENCH = 1;
	public static final byte FURNACE = 2;
	public static final byte DISPENSER = 3;
	public static final byte ENCHANTINGTABLE = 4;
	public static final byte BREWINGSTAND = 5;

	// The Notchian way of doing things
	public static void incrementUniqueID() {
		if (uniqueID >= 127) uniqueID = 1;
		else uniqueID++;
	}
}
