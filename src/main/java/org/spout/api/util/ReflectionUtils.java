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
package org.spout.api.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * Various utility methods that deal with reflection
 */
public class ReflectionUtils {

	/**
	 * Get all the public fields in a class, as well as those in its superclasses (excluding {@link Object})
	 *
	 * @param clazz The class to get all fields in
	 * @return The public fields in the class
	 */
	public static List<Field> getFieldsRecur(Class<?> clazz) {
		return getFieldsRecur(clazz, false);
	}

	/**
	 * Get all the public fields in a class, as well as those in its superclasses
	 *
	 * @see Class#getFields()
	 * @param clazz The class to get all fields in
	 * @param includeObject Whether to include fields in {@link Object}
	 * @return The public fields in the class
	 */
	public static List<Field> getFieldsRecur(Class<?> clazz, boolean includeObject) {
		List<Field> fields = new ArrayList<Field>();
		while (clazz != null && (includeObject || !Object.class.equals(clazz))) {
			fields.addAll(Arrays.asList(clazz.getFields()));
			clazz = clazz.getSuperclass();
		}
		return fields;
	}

	/**
	 * Get all the fields in a class, as well as those in its superclasses (excluding {@link Object})
	 *
	 * @param clazz The class to get all fields in
	 * @return The fields in the class
	 */
	public static List<Field> getDeclaredFieldsRecur(Class<?> clazz) {
		return getDeclaredFieldsRecur(clazz, false);
	}

	/**
	 * Get all the fields in a class, as well as those in its superclasses
	 *
	 * @see Class#getDeclaredFields()
	 * @param clazz The class to get all fields in
	 * @param includeObject Whether to include fields in {@link Object}
	 * @return The fields in the class
	 */
	public static List<Field> getDeclaredFieldsRecur(Class<?> clazz, boolean includeObject) {
		List<Field> fields = new ArrayList<Field>();
		while (clazz != null && (includeObject || !Object.class.equals(clazz))) {
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
			clazz = clazz.getSuperclass();
		}
		return fields;
	}

	/**
	 * Attempts to list all the classes in the specified package as determined
	 * by the context class loader
	 * 
	 * @param packageName the package name to search
	 * @param recursive if the search should include all subdirectories
	 * @return a list of classes that exist within that package
	 * @throws ClassNotFoundException if the package had invalid classes, or does not exist
	 */
	public static List<Class<?>> getClassesForPackage(String packageName, boolean recursive) throws ClassNotFoundException {
		ArrayList<File> directories = new ArrayList<File>();
		try {
			ClassLoader cld = Thread.currentThread().getContextClassLoader();
			if (cld == null) {
				throw new ClassNotFoundException("Can't get class loader.");
			}
			String path = packageName.replace('.', '/');
			Enumeration<URL> resources = cld.getResources(path);
			while (resources.hasMoreElements()) {
				File file = new File(URLDecoder.decode(resources.nextElement().getPath(), "UTF-8"));
				directories.add(file);
				if (recursive) {
					findDirs(directories, file);
				}
			}
		} catch (NullPointerException ex) {
			throw new ClassNotFoundException(packageName + " does not appear to be a valid package (Null pointer exception)", ex);
		} catch (UnsupportedEncodingException encex) {
			throw new ClassNotFoundException(packageName + " does not appear to be a valid package (Unsupported encoding)", encex);
		} catch (IOException ioex) {
			throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + packageName, ioex);
		}

		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		for (File directory : directories) {
			if (directory.exists()) {
				String[] files = directory.list();
				for (String file : files) {
					if (file.endsWith(".class")) {
						try {
							String path = directory.getCanonicalPath().replaceAll("/", ".").replaceAll("\\\\", ".");
							int start = path.indexOf(packageName);
							path = path.substring(start, path.length());
							classes.add(Class.forName(path + '.' + file.substring(0, file.length() - 6)));
						} catch (IOException ex) {
							throw new ClassNotFoundException("IOException was thrown when trying to get path for " + file);
						}
					}
				}
			} else {
				throw new ClassNotFoundException(packageName + " (" + directory.getPath() + ") does not appear to be a valid package");
			}
		}
		return classes;
	}	

	/**
	 * Recursively builds a list of all subdirectories
	 * 
	 * @param dirs list to add to
	 * @param dir to search
	 */
	private static void findDirs(List<File> dirs, File dir) {
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				dirs.add(f);
				findDirs(dirs, f);
			}
		}
	}
}
