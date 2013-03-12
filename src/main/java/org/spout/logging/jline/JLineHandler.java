package org.spout.logging.jline;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.ConsoleOperations;
import jline.ConsoleReader;
import jline.NullCompletor;
import org.fusesource.jansi.AnsiConsole;

/**
 * A log handler backed by JLine
 */
public class JLineHandler extends Handler {
	private final ConsoleReader reader;
	private final OutputStreamWriter writer;
	private DateFormat dateFormat;
	private final AtomicBoolean closed = new AtomicBoolean(false);
	private final CommandCallback callback;

	public JLineHandler(CommandCallback callback, List<Completor> completers) {
		this.callback = callback;
		setDateFormat(new SimpleDateFormat("E HH:mm:ss"));

		try {
			reader = new ConsoleReader();
			writer = new OutputStreamWriter(AnsiConsole.out);
		} catch (IOException e) {
			throw new ExceptionInInitializerError(e);
		}

		@SuppressWarnings("unchecked")
		final Collection<Completor> completors = reader.getCompletors();
		for (Completor c : new ArrayList<Completor>(completors)) {
			reader.removeCompletor(c);
		}
		Completor[] list = completers.toArray(new Completor[completers.size() + 1]);
		list[list.length - 1] = new NullCompletor();
		reader.addCompletor(new ArgumentCompletor(list));
		
		ConsoleCommandThread commandThread = new ConsoleCommandThread();
		commandThread.start();
	}

	public void setDateFormat(DateFormat format) {
		this.dateFormat = format;
	}

	public DateFormat getDateFormat() {
		return dateFormat;
	}

	@Override
	public void publish(LogRecord record) {
		try {
			synchronized (writer) {
				reader.printString(ConsoleOperations.RESET_LINE + "\033[K");
				reader.flushConsole();
				appendDateFormat(writer);
				String message;
				Formatter formatter = getFormatter();
				if (formatter != null) {
					message = formatter.format(record);
				} else {
					message = record.getMessage();
				}
				for (String line : message.split("\n")) {
					if (line.trim().length() > 0) {
						writer.write(line.replaceAll("[\r\n]", "") + '\n');
					}
				}
				writer.flush();

				try {
					reader.drawLine();
				} catch (Throwable ex) {
					reader.getCursorBuffer().clearBuffer();
				}
				reader.flushConsole();
			}
		} catch (IOException e) {
			return;
		}
	}

	private class ConsoleCommandThread extends Thread {
		public ConsoleCommandThread() {
			super("ConsoleCommandThread");
			setDaemon(true);
		}

		@Override
		public void run() {
			String command;
			while (!closed.get()) {
				try {
					command = reader.readLine(">", null);

					if (command == null || command.trim().length() == 0) {
						continue;
					}

					callback.handleCommand(command);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private void appendDateFormat(Writer writer) throws IOException {
		DateFormat dateFormat = getDateFormat();
		if (dateFormat != null) {
			writer.write("[");
			writer.write(dateFormat.format(new Date()));
			writer.write("] ");
		}
	}

	@Override
	public void flush() {
		if (!closed.get()) {
			try {
				reader.flushConsole();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void close() throws SecurityException {
		if (closed.compareAndSet(false, true)) {
			closeImpl();
		}
	}

	private void closeImpl() {
		try {
			reader.killLine();
			reader.flushConsole();
		} catch (IOException e) {}
	}

}
