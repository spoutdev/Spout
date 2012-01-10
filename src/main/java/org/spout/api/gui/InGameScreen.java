/*
 * This file is part of SpoutAPI (http://wwwi.getspout.org/).
 * 
 * Spout API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spout.api.gui;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.spout.api.plugin.Plugin;
import org.spout.api.SpoutManager;
import org.spout.api.event.screen.ScreenCloseEvent;
import org.spout.api.event.screen.ScreenOpenEvent;
import org.spout.api.packet.PacketScreenAction;
import org.spout.api.packet.PacketWidget;
import org.spout.api.packet.ScreenAction;
import org.spout.api.player.SpoutPlayer;

public class InGameScreen extends GenericScreen implements InGameHUD {

	protected HealthBar health;
	protected BubbleBar bubble;
	protected ChatBar chat;
	protected ChatTextBox chatText;
	protected ArmorBar armor;
	protected HungerBar hunger;
	protected ExpBar exp;
	protected PopupScreen activePopup = null;

	public InGameScreen(int playerId) {
		super(playerId);
		this.health = new HealthBar();
		this.bubble = new BubbleBar();
		this.chat = new ChatBar();
		this.chatText = new ChatTextBox();
		this.armor = new ArmorBar();
		this.hunger = new HungerBar();
		this.exp = new ExpBar();

		attachWidget(null, health).attachWidget(null, bubble).attachWidget(null, chat).attachWidget(null, chatText).attachWidget(null, armor).attachWidget(null, hunger).attachWidget(null, exp);

	}

	@Override
	public void onTick() {
		SpoutPlayer player = (SpoutPlayer) SpoutManager.getPlayerFromId(playerId);
		if (player != null && player.isSpoutCraftEnabled()) {
			if (getActivePopup() != null) {
				if (getActivePopup().isDirty()) {
					if (!getActivePopup().getType().isServerOnly()) {
						player.sendPacket(new PacketWidget(getActivePopup(), getId()));
					}
					getActivePopup().setDirty(false);
				}
				getActivePopup().onTick();
			}
		}
		super.onTick();
	}

	@Override
	public InGameScreen attachWidget(Plugin plugin, Widget widget) {
		if (canAttachWidget(widget)) {
			super.attachWidget(plugin, widget);
			return this;
		}
		throw new UnsupportedOperationException("Unsupported widget type");
	}

	@Override
	public boolean updateWidget(Widget widget) {
		if (widget instanceof HealthBar) {
			health = (HealthBar) widget;
		} else if (widget instanceof BubbleBar) {
			bubble = (BubbleBar) widget;
		} else if (widget instanceof ChatTextBox) {
			chatText = (ChatTextBox) widget;
		} else if (widget instanceof ChatBar) {
			chat = (ChatBar) widget;
		} else if (widget instanceof ArmorBar) {
			armor = (ArmorBar) widget;
		} else if (widget instanceof HungerBar) {
			hunger = (HungerBar) widget;
		} else if (widget instanceof ExpBar) {
			exp = (ExpBar) widget;
		}
		return super.updateWidget(widget);
	}

	@Override
	public Screen removeWidget(Widget widget) {
		if (widget instanceof HealthBar) {
			throw new UnsupportedOperationException("Cannot remove the health bar. Use setVisible(false) to hide it instead");
		}
		if (widget instanceof BubbleBar) {
			throw new UnsupportedOperationException("Cannot remove the bubble bar. Use setVisible(false) to hide it instead");
		}
		if (widget instanceof ChatTextBox) {
			throw new UnsupportedOperationException("Cannot remove the chat text box. Use setVisible(false) to hide it instead");
		}
		if (widget instanceof ChatBar) {
			throw new UnsupportedOperationException("Cannot remove the chat bar. Use setVisible(false) to hide it instead");
		}
		if (widget instanceof ArmorBar) {
			throw new UnsupportedOperationException("Cannot remove the armor bar. Use setVisible(false) to hide it instead");
		}
		if (widget instanceof HungerBar) {
			throw new UnsupportedOperationException("Cannot remove the hunger bar. Use setVisible(false) to hide it instead");
		}
		if (widget instanceof ExpBar) {
			throw new UnsupportedOperationException("Cannot remove the exp bar. Use setVisible(false) to hide it instead");
		}
		return super.removeWidget(widget);
	}

	@Override
	public UUID getId() {
		return new UUID(0, 0);
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
	public boolean closePopup() {
		if (getActivePopup() == null) {
			return false;
		}
		SpoutPlayer player = SpoutManager.getPlayerFromId(playerId);
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
	public HealthBar getHealthBar() {
		return health;
	}

	@Override
	public BubbleBar getBubbleBar() {
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
	public ArmorBar getArmorBar() {
		return armor;
	}

	@Override
	public HungerBar getHungerBar() {
		return hunger;
	}

	@Override
	public ExpBar getExpBar() {
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
	public boolean canAttachWidget(Widget widget) {
		if (widget instanceof Screen) {
			return false;
		}
		if (widget instanceof Control) {
			return false;
		}
		return true;
	}

	@Override
	public WidgetType getType() {
		return WidgetType.InGameScreen;
	}

	public void clearPopup() {
		activePopup = null;
	}

	public static boolean isCustomWidget(Widget widget) {
		return widget instanceof HealthBar || widget instanceof BubbleBar || widget instanceof ChatTextBox || widget instanceof ChatBar || widget instanceof ArmorBar;
	}

	@Override
	public ScreenType getScreenType() {
		return ScreenType.GAME_SCREEN;
	}

	@Override
	public void toggleSurvivalHUD(boolean toggle) {
		health.setVisible(toggle);
		bubble.setVisible(toggle);
		armor.setVisible(toggle);
		hunger.setVisible(toggle);
		exp.setVisible(toggle);
	}
}
