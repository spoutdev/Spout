package org.spout.api.signal;

import static org.junit.Assert.fail;
import org.junit.Test;

public class SignalTest {

	public boolean gotSignal = false;
	@Test
	public void testSignalWithStringMethod() throws SecurityException, NoSuchMethodException {
		SignalTestClass emittingObject = new SignalTestClass();
		
		Object receiver = new Object() {
			public void onTest(String arg1) {
				gotSignal = true;
			}
		};
		
		emittingObject.subscribe("test", receiver, "onTest");
		
		emittingObject.doSomething("hello");
		
		if(!gotSignal) {
			fail("Did not get a signal");
		}
		
		gotSignal = false;
	}
	
	public class SignalTestClass extends SignalObject {
		{
			registerSignal(new Signal("test", String.class));
		}
		
		public void doSomething(String arg) {
			emit("test", arg);
		}
	}
}

