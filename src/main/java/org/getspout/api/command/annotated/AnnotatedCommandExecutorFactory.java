package org.getspout.api.command.annotated;

import java.lang.reflect.Method;

/**
 * Classes that implement this interface are used by {@link AnnotatedCommandRegistrationFactory}s to register commands.
 */
public interface AnnotatedCommandExecutorFactory {
	
	public AnnotatedCommandExecutor getAnnotatedCommandExecutor(Object instance, Method method);
}
