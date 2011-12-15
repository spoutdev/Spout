package org.getspout.commons.event;

public interface EventExecutor {
	public void execute(Event event) throws EventException;
}
