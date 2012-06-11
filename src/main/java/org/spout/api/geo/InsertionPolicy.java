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
package org.spout.api.geo;

public enum InsertionPolicy {
	/**
	 * Replaces updates that occur before the given update.<br>
	 * Does not insert unless the block has no other updates, or it replaced at least 1
	 */
	WEAK_REPLACE_SOONER("sooner"),
	/**
	 * Replaces updates that occur before or at the same time as the given update
	 * Does not insert unless the block has no other updates, or it replaced at least 1
	 */
	WEAK_REPLACE_SOONER_EQUAL("soonerEqual"),
	/**
	 * Replaces updates that occur at the same time as the given update
	 * Does not insert unless the block has no other updates, or it replaced at least 1
	 */
	WEAK_REPLACE_EQUAL("equal"),
	/**
	 * Replaces updates that occur after or at the same time as the given update
	 * Does not insert unless the block has no other updates, or it replaced at least 1
	 */
	WEAK_REPLACE_LATER_EQUAL("laterEqual"),
	/**
	 * Replaces updates that occur after the given update
	 * Does not insert unless the block has no other updates, or it replaced at least 1
	 */
	WEAK_REPLACE_LATER("later"),
	/**
	 * Does not replace updates
	 * Does not insert unless the block has no other updates, or it replaced at least 1
	 */
	WEAK_REPLACE_NONE("never"),
	/**
	 * Replaces all updates
	 * Does not insert unless the block has no other updates, or it replaced at least 1
	 */
	WEAK_REPLACE_ALL("always"),
	/**
	 * Replaces updates that occur before the given update
	 */
	FORCE_REPLACE_SOONER("sooner", true),
	/**
	 * Replaces updates that occur before or at the same time as the given update
	 */
	FORCE_REPLACE_SOONER_EQUAL("soonerEqual", true),
	/**
	 * Replaces updates that occur at the same time as the given update
	 */
	FORCE_REPLACE_EQUAL("equal", true),
	/**
	 * Replaces updates that occur after or at the same time as the given update
	 */
	FORCE_REPLACE_LATER_EQUAL("laterEqual", true),
	/**
	 * Replaces updates that occur after the given update
	 */
	FORCE_REPLACE_LATER("later", true),
	/**
	 * Does not replace updates
	 */
	FORCE_REPLACE_NONE("never", true),
	/**
	 * Replaces all updates
	 */
	FORCE_REPLACE_ALL("always", true);
	
	private static LongComparison ip(String type) {
		if (type.equals("sooner")) {
			return InsertionPolicyComparisons.SOONER;
		} else if (type.equals("soonerEqual")) {
			return InsertionPolicyComparisons.SOONER_EQUAL;
		} else if (type.equals("equal")) {
			return InsertionPolicyComparisons.EQUAL;
		} else if (type.equals("laterEqual")) {
			return InsertionPolicyComparisons.LATER_EQUAL;
		} else if (type.equals("later")) {
			return InsertionPolicyComparisons.LATER;
		} else if (type.equals("never")) {
			return InsertionPolicyComparisons.NEVER;
		} else if (type.equals("always")) {
			return InsertionPolicyComparisons.ALWAYS;
		} else {
			throw new IllegalStateException("Unknow insertion type: " + type);
		}
	}
	
	private InsertionPolicy(String policy, boolean force) {
		this.policy = ip(policy);
		this.force = force;
	}	
	private InsertionPolicy(String policy) {
		this(policy, false);
	}
	
	private final LongComparison policy;
	private final boolean force;
	
	public boolean canReplace(long newUpdateTime, long oldUpdateTime) {
		return policy.canReplace(newUpdateTime, oldUpdateTime);
	}
	
	public boolean isForce() {
		return force;
	}
	
	public boolean replaceAll() {
		return this == WEAK_REPLACE_ALL || this == FORCE_REPLACE_ALL;
	}
	
	public boolean replaceNone() {
		return this == WEAK_REPLACE_NONE || this == FORCE_REPLACE_NONE;
	}
	
	public static interface LongComparison {
		
		public boolean canReplace(long newTime, long oldTime);
		
	}
	
}

