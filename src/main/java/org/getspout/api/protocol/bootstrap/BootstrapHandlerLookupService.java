/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
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
