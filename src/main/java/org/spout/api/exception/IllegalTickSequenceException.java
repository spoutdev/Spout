package org.spout.api.exception;

import org.spout.api.scheduler.TickStage;

public class IllegalTickSequenceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public IllegalTickSequenceException(int allowedStages, int actualStage) {
		super("Method called during (" + TickStage.getAllStages(actualStage) + ") when only (" + TickStage.getAllStages(allowedStages) + ") were allowed");
	}

}
