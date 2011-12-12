/*
 * This file is part of SpoutcraftAPI (http://wiki.getspout.org/).
 * 
 * SpoutcraftAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutcraftAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.commons.event;

/**
 * @author lahwran
 */
public interface Cancellable {

	/**
	 * If an event stops propogating (ie, is cancelled) partway through an even slot, that slot will not cease execution, but future even slots will not be called.
	 * 
	 * @param cancelled True to set event canceled, False to uncancel event.
	 */
	public void setCancelled(boolean cancelled);

	/**
	 * Get event canceled state.
	 * 
	 * @return whether event is cancelled
	 */
	public boolean isCancelled();
}
