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

import java.util.Collection;
import java.util.List;

/**
 * Manages services and service providers. Services are an interface specifying
 * a list of methods that a provider must implement. Providers are
 * implementations of these services. A provider can be queried from the
 * services manager in order to use a service (if one is available). If multiple
 * plugins register a service, then the service with the highest priority takes
 * precedence.
 */
public interface ServiceManager {

	/**
	 * Register a provider of a service.
	 *
	 * @param <T> Provider
	 * @param service service class
	 * @param provider provider to register
	 * @param plugin plugin with the provider
	 * @param priority priority of the provider
	 */
	public <T> void register(Class<T> service, T provider, Plugin plugin, ServicePriority priority);

	/**
	 * Unregister all the providers registered by a particular plugin.
	 *
	 * @param plugin
	 */
	public void unregisterAll(Plugin plugin);

	/**
	 * Unregister a particular provider for a particular service.
	 *
	 * @param service
	 * @param provider
	 */
	public void unregister(Class<?> service, Object provider);

	/**
	 * Unregister a particular provider.
	 *
	 * @param provider
	 */
	public void unregister(Object provider);

	/**
	 * Queries for a provider. This may return if no provider has been
	 * registered for a service. The highest priority provider is returned.
	 *
	 * @param <T>
	 * @param service
	 * @return provider or null
	 */
	public <T> T load(Class<T> service);

	/**
	 * Queries for a provider registration. This may return if no provider has
	 * been registered for a service.
	 *
	 * @param <T>
	 * @param service
	 * @return provider registration or null
	 */
	public <T> ServiceProvider<T> getRegistration(Class<T> service);

	/**
	 * Get registrations of providers for a plugin.
	 *
	 * @param plugin
	 * @return provider registration or null
	 */
	public List<ServiceProvider<?>> getRegistrations(Plugin plugin);

	/**
	 * Get registrations of providers for a service. The returned list is
	 * unmodifiable.
	 *
	 * @param <T>
	 * @param service
	 * @return list of registrations
	 */
	public <T> Collection<ServiceProvider<T>> getRegistrations(Class<T> service);

	/**
	 * Get a list of known services. A service is known if it has registered
	 * providers for it.
	 *
	 * @return list of known services
	 */
	public Collection<Class<?>> getKnownServices();

	/**
	 * Returns whether a provider has been registered for a service. Do not
	 * check this first only to call <code>load(service)</code> later, as that
	 * would be a non-thread safe situation.
	 *
	 * @param <T> service
	 * @param service service to check
	 * @return whether there has been a registered provider
	 */
	public <T> boolean isProvidedFor(Class<T> service);

	/**
	 * Represents various priorities of a provider. The highest priority takes
	 * precedence when getting a service priority.
	 */
	public enum ServicePriority {
		Lowest,
		Low,
		Normal,
		High,
		Highest
	}
}
