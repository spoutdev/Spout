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
package org.spout.api.util.typechecker;

import java.util.Map;

public class MapTypeChecker<K, V, U extends Map<? extends K, ? extends V>> extends TypeChecker<U> {
	private final TypeChecker<? extends K> keyChecker;
	private final TypeChecker<? extends V> valueChecker;

	@SuppressWarnings("unchecked")
	protected MapTypeChecker(Class<? super U> clazz, TypeChecker<? extends K> keyChecker, TypeChecker<? extends V> valueChecker) {
		super((Class<U>) clazz);

		this.keyChecker = keyChecker;
		this.valueChecker = valueChecker;
	}

	@Override
	public U check(Object object) {
		U map = super.check(object);

		for (Map.Entry<?, ?> element : map.entrySet()) {
			keyChecker.check(element.getKey());
			valueChecker.check(element.getValue());
		}

		return map;
	}
}
