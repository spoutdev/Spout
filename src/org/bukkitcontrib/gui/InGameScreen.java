package org.bukkitcontrib.gui;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkitcontrib.BukkitContrib;
import org.bukkitcontrib.event.screen.ScreenCloseEvent;
import org.bukkitcontrib.event.screen.ScreenOpenEvent;
import org.bukkitcontrib.packet.PacketScreenAction;
import org.bukkitcontrib.packet.PacketWidget;
import org.bukkitcontrib.packet.ScreenAction;
import org.bukkitcontrib.player.ContribCraftPlayer;

public class InGameScreen extends GenericScreen implements InGameHUD{
	protected HealthBar health;
	protected BubbleBar bubble;
	protected ChatBar chat;
	protected ChatTextBox chatText;
	protected ArmorBar armor;
	protected PopupScreen activePopup = null;
	
	public InGameScreen(int playerId) {
		super(playerId);
		this.health = new HealthBar();
		this.bubble = new BubbleBar();
		this.chat = new ChatBar();
		this.chatText = new ChatTextBox();
		this.armor = new ArmorBar();
		
		attachWidget(health).attachWidget(bubble).attachWidget(chat).attachWidget(chatText).attachWidget(armor);
	}
	@Override
	public void onTick() {
		ContribCraftPlayer player = (ContribCraftPlayer)BukkitContrib.getPlayerFromId(playerId);
		if (player != null && player.getVersion() > 17) {
			if (getActivePopup() != null) {
				if (getActivePopup().isDirty()) {
					player.sendPacket(new PacketWidget(getActivePopup(), getId()));
					getActivePopup().setDirty(false);
				}
				getActivePopup().onTick();
			}
		}
		super.onTick();
	}
	
	@Override
	public InGameScreen attachWidget(Widget widget) {
		if (canAttachWidget(widget)) {
			super.attachWidget(widget);
			return this;
		}
		throw new UnsupportedOperationException("Unsupported widget type");
	}
	
	
	@Override
	public boolean updateWidget(Widget widget) {
		if (widget instanceof HealthBar)
			health = (HealthBar)widget;
		else if (widget instanceof BubbleBar)
			bubble = (BubbleBar)widget;
		else if (widget instanceof ChatTextBox)
			chatText = (ChatTextBox)widget;
		else if (widget instanceof ChatBar)
			chat = (ChatBar)widget;
		else if (widget instanceof ArmorBar)
			armor = (ArmorBar)widget;
		return super.updateWidget(widget);
	}

	@Override
	public Screen removeWidget(Widget widget) {
		if (widget instanceof HealthBar)
			throw new UnsupportedOperationException("Cannot remove the health bar. Use setVisible(false) to hide it instead");
		if (widget instanceof BubbleBar)
			throw new UnsupportedOperationException("Cannot remove the bubble bar. Use setVisible(false) to hide it instead");
		if (widget instanceof ChatTextBox)
			throw new UnsupportedOperationException("Cannot remove the chat text box. Use setVisible(false) to hide it instead");
		if (widget instanceof ChatBar)
			throw new UnsupportedOperationException("Cannot remove the chat bar. Use setVisible(false) to hide it instead");
		if (widget instanceof ArmorBar)
			throw new UnsupportedOperationException("Cannot remove the armor bar. Use setVisible(false) to hide it instead");
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
	
	public boolean closePopup() {
		if (getActivePopup() == null) {
			return false;
		}
		ContribCraftPlayer player = (ContribCraftPlayer)BukkitContrib.getPlayerFromId(playerId);
		ScreenCloseEvent event = new ScreenCloseEvent(player, getActivePopup());
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return false;
		}
		player.sendPacket(new PacketScreenAction(ScreenAction.ScreenClose));
		activePopup = null;
		return true;
	}
	
	public HealthBar getHealthBar() {
		return health;
	}
	
	public BubbleBar getBubbleBar() {
		return bubble;
	}
	
	public ChatBar getChatBar() {
		return chat;
	}
	
	public ChatTextBox getChatTextBox() {
		return chatText;
	}
	
	public ArmorBar getArmorBar() {
		return armor;
	}
	
	public PopupScreen getActivePopup() {
		return activePopup;
	}
	
	public boolean attachPopupScreen(PopupScreen screen) {
		if (getActivePopup() == null) {
			ScreenOpenEvent event = new ScreenOpenEvent(screen);
			Bukkit.getServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				return false;
			}
			activePopup = screen;
			screen.setScreen(this);
			((GenericPopup)screen).playerId = this.playerId;
			return true;
		}
		return false;
	}
	
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
}
