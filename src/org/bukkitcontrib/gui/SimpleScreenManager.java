package org.bukkitcontrib.gui;

import java.util.HashMap;
import org.bukkitcontrib.player.ContribPlayer;

public class SimpleScreenManager implements ScreenManager{
	private HashMap<String, Screen> mainScreens = new HashMap<String, Screen>();

	@Override
	public Screen getInGameScreen(ContribPlayer player) {
		Screen inGame = mainScreens.get(player.getName());
		if (inGame == null) {
			inGame = new InGameScreen(player);
			mainScreens.put(player.getName(), inGame);
		}
		return inGame;
	}

}
