package org.getspout.api.command.annotated;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.getspout.api.command.CommandSource;

public class SimpleAnnotatedCommandExecutorFactory implements AnnotatedCommandExecutorFactory {

	public AnnotatedCommandExecutor getAnnotatedCommandExecutor(Object instance, Method method) {
		return new SimpleAnnotatedCommandExecutor(instance, method);
	}

	public static class SimpleAnnotatedCommandExecutor extends AnnotatedCommandExecutor {

		public SimpleAnnotatedCommandExecutor(Object instance, Method method) {
			super(instance, method);
		}

		@Override
		public List<Object> getAdditionalArgs(CommandSource source, org.getspout.api.command.Command command) {
			return Collections.emptyList();
		}
	}
}
