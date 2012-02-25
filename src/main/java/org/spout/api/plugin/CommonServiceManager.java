/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.spout.api.Spout;
import org.spout.api.event.server.ServiceRegisterEvent;
import org.spout.api.event.server.ServiceUnregisterEvent;

/**
 * A common service manager.
 *
 */
public class CommonServiceManager implements ServiceManager {

	/**
	 * Map of providers.
	 */
	private final Map<Class<?>, List<ServiceProvider<?>>> providers = new HashMap<Class<?>, List<ServiceProvider<?>>>();

	/**
	 * The service manager instance
	 */
	private static ServiceManager serviceManager;

	public static ServiceManager getInstance() {
		if (serviceManager == null) {
			serviceManager = new CommonServiceManager();
		}
		return serviceManager;
	}

	private CommonServiceManager() {
	}

	/**
	 * Register a provider of a service.
	 *
	 * @param <T> Provider
	 * @param service service class
	 * @param provider provider to register
	 * @param plugin plugin with the provider
	 * @param priority priority of the provider
	 */
	public <T> void register(Class<T> service, T provider, Plugin plugin, ServicePriority priority) {

		synchronized (providers) {
			List<ServiceProvider<?>> registered = providers.get(service);

			if (registered == null) {
				registered = new ArrayList<ServiceProvider<?>>();
				providers.put(service, registered);
			}
			ServiceProvider<T> serviceProvider = new ServiceProvider<T>(service, provider, priority, plugin);
			ServiceRegisterEvent event = Spout.getEventManager().callEvent(new ServiceRegisterEvent(serviceProvider, priority));
			registered.add(serviceProvider.setPriority(event.getPriority()));

			// Make sure that providers are in the right order in order
			// for priorities to work correctly
			Collections.sort(registered);
		}
	}

	/**
	 * Unregister all the providers registered by a particular plugin.
	 *
	 * @param plugin
	 */
	public void unregisterAll(Plugin plugin) {
		synchronized (providers) {
			Iterator<Map.Entry<Class<?>, List<ServiceProvider<?>>>> it = providers.entrySet().iterator();

			try {
				while (it.hasNext()) {
					Map.Entry<Class<?>, List<ServiceProvider<?>>> entry = it.next();
					Iterator<ServiceProvider<?>> it2 = entry.getValue().iterator();

					try {
						// Removed entries for this plugin
						while (it2.hasNext()) {
							ServiceProvider<?> serviceProvider = it2.next();
							if (serviceProvider.getPlugin().equals(plugin)) {
								it2.remove();
								Spout.getEventManager().callEvent(new ServiceUnregisterEvent(serviceProvider));
							}
						}
					} catch (NoSuchElementException e) {
						// Why does Java suck
					}

					// Get rid of the empty list
					if (entry.getValue().size() == 0) {
						it.remove();
					}
				}
			} catch (NoSuchElementException e) {
			}
		}
	}

	/**
	 * Unregister a particular provider for a particular service.
	 *
	 * @param service
	 * @param provider
	 */
	public void unregister(Class<?> service, Object provider) {
		synchronized (providers) {
			Iterator<Map.Entry<Class<?>, List<ServiceProvider<?>>>> it = providers.entrySet().iterator();

			try {
				while (it.hasNext()) {
					Map.Entry<Class<?>, List<ServiceProvider<?>>> entry = it.next();

					// We want a particular service
					if (entry.getKey() != service) {
						continue;
					}

					Iterator<ServiceProvider<?>> it2 = entry.getValue().iterator();

					try {
						// Removed entries that match this provider
						while (it2.hasNext()) {
							ServiceProvider<?> serviceProvider = it2.next();
							if (serviceProvider.getProvider() == provider) {
								it2.remove();
								Spout.getEventManager().callEvent(new ServiceUnregisterEvent(serviceProvider));
							}
						}
					} catch (NoSuchElementException e) {
						// Why does Java suck
					}

					// Get rid of the empty list
					if (entry.getValue().size() == 0) {
						it.remove();
					}
				}
			} catch (NoSuchElementException e) {
			}
		}
	}

