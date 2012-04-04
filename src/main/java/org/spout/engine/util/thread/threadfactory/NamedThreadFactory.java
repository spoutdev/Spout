package org.spout.engine.util.thread.threadfactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
	
	private final String namePrefix;
	private static final AtomicInteger idCounter = new AtomicInteger();
	
	public NamedThreadFactory(String namePrefix) {
		this.namePrefix = namePrefix;
	}

	@Override
	public Thread newThread(Runnable runnable) {
		return new Thread(runnable, "Executor{" + namePrefix + "-" + idCounter.getAndIncrement() + "}");
	}

	
}
