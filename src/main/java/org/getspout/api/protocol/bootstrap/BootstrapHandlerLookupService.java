package org.getspout.api.protocol.bootstrap;

import org.getspout.api.protocol.Message;
import org.getspout.api.protocol.MessageHandler;
import org.getspout.api.protocol.bootstrap.handler.BootstrapHandshakeMessageHandler;
import org.getspout.api.protocol.bootstrap.handler.BootstrapIdentificationMessageHandler;
import org.getspout.api.protocol.bootstrap.handler.BootstrapPingMessageHandler;
import org.getspout.api.protocol.bootstrap.msg.BootstrapHandshakeMessage;
import org.getspout.api.protocol.bootstrap.msg.BootstrapIdentificationMessage;
import org.getspout.api.protocol.bootstrap.msg.BootstrapPingMessage;

public class BootstrapHandlerLookupService extends org.getspout.api.protocol.HandlerLookupService {

		public BootstrapHandlerLookupService() {
			super();
		}
		
		static {
			try {
				bind(BootstrapIdentificationMessage.class, BootstrapIdentificationMessageHandler.class);
				bind(BootstrapHandshakeMessage.class, BootstrapHandshakeMessageHandler.class);
				bind(BootstrapPingMessage.class, BootstrapPingMessageHandler.class);
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
