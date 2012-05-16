package org.spout.api.signal;

public class SignalObjectDelegate extends SignalObject {
	public void registerSignalD(Signal signal) {
		registerSignal(signal);
	}
	
	public void emitD(Signal signal, Object ...arguments) {
		emit(signal, arguments);
	}
	
	public void emitD(String signal, Object ...arguments) {
		emit(signal, arguments);
	}
}
