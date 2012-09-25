/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
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
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.event.server.service;

import org.spout.api.plugin.ServiceManager.ServicePriority;
import org.spout.api.plugin.ServiceProvider;

/**
 * Called when a new {@link ServiceProvider} is being registered by the {@link ServiceManager}.
 */
public class ServiceRegisterEvent extends ServiceEvent {
	private ServicePriority priority;

	public ServiceRegisterEvent(ServiceProvider<?> provider, ServicePriority priority) {
		super(provider);
		this.priority = priority;
	}

	/**
	 * Sets the priority to register this service at.
	 * @param priority
	 */
	public void setPriority(ServicePriority priority) {
		this.priority = priority;
	}

	/**
	 * The priority this service is registered at.
	 * @return priority
	 */
	public ServicePriority getPriority() {
		return priority;
	}
}
