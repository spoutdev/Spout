/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.gui;

/**
 * This is the main screen when playing Minecraft.
 *
 * There is no mouse interaction with this screen, so trying to place Controls
 * such as Buttons on it will fail.
 */
public interface InGameHUD extends Screen {

	/**
	 * Gets the armor bar associated with this HUD.
	 * @return armor bar
	 */
	VanillaArmorBar getArmorBar();

	/**
	 * Gets the chat text box associated with this HUD.
	 * @return chat text box
	 */
	ChatTextBox getChatTextBox();

	/**
	 * Gets the chat text bar associated with this HUD.
	 * @return chat bar
	 */
	ChatBar getChatBar();

	/**
	 * Gets the underwater bubble bar associated with this HUD.
	 * @return bubble bar
	 */
	VanillaBubbleBar getBubbleBar();

	/**
	 * Gets the health bar associated with this HUD.
	 * @return health bar
	 */
	VanillaHealthBar getHealthBar();

	/**
	 * Gets the hunger bar associated with this HUD.
	 * @return hunger bar
	 */
	VanillaHungerBar getHungerBar();

	/**
	 * Gets the exp bar associated with this HUD.
	 * @return exp bar
	 */
	VanillaExpBar getExpBar();

	/**
	 * Ease of use method setting all the survival mode HUD elements to setVisible(toggle).
	 * @param toggle true or false
	 */
	void toggleSurvivalHUD(boolean toggle);
}
