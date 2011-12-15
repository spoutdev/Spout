package org.getspout.commons.event;

import java.lang.annotation.*;

/**
 * An annotation to mark methods as being event handler methods
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

	Class<? extends org.getspout.commons.event.Event> event();

	Order priority();
}
