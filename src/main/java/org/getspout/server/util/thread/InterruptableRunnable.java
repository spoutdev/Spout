package org.getspout.server.util.thread;

public interface InterruptableRunnable implements Serializable {

	public void run() throws InterruptedException;
	
}
