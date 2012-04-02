package org.spout.api.protocol.common;

import org.spout.api.protocol.HandlerLookupService;
import org.spout.api.protocol.common.handler.CustomDataMessageHandler;
import org.spout.api.protocol.common.message.CustomDataMessage;


public class CommonBootstrapHandlerLookupService extends HandlerLookupService {
	public CommonBootstrapHandlerLookupService() {
		super();
		try {
			bind(CustomDataMessage.class, CustomDataMessageHandler.class);
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}
}
