package org.bukkitcontrib.gui;

import java.util.UUID;

public class InGameScreen extends GenericScreen implements Screen{
	protected HealthBar health;
	protected BubbleBar bubble;
	protected ChatBar chat;
	protected ChatTextBox chatText;
	protected ArmorBar armor;
	
	public InGameScreen() {
		this.health = new HealthBar();
		this.bubble = new BubbleBar();
		this.chat = new ChatBar();
		this.chatText = new ChatTextBox();
		this.armor = new ArmorBar();
		
		attachWidget(health).attachWidget(bubble).attachWidget(chat).attachWidget(chatText).attachWidget(armor);
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
	
	public UUID getId() {
		return new UUID(0, 0);
	}
	
	public int getHeight() {
		return 240;
	}
	
	public int getWidth() {
		return 427;
	}
	
	public static boolean isCustomWidget(Widget widget) {
		return widget instanceof HealthBar || widget instanceof BubbleBar || widget instanceof ChatTextBox || widget instanceof ChatBar || widget instanceof ArmorBar;
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

}
