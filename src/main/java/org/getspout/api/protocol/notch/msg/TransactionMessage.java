package org.getspout.api.protocol.notch.msg;

import org.getspout.api.protocol.Message;

public final class TransactionMessage extends Message {
	private final int id, transaction;
	private final boolean accepted;

	public TransactionMessage(int id, int transaction, boolean accepted) {
		this.id = id;
		this.transaction = transaction;
		this.accepted = accepted;
	}

	public int getId() {
		return id;
	}

	public int getTransaction() {
		return transaction;
	}

	public boolean isAccepted() {
		return accepted;
	}

	@Override
	public String toString() {
		return "TransactionMessage{id=" + id + ",transaction=" + transaction +",isAccepted=" + accepted + "}";
	}
}
