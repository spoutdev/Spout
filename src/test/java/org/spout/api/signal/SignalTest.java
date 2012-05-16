package org.spout.api.signal;

import static org.junit.Assert.fail;
import org.junit.Test;

public class SignalTest {

	public boolean gotSignal = false;
	@Test
	public void testSignalWithStringMethod() throws SecurityException, NoSuchMethodException {
		SignalTestClass emittingObject = new SignalTestClass();
		
		Receiver receiver = new Receiver();
		
		emittingObject.subscribe("test", receiver, "onTest");
		
		emittingObject.doSomething("hello");
		
		if(!gotSignal) {
			fail("Did not get a signal");
		}
		
		gotSignal = false;
	}
	
	@Test
	public void testSignalWithoutArguments() throws SecurityException, NoSuchMethodException {
		SignalTestClass emittingObject = new SignalTestClass();
		
		Receiver receiver = new Receiver();
		
		emittingObject.subscribe("clicked", receiver, "onClick");
		
		emittingObject.click();
		
		if(!gotSignal) {
			fail("Did not get a signal");
		}
		
		gotSignal = false;
	}
	
	@Test
	public void testSignalUnsubscribing() throws SecurityException, NoSuchMethodException {
		SignalTestClass emittingObject = new SignalTestClass();
		
		Receiver receiver = new Receiver();
		
		emittingObject.subscribe("clicked", receiver, "onClick");
		emittingObject.unsubscribe("clicked", receiver);
		
		emittingObject.click();
		
		if(gotSignal) {
			fail("Unsubscribed, but still got a signal");
		}
		
		gotSignal = false;
	}
	
	@Test
	public void testSignalUnsubscribingAll() throws SecurityException, NoSuchMethodException {
		SignalTestClass emittingObject = new SignalTestClass();
		
		Receiver receiver = new Receiver();
		
		emittingObject.subscribe("clicked", receiver, "onClick");
		emittingObject.unsubscribe(receiver);
		
		emittingObject.click();
		
		if(gotSignal) {
			fail("Unsubscribed, but still got a signal");
		}
		
		gotSignal = false;
	}
	
	public class Receiver {
		public void onTest(String arg1) {
			gotSignal = true;
		}
		
		public void onClick(){
			gotSignal = true;
		}
	}
	
	public class SignalTestClass extends SignalObject {
		{
			registerSignal(new Signal("test", String.class));
			registerSignal(new Signal("clicked"));
		}
		
		public void doSomething(String arg) {
			emit("test", arg);
		}
		
		public void click() {
			emit("clicked");
		}
	}
}

