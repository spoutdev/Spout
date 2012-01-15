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

import org.spout.api.player.Player;

public class InGameScreen extends AbstractScreen implements InGameHUD {

	private final VanillaArmorBar armor = new VanillaArmorBar();
	private final VanillaBubbleBar bubble = new VanillaBubbleBar();
	private final ChatBar chat = new ChatBar();
	private final ChatTextBox chatText = new ChatTextBox();
	private final VanillaExpBar exp = new VanillaExpBar();
	private final VanillaHealthBar health = new VanillaHealthBar();
	private final VanillaHungerBar hunger = new VanillaHungerBar();
	private PopupScreen activePopup = null;

	public InGameScreen(int playerId) {
		super(playerId);
		super.addChildren(health, bubble, chat, chatText, armor, hunger, exp);
	}

	@Override
	public void onTick() {
		super.onTick();
		Player player = getPlayer();
		if (player != null && player.isSpoutCraftEnabled()) {
			if (getActivePopup() != null) {
				if (getActivePopup().isDirty()) {
					if (!getActivePopup().getType().isNetworkEnabled()) {
						player.sendPacket(new PacketWidget(getActivePopup(), getUID()));
					}
					getActivePopup().setDirty(false);
				}
				getActivePopup().onTick();
			}
		}
	}

	@Override
	public Screen insertChild(int index, Widget child) {
		if (child instanceof Control) {
			throw new UnsupportedOperationException("Unsupported widget type");
		}
		super.insertChild(index, child);
		return this;
	}

	@Override
	public InGameScreen removeChild(Widget child) {
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
	public int getHeight() {
		return 240;
	}

	@Override
	public int getWidth() {
		return 427;
	}

	@Override
	public VanillaHealthBar getHealthBar() {
		return health;
	}

	@Override
	public VanillaBubbleBar getBubbleBar() {
		return bubble;
	}

	@Override
	public ChatBar getChatBar() {
		return chat;
	}

	@Override
	public ChatTextBox getChatTextBox() {
		return chatText;
	}

	@Override
	public VanillaArmorBar getArmorBar() {
		return armor;
	}

	@Override
	public VanillaHungerBar getHungerBar() {
		return hunger;
	}

	@Override
	public VanillaExpBar getExpBar() {
		return exp;
	}

	@Override
	public PopupScreen getActivePopup() {
		return activePopup;
	}

	@Override
	public boolean attachPopupScreen(PopupScreen screen) {
		if (getActivePopup() == null) {
			ScreenOpenEvent event = new ScreenOpenEvent(SpoutManager.getPlayerFromId(playerId), screen, ScreenType.CUSTOM_SCREEN);
			Bukkit.getServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				return false;
			}
			activePopup = screen;
			screen.setDirty(true);
			screen.setScreen(this);
			((GenericPopup) screen).playerId = this.playerId;
			return true;
		}
		return false;
	}

	@Override
	public boolean closePopup() {
		if (getActivePopup() == null) {
			return false;
		}
		Player player = getPlayer();
		ScreenCloseEvent event = new ScreenCloseEvent(player, getActivePopup(), ScreenType.CUSTOM_SCREEN);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return false;
		}
		player.sendPacket(new PacketScreenAction(ScreenAction.Close, ScreenType.CUSTOM_SCREEN));
		activePopup = null;
		return true;
	}

	@Override
	public WidgetType getType() {
		return WidgetType.INGAMESCREEN;
	}

	public void clearPopup() {
		activePopup = null;
	}

	public static boolean isCustomWidget(Widget widget) {
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
	public void toggleSurvivalHUD(boolean toggle) {
		armor.setVisible(toggle);
		bubble.setVisible(toggle);
		exp.setVisible(toggle);
		health.setVisible(toggle);
		hunger.setVisible(toggle);
	}
}
