package org.bukkitcontrib.gui;

import org.bukkitcontrib.player.ContribPlayer;

public class InGameScreen extends GenericScreen implements Screen{
	protected ContribPlayer player;
	protected HealthBar health;
	protected BubbleBar bubble;
	protected ChatBar chat;
	protected ChatTextBox chatText;
	
	public InGameScreen(ContribPlayer player) {
		this.player = player;
		this.health = new HealthBar(player);
		this.bubble = new BubbleBar(player);
		this.chat = new ChatBar(player);
		this.chatText = new ChatTextBox(player);
		
		attachWidget(health).attachWidget(bubble).attachWidget(chat).attachWidget(chatText);
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
		return super.removeWidget(widget);
	}

}
