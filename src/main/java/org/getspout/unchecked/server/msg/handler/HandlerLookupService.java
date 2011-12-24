package org.getspout.unchecked.server.msg.handler;

import java.util.HashMap;
import java.util.Map;

import org.getspout.api.protocol.Message;
import org.getspout.api.protocol.notch.msg.ActivateItemMessage;
import org.getspout.api.protocol.notch.msg.AnimateEntityMessage;
import org.getspout.api.protocol.notch.msg.BlockPlacementMessage;
import org.getspout.api.protocol.notch.msg.ChatMessage;
import org.getspout.api.protocol.notch.msg.CloseWindowMessage;
import org.getspout.api.protocol.notch.msg.DiggingMessage;
import org.getspout.api.protocol.notch.msg.EntityActionMessage;
import org.getspout.api.protocol.notch.msg.GroundMessage;
import org.getspout.api.protocol.notch.msg.HandshakeMessage;
import org.getspout.api.protocol.notch.msg.IdentificationMessage;
import org.getspout.api.protocol.notch.msg.KickMessage;
import org.getspout.api.protocol.notch.msg.PingMessage;
import org.getspout.api.protocol.notch.msg.PositionMessage;
import org.getspout.api.protocol.notch.msg.PositionRotationMessage;
import org.getspout.api.protocol.notch.msg.QuickBarMessage;
import org.getspout.api.protocol.notch.msg.RespawnMessage;
import org.getspout.api.protocol.notch.msg.RotationMessage;
import org.getspout.api.protocol.notch.msg.ServerListPingMessage;
import org.getspout.api.protocol.notch.msg.WindowClickMessage;

public final class HandlerLookupService {
	private static final Map<Class<? extends Message>, MessageHandler<?>> handlers = new HashMap<Class<? extends Message>, MessageHandler<?>>();

	static {
		try {
			bind(IdentificationMessage.class, IdentificationMessageHandler.class);
			bind(HandshakeMessage.class, HandshakeMessageHandler.class);
			bind(ChatMessage.class, ChatMessageHandler.class);
			bind(GroundMessage.class, GroundMessageHandler.class);
			bind(PositionMessage.class, PositionMessageHandler.class);
			bind(RotationMessage.class, RotationMessageHandler.class);
			bind(PositionRotationMessage.class, PositionRotationMessageHandler.class);
			bind(KickMessage.class, KickMessageHandler.class);
			bind(DiggingMessage.class, DiggingMessageHandler.class);
			bind(BlockPlacementMessage.class, BlockPlacementMessageHandler.class);
			bind(WindowClickMessage.class, WindowClickMessageHandler.class);
			bind(CloseWindowMessage.class, CloseWindowMessageHandler.class);
			bind(ActivateItemMessage.class, ActivateItemMessageHandler.class);
			bind(EntityActionMessage.class, EntityActionMessageHandler.class);
			bind(AnimateEntityMessage.class, AnimateEntityMessageHandler.class);
			bind(ServerListPingMessage.class, ServerListPingMessageHandler.class);
			bind(PingMessage.class, PingMessageHandler.class);
			bind(QuickBarMessage.class, QuickBarMessageHandler.class);
			bind(RespawnMessage.class, RespawnMessageHandler.class);
		} catch (Exception ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	private static <T extends Message> void bind(Class<T> clazz, Class<? extends MessageHandler<T>> handlerClass) throws InstantiationException, IllegalAccessException {
		MessageHandler<T> handler = handlerClass.newInstance();
		handlers.put(clazz, handler);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Message> MessageHandler<T> find(Class<T> clazz) {
		return (MessageHandler<T>) handlers.get(clazz);
	}

	private HandlerLookupService() {
	}
}
