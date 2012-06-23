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
package org.spout.api.util.config.serialization;

import java.util.Comparator;

/**
 * Handles serializing and deserializing objects for use in annotated configurations.
 */
public abstract class Serializer {
	private boolean allowsNullValue;
	public Object deserialize(GenericType type, Object value) {
		if (value == null && !allowsNullValue()) {
			return null;
		}

		if (isApplicableDeserialize(type, value) && (getParametersRequired() == -1
				|| type.getGenerics().length == getParametersRequired())) {
			return handleDeserialize(type, value);
		} else {
			return null;
		}
	}

	public Object serialize(GenericType type, Object value) {
		if (value == null && !allowsNullValue()) {
			return null;
		}

		if (isApplicableSerialize(type, value) && (getParametersRequired() == -1
				|| type.getGenerics().length == getParametersRequired())) {
			return handleSerialize(type, value);
		} else {
			return null;
		}
	}

	protected abstract Object handleDeserialize(GenericType type, Object value);

	protected Object handleSerialize(GenericType type, Object value) {
		return value;
	}

	public boolean isApplicableDeserialize(GenericType type, Object value) {
		return isApplicable(type);
	}

	public abstract boolean isApplicable(GenericType type);

	public boolean isApplicableSerialize(GenericType type, Object value) {
		return isApplicable(type);
	}

	protected abstract int getParametersRequired();

	public boolean allowsNullValue() {
		return allowsNullValue;
	}

	protected void setAllowsNullValue(boolean allowsNullValue) {
		this.allowsNullValue = allowsNullValue;
	}

	public static class NeededGenericsComparator implements Comparator<Serializer> {

		public int compare(Serializer a, Serializer b) {
			return Integer.valueOf(a.getParametersRequired()).compareTo(b.getParametersRequired());
		}
	}
}
