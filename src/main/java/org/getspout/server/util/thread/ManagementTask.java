package org.getspout.server.util.thread;

import java.io.Serializable;

/**
 * This task must support being serialized and then the deserialized object being run instead
 */

public interface ManagementTask extends Serializable {

	public void run(AsyncExecutor executor) throws InterruptedException;
	
}
