/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.engine.util;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import com.grahamedgecombe.jterminal.JTerminal;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.ConsoleOperations;
import jline.ConsoleReader;
import jline.NullCompletor;
import jline.SimpleCompletor;

import org.spout.api.ChatColor;
import org.spout.api.Engine;
import org.spout.api.command.CommandSource;
import org.spout.api.data.ValueHolder;
import org.spout.api.geo.World;

import org.spout.engine.SpoutServer;

/**
 * A meta-class to handle all logging and input-related console improvements.
 * Portions are heavily based on CraftBukkit.
 */
public final class ConsoleManager {
	private final Engine engine;
	private ConsoleReader reader;
	private ColoredCommandSource source;
	private ConsoleCommandThread thread;
	private final FancyConsoleHandler consoleHandler;
	private final RotatingFileHandler fileHandler;
	private JFrame jFrame = null;
	private JTerminal jTerminal = null;
	private JTextField jInput = null;
	private boolean running = true;
	private boolean jLine = false;

	public ConsoleManager(Engine engine) {
		this.engine = engine;

		consoleHandler = new FancyConsoleHandler();

		String logFile = engine.getLogFile();
		if (new File(logFile).getParentFile() != null) {
			new File(logFile).getParentFile().mkdirs();
		}
		fileHandler = new RotatingFileHandler(logFile);

		consoleHandler.setFormatter(new DateOutputFormatter(new SimpleDateFormat("HH:mm:ss")));
		fileHandler.setFormatter(new DateOutputFormatter(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")));

		Logger logger = Logger.getLogger("");
		for (Handler h : logger.getHandlers()) {
			logger.removeHandler(h);
		}
		logger.addHandler(consoleHandler);
		logger.addHandler(fileHandler);

		try {
			reader = new ConsoleReader();
		} catch (IOException ex) {
			engine.getLogger().log(Level.SEVERE, "Exception initializing console reader: {0}", ex.getMessage());
			ex.printStackTrace();
		}

		Runtime.getRuntime().addShutdownHook(new ServerShutdownThread());

		System.setOut(new PrintStream(new LoggerOutputStream(Level.INFO), true));
		System.setErr(new PrintStream(new LoggerOutputStream(Level.SEVERE), true));
	}

	public ColoredCommandSource getCommandSource() {
		return source;
	}

	public void stop() {
		consoleHandler.flush();
		fileHandler.flush();
		fileHandler.close();
		running = false;
		if (jFrame != null) {
			jFrame.dispose();
		}
	}

	public void setupConsole(String mode) {
		if (mode.equalsIgnoreCase("gui")) {
			JTerminalListener listener = new JTerminalListener();

			jFrame = new JFrame("Spout");
			jTerminal = new JTerminal();
			jInput = new JTextField(80) {
				/**
				 *
				 */
				private static final long serialVersionUID = 620432435961476505L;

				@Override
				public void setBorder(Border border) {
				}
			};
			jInput.paint(null);
			jInput.setFont(new Font("Monospaced", Font.PLAIN, 12));
			jInput.setBackground(Color.BLACK);
			jInput.setForeground(Color.WHITE);
			jInput.setMargin(new Insets(0, 0, 0, 0));
			jInput.addKeyListener(listener);

			JLabel caret = new JLabel("> ");
			caret.setFont(new Font("Monospaced", Font.PLAIN, 12));
			caret.setForeground(Color.WHITE);

			JPanel ipanel = new JPanel();
			ipanel.add(caret, BorderLayout.WEST);
			ipanel.add(jInput, BorderLayout.EAST);
			ipanel.setBorder(BorderFactory.createEmptyBorder());
			ipanel.setBackground(Color.BLACK);
			ipanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			ipanel.setSize(jTerminal.getWidth(), ipanel.getHeight() * 2);

			jFrame.getContentPane().add(jTerminal, BorderLayout.NORTH);
			jFrame.getContentPane().add(ipanel, BorderLayout.SOUTH);
			jFrame.addWindowListener(listener);
			jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			jFrame.setLocationRelativeTo(null);
			jFrame.pack();
			jFrame.setVisible(true);
			consoleHandler.checkJTerminal();
		} else if (mode.equalsIgnoreCase("jline")) {
			jLine = true;
		}

		source = new ColoredCommandSource();
		thread = new ConsoleCommandThread();

		if (jTerminal == null) {
			thread.setDaemon(true);
			thread.start();
		}
	}

	@SuppressWarnings("unchecked")
	public void refreshCommands() {
		for (Completor c : new ArrayList<Completor>(reader.getCompletors())) {
			reader.removeCompletor(c);
		}

		Completor[] list = new Completor[]{new SimpleCompletor(engine.getAllCommands()), new NullCompletor()};
		reader.addCompletor(new ArgumentCompletor(list));
	}

	public String colorize(String string) {
		if (!string.contains("\u00A7")) {
			return string;
		} else if ((!jLine || !reader.getTerminal().isANSISupported()) && jTerminal == null) {
			return ChatColor.strip(string);
		} else {
			return string.replace(ChatColor.RED.toString(), "\033[1;31m")
					.replace(ChatColor.YELLOW.toString(), "\033[1;33m")
					.replace(ChatColor.BRIGHT_GREEN.toString(), "\033[1;32m")
					.replace(ChatColor.CYAN.toString(), "\033[1;36m")
					.replace(ChatColor.BLUE.toString(), "\033[1;34m")
					.replace(ChatColor.PINK.toString(), "\033[1;35m")
					.replace(ChatColor.BLACK.toString(), "\033[0;0m")
					.replace(ChatColor.DARK_GRAY.toString(), "\033[1;30m")
					.replace(ChatColor.DARK_RED.toString(), "\033[0;31m")
					.replace(ChatColor.GOLD.toString(), "\033[0;33m")
					.replace(ChatColor.DARK_GREEN.toString(), "\033[0;32m")
					.replace(ChatColor.DARK_CYAN.toString(), "\033[0;36m")
					.replace(ChatColor.DARK_BLUE.toString(), "\033[0;34m")
					.replace(ChatColor.PURPLE.toString(), "\033[0;35m")
					.replace(ChatColor.GRAY.toString(), "\033[0;37m")
					.replace(ChatColor.WHITE.toString(), "\033[1;37m") + "\033[0m";
		}
	}

	private class ConsoleCommandThread extends Thread {
		@Override
		public void run() {
			String command;
			while (running) {
				try {
					if (jLine) {
						command = reader.readLine(">", null);
					} else {
						command = reader.readLine();
					}

					if (command == null || command.trim().length() == 0) {
						continue;
					}

					engine.getScheduler().scheduleSyncDelayedTask(null, new CommandTask(command.trim()));
				} catch (Exception ex) {
					engine.getLogger().severe("Impossible exception while executing command: " + ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	private AtomicInteger serverShutdownThreadCount = new AtomicInteger(1);

	private class ServerShutdownThread extends Thread {
		public ServerShutdownThread() {
			super("ServerShutdownThread-" + serverShutdownThreadCount.getAndIncrement());
		}

		@Override
		public void run() {
			engine.stop();
		}
	}

	private class CommandTask implements Runnable {
		private String command;

		public CommandTask(String command) {
			this.command = command;
		}

		@Override
		public void run() {
			engine.processCommand(source, command);
		}
	}

	// TODO - convert to command source
	public class ColoredCommandSource implements CommandSource {
		@Override
		public String getName() {
			return "Console";
		}

		@Override
		public boolean sendMessage(String text) {
			engine.getLogger().info(text);
			return true;
		}

		@Override
		public boolean sendRawMessage(String text) {
			engine.getLogger().info(text);
			return true;
		}

		@Override
		public boolean hasPermission(String node) {
			return true;
		}

		@Override
		public boolean isInGroup(String group) {
			return false;
		}

		@Override
		public String[] getGroups() {
			return new String[0];
		}

		@Override
		public boolean isGroup() {
			return false;
		}

		@Override
		public boolean hasPermission(World world, String node) {
			return true;
		}

		@Override
		public ValueHolder getData(String node) {
			return null;
		}
	}

	private class LoggerOutputStream extends ByteArrayOutputStream {
		private final String separator = System.getProperty("line.separator");
		private final Level level;

		public LoggerOutputStream(Level level) {
			super();
			this.level = level;
		}

		@Override
		public synchronized void flush() throws IOException {
			super.flush();
			String record = this.toString();
			super.reset();

			if (record.length() > 0 && !record.equals(separator)) {
				engine.getLogger().logp(level, "LoggerOutputStream", "log" + level, record);
			}
		}
	}

	private class FancyConsoleHandler extends ConsoleHandler {
		public FancyConsoleHandler() {
			checkJTerminal();
		}

		public void checkJTerminal() {
			if (jTerminal != null) {
				setOutputStream(new TerminalOutputStream());
			}
		}

		@Override
		public synchronized void flush() {
			try {
				if (jLine && jTerminal == null) {
					reader.printString(ConsoleOperations.RESET_LINE + "");
					reader.flushConsole();
					super.flush();
					try {
						reader.drawLine();
					} catch (Throwable ex) {
						reader.getCursorBuffer().clearBuffer();
					}
					reader.flushConsole();
				} else {
					super.flush();
				}
			} catch (IOException ex) {
				engine.getLogger().severe("I/O exception flushing console output");
				ex.printStackTrace();
			}
		}
	}

	private class RotatingFileHandler extends StreamHandler {
		private final SimpleDateFormat date;
		private final String logFile;
		private String filename;

		public RotatingFileHandler(String logFile) {
			this.logFile = logFile;
			date = new SimpleDateFormat("yyyy-MM-dd");
			filename = calculateFilename();
			try {
				setOutputStream(new FileOutputStream(filename, true));
			} catch (FileNotFoundException ex) {
				engine.getLogger().log(Level.SEVERE, "Unable to open {0} for writing: {1}", new Object[]{filename, ex.getMessage()});
				ex.printStackTrace();
			}
		}

		@Override
		public synchronized void flush() {
			if (!filename.equals(calculateFilename())) {
				filename = calculateFilename();
				engine.getLogger().log(Level.INFO, "Log rotating to {0}...", filename);
				try {
					setOutputStream(new FileOutputStream(filename, true));
				} catch (FileNotFoundException ex) {
					engine.getLogger().log(Level.SEVERE, "Unable to open {0} for writing: {1}", new Object[]{filename, ex.getMessage()});
					ex.printStackTrace();
				}
			}
			super.flush();
		}

		private String calculateFilename() {
			return logFile.replace("%D", date.format(new Date()));
		}
	}

	private class DateOutputFormatter extends Formatter {
		private final SimpleDateFormat date;

		public DateOutputFormatter(SimpleDateFormat date) {
			this.date = date;
		}

		@Override
		public String format(LogRecord record) {
			StringBuilder builder = new StringBuilder();

			builder.append(date.format(record.getMillis()));
			builder.append(" [");
			builder.append(record.getLevel().getLocalizedName().toUpperCase());
			builder.append("] ");
			builder.append(colorize(formatMessage(record)));
			builder.append('\n');

			if (record.getThrown() != null) {
				StringWriter writer = new StringWriter();
				record.getThrown().printStackTrace(new PrintWriter(writer));
				builder.append(writer.toString());
			}

			return builder.toString();
		}
	}

	private class JTerminalListener implements WindowListener, KeyListener {
		@Override
		public void windowOpened(WindowEvent e) {
		}

		@Override
		public void windowIconified(WindowEvent e) {
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
		}

		@Override
		public void windowActivated(WindowEvent e) {
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
		}

		@Override
		public void windowClosed(WindowEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void windowClosing(WindowEvent e) {
			engine.stop();
		}

		@Override
		public void keyTyped(KeyEvent e) {
			if (e.getKeyChar() == '\n') {
				String command = jInput.getText().trim();
				if (command.length() > 0) {
					engine.getScheduler().scheduleSyncDelayedTask(null, new CommandTask(command));
				}
				jInput.setText("");
			}
		}
	}

	private class TerminalOutputStream extends ByteArrayOutputStream {
		private final String separator = System.getProperty("line.separator");

		@Override
		public synchronized void flush() throws IOException {
			super.flush();
			String record = this.toString();
			super.reset();

			if (record.length() > 0 && !record.equals(separator)) {
				jTerminal.print(record);
				jFrame.repaint();
			}
		}
	}
}
