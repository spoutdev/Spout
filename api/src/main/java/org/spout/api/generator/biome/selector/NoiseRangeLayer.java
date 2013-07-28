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
package org.spout.api.generator.biome.selector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A layer split into several ranges of noise values provided by an extending class via implementation of {@link #getNoiseValue(int, int, int, int)}. Each range has an element assigned to it. If the
 * elements are not added already sorted, they must be sorted at least once before use using {@link #sortElements()}.
 */
public abstract class NoiseRangeLayer implements BiomeSelectorLayer {
	protected final List<ElementRange> ranges = new ArrayList<>();

	/**
	 * Adds an element to the layer within the range specified by minimum and maximum.
	 *
	 * @param element The element to be added.
	 * @param min The minimum of the range.
	 * @param max The maximum of the range.
	 * @return The layer itself for chained calls.
	 */
	public NoiseRangeLayer addElement(LayeredBiomeSelectorElement element, float min, float max) {
		return addElement(new ElementRange(element, min, max));
	}

	/**
	 * Adds a ranged element to the layer.
	 *
	 * @param element The element to be added.
	 * @return The layer itself for chained calls.
	 */
	public NoiseRangeLayer addElement(ElementRange element) {
		ranges.add(element);
		return this;
	}

	/**
	 * Adds multiple ranged elements to the layer.
	 *
	 * @param elements The element to be added.
	 * @return The layer itself for chained calls.
	 */
	public NoiseRangeLayer addElements(ElementRange... elements) {
		return addElements(Arrays.asList(elements));
	}

	/**
	 * Adds a collection of ranged elements to the layer.
	 *
	 * @param elements The element to be added.
	 * @return The layer itself for chained calls.
	 */
	public NoiseRangeLayer addElements(Collection<ElementRange> elements) {
		ranges.addAll(elements);
		return this;
	}

	/**
	 * Sorts the added ranged elements from smallest to largest using the minimums and maximums. An element is smaller than another if either its minimum or maximum is smaller to the other's
	 * corresponding minimum or maximum. It is equal to the other is both their maximums and minimums are equal. Else it is larger.
	 *
	 * @return The layer itself for chained calls.
	 */
	public NoiseRangeLayer sortElements() {
		Collections.sort(ranges);
		return this;
	}

	/**
	 * Returns the noise value at the specified coordinates using the provided seed.
	 *
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param z The z coordinate.
	 * @param seed The seed for the noise source.
	 * @return The noise value.
	 */
	protected abstract float getNoiseValue(int x, int y, int z, int seed);

	@Override
	public LayeredBiomeSelectorElement pick(int x, int y, int z, long seed) {
		final float value = getNoiseValue(x, y, z, (int) seed);
		for (ElementRange range : ranges) {
			if (range.isInRange(value)) {
				return range.getElement();
			}
		}
		return null;
	}

	/**
	 * Represent a ranged element defined by a minimum and maximum and an element.
	 */
	public static class ElementRange implements Comparable<ElementRange> {
		private final LayeredBiomeSelectorElement element;
		private final float min;
		private final float max;

		/**
		 * Constructs a new ranged element from the element and its minimum and maximum.
		 *
		 * @param element The element for the range.
		 * @param min The minimum of the range.
		 * @param max The maximum of the range.
		 */
		public ElementRange(LayeredBiomeSelectorElement element, float min, float max) {
			this.element = element;
			this.min = min;
			this.max = max;
		}

		private boolean isInRange(float value) {
			return value >= min && value <= max;
		}

		/**
		 * Gets the element for the range.
		 *
		 * @return The element for the range.
		 */
		public LayeredBiomeSelectorElement getElement() {
			return element;
		}

		@Override
		public int compareTo(ElementRange t) {
			if (min < t.min || max < t.max) {
				return -1;
			} else if (min == t.min && max == t.max) {
				return 0;
			}
			return 1;
		}
	}
}
