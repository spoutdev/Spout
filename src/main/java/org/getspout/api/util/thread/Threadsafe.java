package org.getspout.api.util.thread;

public @interface Threadsafe {
	public String author() default "SpoutDev";

	public String version() default "1.0";

	public String shortDescription() default "Indicates that the method is inherently thread-safe.";
}
