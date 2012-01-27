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

public class InGameScreen extends GenericScreen implements InGameHUD {

	private final VanillaArmorBar armor = new VanillaArmorBar();
	private final VanillaBubbleBar bubble = new VanillaBubbleBar();
	private final ChatBar chat = new ChatBar();
	private final ChatTextBox chatText = new ChatTextBox();
	private final VanillaExpBar exp = new VanillaExpBar();
	private final VanillaHealthBar health = new VanillaHealthBar();
	private final VanillaHungerBar hunger = new VanillaHungerBar();
	private Popup activePopup = null;

	public InGameScreen(int playerId) {
		super(playerId);
		super.addChildren(health, bubble, chat, chatText, armor, hunger, exp);
	}

	@Override
	public Screen insertChild(final int index, final Widget child) {
		if (child instanceof Control) {
			throw new UnsupportedOperationException("Unsupported widget type");
		}
		super.insertChild(index, child);
		return this;
	}

	@Override
	public InGameScreen removeChild(final Widget child) {
		if (child instanceof VanillaHealthBar) {
			throw new UnsupportedOperationException("Cannot remove the health bar. Use setVisible(false) to hide it instead");
		}
		if (child instanceof VanillaBubbleBar) {
			throw new UnsupportedOperationException("Cannot remove the bubble bar. Use setVisible(false) to hide it instead");
		}
		if (child instanceof ChatTextBox) {
			throw new UnsupportedOperationException("Cannot remove the chat text box. Use setVisible(false) to hide it instead");
		}
		if (child instanceof ChatBar) {
			throw new UnsupportedOperationException("Cannot remove the chat bar. Use setVisible(false) to hide it instead");
		}
		if (child instanceof VanillaArmorBar) {
			throw new UnsupportedOperationException("Cannot remove the armor bar. Use setVisible(false) to hide it instead");
		}
		if (child instanceof VanillaHungerBar) {
			throw new UnsupportedOperationException("Cannot remove the hunger bar. Use setVisible(false) to hide it instead");
		}
		if (child instanceof VanillaExpBar) {
			throw new UnsupportedOperationException("Cannot remove the exp bar. Use setVisible(false) to hide it instead");
		}
		super.removeChild(child);
		return this;
	}

	@Override
	public final VanillaHealthBar getHealthBar() {
		return health;
	}

	@Override
	public final VanillaBubbleBar getBubbleBar() {
		return bubble;
	}

	@Override
	public final ChatBar getChatBar() {
		return chat;
	}

	@Override
	public final ChatTextBox getChatTextBox() {
		return chatText;
	}

	@Override
	public final VanillaArmorBar getArmorBar() {
		return armor;
	}

	@Override
	public final VanillaHungerBar getHungerBar() {
		return hunger;
	}

	@Override
	public final VanillaExpBar getExpBar() {
		return exp;
	}

	@Override
	public WidgetType getType() {
		return WidgetType.INGAMESCREEN;
	}

	public static boolean isCustomWidget(final Widget widget) {
		return widget instanceof VanillaArmorBar
				|| widget instanceof VanillaBubbleBar
				|| widget instanceof ChatBar
				|| widget instanceof ChatTextBox
				|| widget instanceof VanillaExpBar
				|| widget instanceof VanillaHealthBar
				|| widget instanceof VanillaHungerBar;
	}

	@Override
	public ScreenType getScreenType() {
		return ScreenType.GAME_SCREEN;
	}

	@Override
	public void toggleSurvivalHUD(final boolean toggle) {
		armor.setVisible(toggle);
		bubble.setVisible(toggle);
		exp.setVisible(toggle);
		health.setVisible(toggle);
		hunger.setVisible(toggle);
	}
}
