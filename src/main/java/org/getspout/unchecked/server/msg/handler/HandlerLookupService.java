package org.getspout.unchecked.server.msg.handler;

import org.getspout.api.protocol.Message;
import org.getspout.api.protocol.MessageHandler;
import org.getspout.api.protocol.notch.msg.HandshakeMessage;
import org.getspout.api.protocol.notch.msg.IdentificationMessage;

public final class HandlerLookupService extends org.getspout.api.protocol.HandlerLookupService {

	public HandlerLookupService() {
		super();
	}
	
	static {
		try {
			bind(IdentificationMessage.class, IdentificationMessageHandler.class);
			bind(HandshakeMessage.class, HandshakeMessageHandler.class);
		} catch (Exception ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	protected static <T extends Message> void bind(Class<T> clazz, Class<? extends MessageHandler<T>> handlerClass) throws InstantiationException, IllegalAccessException {
		MessageHandler<T> handler = handlerClass.newInstance();
		handlers.put(clazz, handler);
	}

	@SuppressWarnings("unchecked")
	public <T extends Message> MessageHandler<T> find(Class<T> clazz) {
		return (MessageHandler<T>) handlers.get(clazz);
	}

}
