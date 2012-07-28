/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
package org.spout.engine.chat.console;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.html.HTMLStyleHandler;
import org.spout.engine.SpoutEngine;
import org.spout.engine.util.MacOSXUtils;

/**
 * A gui chat panel
 */
public class SwingConsole extends JPanel implements Console, KeyListener, WindowListener {
	private static final long serialVersionUID = -3982648070770129340L;
	public static final int MAX_RECORDS = 500;
	public static final Font TEXT_FONT = Font.getFont(Font.MONOSPACED);
	public static final Color BG_COLOR = Color.BLACK; //new Color(Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue(), 40);
	public static final Color FG_COLOR = Color.WHITE;

	private final SpoutEngine engine;
	private final JTextField cmdInput;
	private final JEditorPane output;
	private final Element bodyElement;
	private final HTMLDocument document;
	private final MessageAdderThread thread;
	private JFrame frame;

	private static final String HTML_PREFIX = "<html>" +
			"<body style=\"color: white; font-family: monospace; font-size: 12pt;\">";
	private static final String HTML_SUFFIX = "</body>" +
			"</html>";

	public SwingConsole(SpoutEngine engine) {
		this.engine = engine;
		setDateFormat(new SimpleDateFormat("E HH:mm:ss"));
		setLayout(new BorderLayout());
		setOpaque(false);
		setBackground(BG_COLOR);

		JPanel entryPanel = applyProperties(new JPanel());
		entryPanel.setLayout(new BorderLayout());
		{
			JLabel prefixLabel = applyProperties(new JLabel("» "));
			prefixLabel.setForeground(FG_COLOR);
			entryPanel.add(prefixLabel, BorderLayout.WEST);

			cmdInput = applyProperties(new JTextField());
			cmdInput.setForeground(FG_COLOR);
			cmdInput.addKeyListener(this);
			cmdInput.setBorder(BorderFactory.createEmptyBorder());
			entryPanel.add(cmdInput, BorderLayout.CENTER);
		}
		entryPanel.setBorder(BorderFactory.createEmptyBorder());
		add(entryPanel, BorderLayout.SOUTH);


		output = applyProperties(new JEditorPane());
		output.setBorder(BorderFactory.createEmptyBorder());
		output.setEditorKit(new HTMLEditorKit());
		output.setText(HTML_PREFIX + HTML_SUFFIX);
		document = (HTMLDocument) output.getDocument();
		Element htmlElement = getElement("html", document);
		assert htmlElement != null;
		bodyElement = getElement("body", htmlElement);

		output.setDragEnabled(false);
		output.setEditable(false);

		JScrollPane scroll = new JScrollPane();
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.setViewportView(output);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setAutoscrolls(true);
		add(scroll, BorderLayout.CENTER);
		cmdInput.grabFocus();
		thread = new MessageAdderThread();
	}


	public void init() {
		if (frame != null) {
			close();
		}

		thread.start();

		frame = new JFrame();
		frame.setTitle("Spout");
		frame.setContentPane(this);
		frame.addWindowListener(this);
		frame.pack();
		frame.setSize(new Dimension(800, 400));
		frame.setVisible(true);
		MacOSXUtils.FullScreenUtilities_setWindowCanFullScreen(frame, true); // I have had issues getting this to do anything so far, if it works have fun
	}

	public void close() {
		if (frame != null) {
			frame.setVisible(false);
			frame.dispose();
			frame = null;
		}
		thread.interrupt();
	}

	private DateFormat dateFormat;
	public void setDateFormat(DateFormat format) {
		this.dateFormat = format;
	}

	private static <T extends Component> T applyProperties(T container) {
		container.setBackground(BG_COLOR);
		container.setFont(TEXT_FONT);
		container.setForeground(FG_COLOR);
		return container;
	}

	public Element getElement(String name, Element source) {
		for (int i = 0; i < source.getElementCount(); ++i) {
			Element testElement = source.getElement(i);
			if (!testElement.isLeaf() && testElement.getName().equalsIgnoreCase(name)) {
				return testElement;
			}
		}
		return null;
	}

	public Element getElement(String name, HTMLDocument source) {
		for (Element testElement : source.getRootElements()) {
			if (!testElement.isLeaf() && testElement.getName().equalsIgnoreCase(name)) {
				return testElement;
			}
		}
		return null;
	}

	private class MessageAdderThread extends Thread {
		public MessageAdderThread() {
			super("SwingConsole message adder");
		}

		private final AtomicInteger recordCount = new AtomicInteger(0);
		private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<String>();

		private void addMessage(String msg) {
			messageQueue.add(msg);
		}

		@Override
		public void run() {
			String msg;
			try {
				while ((msg = messageQueue.take()) != null) {
					if (recordCount.getAndIncrement() >= MAX_RECORDS) {
						recordCount.set(MAX_RECORDS);
						try {
							document.setInnerHTML(bodyElement.getElement(0), "");
						} catch (BadLocationException ignore) {
						} catch (IOException ignore) {
						}
					}

					try {
						document.insertBeforeEnd(bodyElement, "<div>" + msg + "</div>");
						scrollToBottom();
					} catch (BadLocationException ignore) {
					} catch (IOException ignore) {
					}
				}
			} catch (InterruptedException ignore) {
			}
		}
	}

	public void addMessage(ChatArguments message) {
		ChatArguments outputText = new ChatArguments();
		if (dateFormat != null) {
			outputText.append("[").append(dateFormat.format(new Date())).append("] ");
		}
		outputText.append(message.getExpandedPlaceholders());

		thread.addMessage(outputText.asString(HTMLStyleHandler.ID));
	}

	private void scrollToBottom() {
		Rectangle visible = output.getVisibleRect();
		visible.y = output.getHeight();
		output.scrollRectToVisible(visible);
	}

	public void keyTyped(KeyEvent keyEvent) {
		synchronized (cmdInput) {
			if (keyEvent.getKeyChar() == '\n') {
				String line = cmdInput.getText().trim();
				ChatArguments args = new ChatArguments();
				int spaceIndex = line.indexOf(" ");
				String cmd = line;
				if (spaceIndex != -1) {
					cmd = line.substring(0, spaceIndex);
					args.append(line.substring(spaceIndex + 1));
				}
				engine.getCommandSource().sendCommand(cmd, args);
				cmdInput.setText("");
			}
		}
	}

	public void keyPressed(KeyEvent keyEvent) {
	}

	public void keyReleased(KeyEvent keyEvent) {
	}

	public void windowOpened(WindowEvent windowEvent) {
	}

	public void windowClosing(WindowEvent windowEvent) {
		engine.stop();
	}

	public void windowClosed(WindowEvent windowEvent) {
	}

	public void windowIconified(WindowEvent windowEvent) {
	}

	public void windowDeiconified(WindowEvent windowEvent) {
	}

	public void windowActivated(WindowEvent windowEvent) {
	}

	public void windowDeactivated(WindowEvent windowEvent) {
	}
}
