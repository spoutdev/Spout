/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.event.cause;

import org.spout.api.Spout;
import org.spout.api.event.Cause;
import org.spout.api.exception.MaxCauseChainReached;

/**
 * Base cause class which contains all the logic to handle cause chains, parents, child, master parent / main cause etc.
 */
abstract public class BaseCause<T> implements Cause<T> {
	private Cause parentCause;
	private Cause mainCause;
	private int chainPosition = 0;

	/**
	 * Creates a cause without a parent.
	 */
	public BaseCause() {
		parentCause = null;
		mainCause = null;
	}

	/**
	 * Creates a cause with a parent. If the {@link #chainPosition} is larger than {@link org.spout.api.Engine#getCauseChainMaximum()}
	 * a {@link MaxCauseChainReached} RuntimeException will be thrown and the {@link #parentCause}, {@link #mainCause} and {@link #chainPosition}
	 * reseted.
	 * @param parent
	 */
	public BaseCause(Cause parent) {
		if (parent.getChainPosition() == Spout.getEngine().getCauseChainMaximum()) {
			parentCause = null;
			mainCause = null;
			chainPosition = 0;
			throwException();
		} else {
			if (causeOfSameClass()) {
				parentCause = parent.getParentCause();
			} else {
				parentCause = parent;
			}
			mainCause = parent.getMainCause();
			chainPosition++;
		}
	}

	/**
	 * Gets the position of this cause in the cause chain.
	 * Note: 0 means no parent, values higher than 0 are not in sequence
	 * as same causes are collapsed into one cause but the chain position pointer goes up.
	 * @return position in chain or 0 if no parent
	 */
	public int getChainPosition() {
		return chainPosition;
	}

	/**
	 * Gets the main cause of the cause chain.
	 * The result needs to be checked with instanceOf and casted to the correct cause.
	 * Note: Can be null if there is no parent
	 * @return main cause or null if there is no parent
	 */
	@Override
	public Cause getMainCause() {
		return mainCause;
	}

	/**
	 * Gets the parent cause of this cause in the cause chain.
	 * The result needs to be checked with instanceOf and casted to the correct cause.
	 * Note: Can be null if there is no parent
	 * @return parent cause or null if there is no parent
	 */
	@Override
	public Cause getParentCause() {
		return parentCause;
	}

	/**
	 * Checks if the Class of the parent cause is the same class as the new cause
	 * @return true if class of parent cause and new cause are the same
	 */
	protected abstract boolean causeOfSameClass();

	/**
	 * Throws the {@link MaxCauseChainReached} Exception with the point of the cause
	 */
	protected abstract void throwException();
}
