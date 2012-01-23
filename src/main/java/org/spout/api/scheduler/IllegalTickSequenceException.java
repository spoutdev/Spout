package org.spout.api.scheduler;

public class IllegalTickSequenceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public IllegalTickSequenceException(int allowedStages, int actualStage) {
		super("Method called during (" + TickStage.getAllStages(actualStage) + 
				") when only (" + TickStage.getAllStages(allowedStages) + ") were allowed");
	}
	
	
}
