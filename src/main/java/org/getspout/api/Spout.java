package org.getspout.api;

/**
 * Represents the Spout core, to get singleton {@link Game} instance
 * @author Cameron
 *
 */
public final class Spout {
	private static Game instance = null;
	private Spout() {
		throw new IllegalStateException("Can not construct Spout instance");
	}
	
	public static void setGame(Game game){
		if (instance == null) {
			instance = game;
		}
		else {
			throw new UnsupportedOperationException("Can not redefine singleton Game instance");
		}
	}
	
	public static Game getGame() {
		return instance;
	}

}
