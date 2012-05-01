package org.spout.api.event.server.permissions;

import org.spout.api.event.HandlerList;
import org.spout.api.event.Result;
import org.spout.api.event.server.NodeBasedEvent;
import org.spout.api.permissions.PermissionsSubject;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This event is called to gather the PermissionsSubjects with the given permission set.
 * Plugins responsible for managing PermissionsSubjects should add them to the list of subjects.
 */
public class PermissionGetAllWithNodeEvent extends NodeBasedEvent {
	private final Map<PermissionsSubject, Result> receivers = new HashMap<PermissionsSubject, Result>();

	private static final HandlerList handlers = new HandlerList();

	public PermissionGetAllWithNodeEvent(String node) {
		super(node);
	}

	/**
	 * Returns the map of receivers. This map is modified to add applicable receivers
	 *
	 * @return The receivers map
	 */
	public Map<PermissionsSubject, Result> getReceivers() {
		return receivers;
	}

	public Set<PermissionsSubject> getAllowedReceivers() {
		Set<PermissionsSubject> ret = new HashSet<PermissionsSubject>();
		for (Map.Entry<PermissionsSubject, Result> entry : receivers.entrySet()) {
			if (entry.getValue() == Result.ALLOW) {
				ret.add(entry.getKey());
			}
		}
		return Collections.unmodifiableSet(ret);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
