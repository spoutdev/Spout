package org.getspout.api.protocol;

import java.util.HashMap;
import java.util.Map;

public class HandlerLookupService {
	protected static final Map<Class<? extends Message>, MessageHandler<?>> handlers = new HashMap<Class<? extends Message>, MessageHandler<?>>();

	protected static <T extends Message> void bind(Class<T> clazz, Class<? extends MessageHandler<T>> handlerClass) throws InstantiationException, IllegalAccessException {
		MessageHandler<T> handler = handlerClass.newInstance();
		handlers.put(clazz, handler);
	}

	@SuppressWarnings("unchecked")
	public <T extends Message> MessageHandler<T> find(Class<T> clazz) {
		return (MessageHandler<T>) handlers.get(clazz);
	}

	protected HandlerLookupService() {
	}
}
