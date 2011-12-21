/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.addon.java;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.getspout.api.plugin.RestrictedClassException;

public class AddonClassLoader extends URLClassLoader {
	private final JavaAddonLoader loader;
	private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();

	public AddonClassLoader(final JavaAddonLoader loader, final URL[] urls, final ClassLoader parent) {
		super(urls, parent);

		this.loader = loader;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (name.startsWith("net.minecraft")) {
			throw new RestrictedClassException("Accessing net.minecraft is not allowed");
		}
		return findClass(name, true);
	}

	protected Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
		Class<?> result = classes.get(name);

		if (result == null) {
			if (checkGlobal) {
				result = loader.getClassByName(name);
			}

			if (result == null) {
				result = super.findClass(name);

				if (result != null) {
					loader.setClass(name, result);
				}
			}

			classes.put(name, result);
		}

		return result;
	}

	public Set<String> getClasses() {
		return classes.keySet();
	}
}
