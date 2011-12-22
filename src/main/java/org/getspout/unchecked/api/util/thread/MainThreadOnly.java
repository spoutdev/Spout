package org.getspout.unchecked.api.util.thread;

public @interface MainThreadOnly {
	public String author() default "SpoutDev";

	public String version() default "1.0";

	public String shortDescription() default "Indicates that the method may only be called from the main thread";
}
