package org.getspout.api.command.annotated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This allows for annotation-based command registration.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	public String[] aliases();

	public String usage() default "";

	public String desc();

	public String flags() default "";

	public int min() default 0;

	public int max() default -1;
}
