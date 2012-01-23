package org.spout.api.scheduler;

public class IllegalTickSequenceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public IllegalTickSequenceException(int allowedStages, int actualStage) {
		super("Method called during (" + TickStages.getAllStages(actualStage) + 
				") when only (" + TickStages.getAllStages(allowedStages) + ") were allowed");
	}
	
	
}
