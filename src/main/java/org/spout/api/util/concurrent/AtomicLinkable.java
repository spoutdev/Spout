package org.spout.api.util.concurrent;

/**
 * Indicates a class that can be linked with other AtomicLinkable classes to form a single Atomic Object.
 */
public interface AtomicLinkable {

	OptimisticReadWriteLock getLock();
	
}
