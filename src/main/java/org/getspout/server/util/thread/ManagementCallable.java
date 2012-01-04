package org.getspout.server.util.thread;

import java.io.Serializable;

/**
 * This is a task that returns a value
 *
 * This task must support being serialized and then the deserialized object being run instead.
 *
 * Its return value must also support serialization
 */

public abstract class ManagementCallable<T extends Serializable> extends ManagementTask {

	private static final long serialVersionUID = 1L;

}
