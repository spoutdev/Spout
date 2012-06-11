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
package org.spout.api.util.concurrent;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class AtomicIntegerHelperTest {
	
	@Test
	public void test() {
		
		AtomicInteger i = new AtomicInteger();
		
		i.set(0xFF);
		
		assertTrue("Unable to set bits which were zero", AtomicIntegerHelper.setBit(i, 0xF00));
		
		assertEquals("Integer not correctly updated", i.get(), 0xFFF);
		
		assertFalse("Successfully set bits that were one", AtomicIntegerHelper.setBit(i, 0x0F0));
		
		assertEquals("Integer changed value", i.get(), 0xFFF);
		
		assertTrue("Unable to clear bits which were one", AtomicIntegerHelper.clearBit(i, 0x060));
		
		assertEquals("Integer not correctly updated", i.get(), 0xF9F);
		
		assertFalse("Successfully cleared bits that were zero", AtomicIntegerHelper.clearBit(i, 0x1000));

		assertFalse("Successfully cleared bits that were zero", AtomicIntegerHelper.clearBit(i, 0x0040));
		
		assertEquals("Integer changed value", i.get(), 0xF9F);
		
	}

}
