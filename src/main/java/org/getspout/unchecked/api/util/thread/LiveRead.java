package org.getspout.unchecked.api.util.thread;

public @interface LiveRead {
	public String author() default "SpoutDev";

	public String version() default "1.0";

	public String shortDescription() default "Indicates that this method reads the current value of an object.  " + "This may have adverse performance implications as it requires thread synchronisation with the managing thread.  " + "All previously submitted DelayedWrites should complete before this read returns";
}