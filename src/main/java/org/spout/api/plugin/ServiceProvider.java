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
package org.spout.api.plugin;

import org.spout.api.plugin.ServiceManager.ServicePriority;

/**
 * Constructs a service provider from an abstract class or interface.<br/>
 * Plugins shout NOT be constructing their own ServiceProviders, they should be getting providers from the ServiceManager dependent on the implementing class.
 * 
 * @param <T> Service
 */
public class ServiceProvider<T> implements Comparable<ServiceProvider<?>> {

	private Class<T> service;
	private Plugin plugin;
	private T provider;
	private ServicePriority priority;

	public ServiceProvider(Class<T> service, T provider, ServicePriority priority, Plugin plugin) {

		this.service = service;
		this.plugin = plugin;
		this.provider = provider;
		this.priority = priority;
	}

	/**
	 * Get the Class associated with the service provider.
	 *
	 * @return service
	 */
	public Class<T> getService() {
		return service;
	}

	/**
	 * Get the plugin that registered the service provider.
	 *
	 * @return plugin
	 */
	public Plugin getPlugin() {
		return plugin;
	}

	/**
	 * Get the provider which implements the class defined by the service.
	 *
	 * @return T provider
	 */
	public T getProvider() {
		return provider;
	}

	/**
	 * Set the priority for the service provider
	 *
	 * @param priority
	 */
	public ServiceProvider<T> setPriority(ServicePriority priority) {
		this.priority = priority;
		return this;
	}

	/**
	 * Get the priority for the service provider
	 *
	 * @return priority
	 */
	public ServicePriority getPriority() {
		return priority;
	}

	public int compareTo(ServiceProvider<?> other) {
		if (priority.ordinal() == other.getPriority().ordinal()) {
			return 0;
		}

		return priority.ordinal() < other.getPriority().ordinal() ? 1 : -1;
	}
}