	/**
	 * Unregister a particular provider.
	 *
	 * @param provider
	 */
	public void unregister(Object provider) {
		synchronized (providers) {
			Iterator<Map.Entry<Class<?>, List<ServiceProvider<?>>>> it = providers.entrySet().iterator();

			try {
				while (it.hasNext()) {
					Map.Entry<Class<?>, List<ServiceProvider<?>>> entry = it.next();
					Iterator<ServiceProvider<?>> it2 = entry.getValue().iterator();

					try {
						// Removed entries that match this provider
						while (it2.hasNext()) {
							ServiceProvider<?> serviceProvider = it2.next();
							if (serviceProvider.getProvider() == provider) {
								it2.remove();
								Spout.getEventManager().callEvent(new ServiceUnregisterEvent(serviceProvider));
							}
						}
					} catch (NoSuchElementException e) {
						// Why does Java suck
					}

					// Get rid of the empty list
					if (entry.getValue().size() == 0) {
						it.remove();
					}
				}
			} catch (NoSuchElementException e) {
			}
		}
	}

	/**
	 * Queries for a provider. This may return if no provider has been
	 * registered for a service. The highest priority provider is returned.
	 *
	 * @param <T>
	 * @param service
	 * @return provider or null
	 */
	@SuppressWarnings("unchecked")
	public <T> T load(Class<T> service) {
		synchronized (providers) {
			List<ServiceProvider<?>> registered = providers.get(service);

			if (registered == null) {
				return null;
			}

			// This should not be null!
			return (T) registered.get(0).getProvider();
		}
	}

	/**
	 * Queries for a provider registration. This may return if no provider has
	 * been registered for a service.
	 *
	 * @param <T>
	 * @param service
	 * @return provider registration or null
	 */
	@SuppressWarnings("unchecked")
	public <T> ServiceProvider<T> getRegistration(Class<T> service) {
		synchronized (providers) {
			List<ServiceProvider<?>> registered = providers.get(service);

			if (registered == null) {
				return null;
			}

			// This should not be null!
			return (ServiceProvider<T>) registered.get(0);
		}
	}

	/**
	 * Get registrations of providers for a plugin.
	 *
	 * @param plugin
	 * @return provider registration or null
	 */
	public List<ServiceProvider<?>> getRegistrations(Plugin plugin) {
		synchronized (providers) {
			List<ServiceProvider<?>> ret = new ArrayList<ServiceProvider<?>>();

			for (List<ServiceProvider<?>> registered : providers.values()) {
				for (ServiceProvider<?> provider : registered) {
					if (provider.getPlugin() == plugin) {
						ret.add(provider);
					}
				}
			}

			return ret;
		}
	}

	/**
	 * Get registrations of providers for a service. The returned list is
	 * unmodifiable.
	 *
	 * @param <T>
	 * @param service
	 * @return list of registrations
	 */
	@SuppressWarnings("unchecked")
	public <T> Collection<ServiceProvider<T>> getRegistrations(Class<T> service) {
		synchronized (providers) {
			List<ServiceProvider<?>> registered = providers.get(service);

			if (registered == null) {
				return Collections.unmodifiableList(new ArrayList<ServiceProvider<T>>());
			}

			List<ServiceProvider<T>> ret = new ArrayList<ServiceProvider<T>>();

			for (ServiceProvider<?> provider : registered) {
				ret.add((ServiceProvider<T>) provider);
			}

			return Collections.unmodifiableList(ret);
		}
	}

	/**
	 * Get a list of known services. A service is known if it has registered
	 * providers for it.
	 *
	 * @return list of known services
	 */
	public Collection<Class<?>> getKnownServices() {
		return Collections.unmodifiableSet(providers.keySet());
	}

	/**
	 * Returns whether a provider has been registered for a service. Do not
	 * check this first only to call <code>load(service)</code> later, as that
	 * would be a non-thread safe situation.
	 *
	 * @param <T> service
	 * @param service service to check
	 * @return whether there has been a registered provider
	 */
	public <T> boolean isProvidedFor(Class<T> service) {
		return getRegistration(service) != null;
	}
}
