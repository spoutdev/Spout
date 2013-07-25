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
package org.spout.api.util.string;

import org.junit.Assert;
import org.junit.Test;

/* Copyright (c) 2012 Kevin L. Stern
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * Test class for DamerauLevenshteinAlgorithm.
 *
 * @author Kevin L. Stern
 */
public class DamerauLevenshteinAlgorithmTest {
	@Test
	public void test() {
		Assert.assertEquals(6, new DamerauLevenshteinAlgorithm(1, 1, 1, 1)
				.execute("NawKtYu", "tKNwYua"));

		Assert.assertEquals(1, new DamerauLevenshteinAlgorithm(1, 1, 1, 1)
				.execute("Jdc", "dJc"));

		Assert.assertEquals(5, new DamerauLevenshteinAlgorithm(1, 1, 1, 1)
				.execute("sUzSOwx", "zsSxUwO"));

		Assert.assertEquals(7, new DamerauLevenshteinAlgorithm(1, 1, 1, 1)
				.execute("eOqoHAta", "tAeaqHoO"));

		Assert.assertEquals(1, new DamerauLevenshteinAlgorithm(1, 1, 1, 1)
				.execute("glSbo", "lgSbo"));

		Assert.assertEquals(4, new DamerauLevenshteinAlgorithm(1, 1, 1, 1)
				.execute("NJtQKcJE", "cJEtQKJN"));

		Assert.assertEquals(5, new DamerauLevenshteinAlgorithm(1, 1, 1, 1)
				.execute("GitIEVs", "EGItVis"));

		Assert.assertEquals(4, new DamerauLevenshteinAlgorithm(1, 1, 1, 1)
				.execute("MiWK", "WKiM"));
	}

	@Test
	public void testCosts() {
		/*
		 * Test replace cost.
		 */
		Assert.assertEquals(1,
				new DamerauLevenshteinAlgorithm(100, 100, 1, 100).execute("a",
						"b"));
		/*
		 * Test swap cost.
		 */
		Assert.assertEquals(200, new DamerauLevenshteinAlgorithm(100, 100, 100,
				200).execute("ab", "ba"));
		/*
		 * Test delete cost.
		 */
		Assert.assertEquals(1,
				new DamerauLevenshteinAlgorithm(1, 100, 100, 100).execute("aa",
						"a"));
		/*
		 * Test insert cost.
		 */
		Assert.assertEquals(1,
				new DamerauLevenshteinAlgorithm(100, 1, 100, 100).execute("a",
						"aa"));
	}

	@Test
	public void testInvalidCosts() {
		try {
			new DamerauLevenshteinAlgorithm(1, 1, 1, 0);
			Assert.fail();
		} catch (IllegalArgumentException e) {

		}
	}
}