/*
 * This file is part of SpoutcraftAPI (http://wiki.getspout.org/).
 * 
 * SpoutcraftAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutcraftAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.commons.entity;

import org.getspout.commons.GameMode;
import org.getspout.commons.command.CommandSender;
import org.getspout.commons.gui.InGameHUD;
import org.getspout.commons.player.RenderDistance;
import org.getspout.commons.util.FixedLocation;
import org.getspout.commons.util.Location;

public interface ActivePlayer extends Player, CommandSender  {

	/**
	 * Gets the maximum render view that this player can view
	 * @return maximum render view
	 */
	public RenderDistance getMaximumView();

	/**
	 * Sets the maximum render view that this player can view
	 * @param distance to set
	 */
	public void setMaximumView(RenderDistance distance);

	/**
	 * Gets the minimum render view that this player can view
	 * @return minimum render view
	 */
	public RenderDistance getMinimumView();

	/**
	 * Sets the minimum render view that this player can view
	 * @param distance to set
	 */
	public void setMinimumView(RenderDistance distance);

	/**
	 * The next render distance the player will see if they toggle the view with their view key
	 * @return render view
	 */
	public RenderDistance getNextRenderDistance();

	/**
	 * Gets the current render view
	 * @return current view
	 */
	public RenderDistance getCurrentView();
	
	/**
	 * Sets the current render distance
	 * @param distance to set
	 */
	public void setCurrentView(RenderDistance distance);

	/**
	 * Gets the Main HUD that renders for the player
	 * @return Main Screen
	 */
	public InGameHUD getMainScreen();

	/**
	 * Shows the achievement get window with the given title and message, and with the item of the given id
	 * @param title to show
	 * @param message to show
	 * @param id to render
	 */
	public void showAchievement(String title, String message, int id);

	/**
	 * Shows the achievement get window with the given title and message, and with the item of the given id
	 * @param title to show
	 * @param message to show
	 * @param id to render
	 * @param data to render
	 * @param time to show the achievement window for
	 */
	public void showAchievement(String title, String message, int id, int data, int time);

	/**
	 * The last location that the player clicked at
	 * @return last click location
	 */
	public FixedLocation getLastClickedLocation();

	/**
	 * Sets the compass target
	 * @param loc to set
	 */
	public void setCompassTarget(Location loc);

	/**
	 * Gets the compass target
	 * @return target
	 */
	public Location getCompassTarget();

	/**
	 * Sends a raw chat packet to the server with the message
	 * @param message to send
	 */
	public void sendRawMessage(String message);

	/**
	 * Disconnects from the server, with the given disconnect message
	 * @param message
	 */
	public void disconnect(String message);

	/**
	 * Sends a formatted chat message to the server
	 * @param msg to send
	 */
	public void chat(String msg);

	/**
	 * Has the player execute the chat command
	 * @param command to execute
	 * @return command
	 */
	public boolean performCommand(String command);
	
	/**
	 * Gets whether the player is sprinting or not.
	 *
	 * @return true if player is sprinting.
	 */
	public boolean isSprinting();

	/**
	 * Sets whether the player is sprinting or not.
	 *
	 * @param sprinting true if the player should be sprinting
	 */
	public void setSprinting(boolean sprinting);
	
	 /**
	 * Gets the players current experience points towards the next level
	 *
	 * @return Current experience points
	 */

	public int getExperience();

	/**
	 * Sets the players current experience points
	 *
	 * @param exp New experience points
	 */
	public void setExperience(int exp);

	/**
	 * Gets the players current experience level
	 *
	 * @return Current experience level
	 */
	public int getLevel();

	/**
	 * Sets the players current experience level
	 *
	 * @param level New experience level
	 */
	public void setLevel(int level);

	/**
	 * Gets the players total experience points
	 *
	 * @return Current total experience points
	 */
	public int getTotalExperience();

	/**
	 * Sets the players current experience level
	 *
	 * @param exp New experience level
	 */
	public void setTotalExperience(int exp);

	/**
	 * Gets the players current exhaustion level.
	 * <p>
	 * Exhaustion controls how fast the food level drops. While you have a certain
	 * amount of exhaustion, your saturation will drop to zero, and then your food
	 * will drop to zero.
	 *
	 * @return Exhaustion level
	 */
	public float getExhaustion();

	/**
	 * Sets the players current exhaustion level
	 *
	 * @param value Exhaustion level
	 */
	public void setExhaustion(float value);

	/**
	 * Gets the players current saturation level.
	 * <p>
	 * Saturation is a buffer for food level. Your food level will not drop if you
	 * are saturated > 0.
	 *
	 * @return Saturation level
	 */
	public float getSaturation();

	/**
	 * Sets the players current saturation level
	 *
	 * @param value Exhaustion level
	 */
	public void setSaturation(float value);

	/**
	 * Gets the players current food level
	 *
	 * @return Food level
	 */
	public int getFoodLevel();

	/**
	 * Sets the players current food level
	 *
	 * @param value New food level
	 */
	public void setFoodLevel(int value);

   /**
    * Gets this humans current {@link GameMode}
    *
    * @return Current game mode
    */
   public GameMode getGameMode();

   /**
    * Sets this humans current {@link GameMode}
    *
    * @param mode New game mode
    */
   public void setGameMode(GameMode mode);
}
