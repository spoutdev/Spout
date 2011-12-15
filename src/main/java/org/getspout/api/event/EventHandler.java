package org.getspout.api.event;

import java.lang.annotation.*;

/**
 * An annotation to mark methods as being event handler methods
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

	Class<? extends org.getspout.api.event.Event> event();

	Order priority();
}
