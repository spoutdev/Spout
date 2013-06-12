package org.spout.logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class MessageFormatter extends Formatter{
	@Override
	public String format(LogRecord record) {
		String message = record.getMessage();
		if (record.getParameters() != null) {
			for (int i = 0; i < record.getParameters().length; i++) {
				message = message.replaceAll(new StringBuilder("\\{").append(i).append("}").toString(), (record.getParameters()[i] == null ? "null" : record.getParameters()[i].toString()));
			}
		}
		return message;
	}
}
