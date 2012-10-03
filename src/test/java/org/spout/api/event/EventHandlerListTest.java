package org.spout.api.event;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.Test;
import org.spout.api.util.ReflectionUtils;

public class EventHandlerListTest {
	@Test
	public void test() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		List<Class<?>> classes = ReflectionUtils.getClassesForPackage("org.spout.api.event", true);
		for (Class<?> clazz : classes) {
			if (Event.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
				System.out.println("Verifying handlers in " + clazz.getSimpleName());
				
				Method m = clazz.getMethod("getHandlerList", (Class[])null);
				HandlerList list = (HandlerList) m.invoke(null, (Object[])null);
				assertTrue("Expected non null handler list", list != null);
			}
		}
	}
}
