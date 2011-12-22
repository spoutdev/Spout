package org.getspout.unchecked.api.command.annotated;

/**
 * An {@link AnnotatedCommandRegistrationFactory} uses this this class to create
 * a new instance of command objects.
 */
public interface Injector {

	public Object newInstance(Class<?> clazz);
}
