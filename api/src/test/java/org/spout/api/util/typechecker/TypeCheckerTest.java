/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.util.typechecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import static org.spout.api.util.typechecker.TypeChecker.tList;
import static org.spout.api.util.typechecker.TypeChecker.tMap;

public class TypeCheckerTest {
	TypeChecker<List<? extends Map<? extends String, ? extends List<? extends Integer>>>> checker = tList(tMap(String.class, tList(Integer.class)));

	@Test
	public void testSuccess() throws Exception {
		List<Map<String, List<Integer>>> source = new ArrayList<>();
		final HashMap<String, List<Integer>> map = new HashMap<>();
		map.put("random key", Arrays.asList(1, 2, 3, 4));
		source.add(map);

		List<? extends Map<? extends String, ? extends List<? extends Integer>>> checked = checker.check((Object) source);

		assertEquals(source, checked);
	}

	@Test (expected = ClassCastException.class)
	public void testFailure1() throws Exception {
		List<String> source = new ArrayList<>();
		source.add("Hi there, I break your program!");

		checker.check((Object) source);
	}

	@Test (expected = ClassCastException.class)
	public void testFailure2() throws Exception {
		List<Map<String, List<Object>>> source = new ArrayList<>();
		final HashMap<String, List<Object>> map = new HashMap<>();

		map.put("random key", Arrays.<Object>asList(1, 2, 3, "Hi there, I break your program!"));
		source.add(map);

		checker.check((Object) source);
	}

	@Test (expected = ClassCastException.class)
	public void testFailure3() throws Exception {
		Set<Map<String, List<Integer>>> source = new HashSet<>();

		checker.check((Object) source);
	}
}
