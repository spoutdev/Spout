/*
 * This file is part of SpoutAPI (http://wwwi.getspout.org/).
 * 
 * SpoutcraftAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutcraftAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spout.api.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.spout.api.Spout;

public class GenericTextProcessor implements TextProcessor {

	protected static final int AVERAGE_CHAR_WIDTH = 6;
	protected static final char CHAR_NULL = '\0';
	protected static final char CHAR_SPACE = ' ';
	protected static final char CHAR_NEWLINE = '\n';
	protected static final String STR_SPACE = String.valueOf(CHAR_SPACE);
	protected static final String STR_NEWLINE = String.valueOf(CHAR_NEWLINE);

	protected int charLimit = 16;
	protected int lineLimit = 1;
	protected int width = 0;
	protected int cursor = 0;
	protected StringBuffer textBuffer = new StringBuffer();
	protected ArrayList<String> formattedText = new ArrayList<String>();
	protected ArrayList<Integer> lineBreaks = new ArrayList<Integer>();
	protected MinecraftFont font = Spout.getClient().getRenderDelegate().getMinecraftFont();

	public GenericTextProcessor() {
	}

	protected boolean cursorUp() {
		int line = getCursorLine();
		if (line > 0) {
			int start = (line == 1) ? 0 : lineBreaks.get(line - 2);
			cursor = start + Math.min(lineBreaks.get(line - 1) - start - 1, cursor - lineBreaks.get(line - 1));
		} else {
			cursor = 0;
		}
		return true;
	}

	protected boolean cursorDown() {
		int line = getCursorLine();
		if (line + 1 < lineBreaks.size()) {
			int start = (line == 0) ? 0 : lineBreaks.get(line - 1);
			cursor = lineBreaks.get(line) + Math.min(lineBreaks.get(line + 1) - lineBreaks.get(line) - 1, cursor - start);
		} else {
			cursor = textBuffer.length();
		}
		return true;
	}

	protected boolean cursorLeft() {
		if (cursor > 0)
			--cursor;
		return true;
	}

	protected boolean cursorRight() {
		if (cursor < textBuffer.length())
			++cursor;
		return true;
	}

	public int getCursor() {
		return cursor;
	}

	public void setCursor(int cursor) {
		this.cursor = cursor;
		correctCursor();
	}

	public int[] getCursor2D() {
		int[] c = new int[2];
		c[0] = getCursorLine();
		c[1] = getCursorOffset(c[0]);
		return c;
	}

	protected void correctCursor() {
		cursor = Math.max(0, Math.min(cursor, textBuffer.length()));
	}

	protected int getCursorLine() {
		for (int i = 0; i < lineBreaks.size(); ++i)
			if (cursor < lineBreaks.get(i))
				return i;

		if (cursor == textBuffer.length() && getCharAt(cursor - 1) == CHAR_NEWLINE)
			return lineBreaks.size();
		return Math.max(0, lineBreaks.size() - 1);
	}

	protected int getCursorOffset(int line) {
		int start = (line < 1) ? 0 : lineBreaks.get(line - 1);
		return cursor - start;
	}

	protected int getPreviousWordPosition(int offset) {
		int i = Math.max(textBuffer.lastIndexOf(STR_SPACE, offset - 1), textBuffer.lastIndexOf(STR_NEWLINE, offset - 1));
		return Math.max(0, i);
	}

	protected int getNextWordPosition(int offset) {
		int i = textBuffer.indexOf(STR_SPACE, offset) + 1;
		if (i == 0)
			i = textBuffer.indexOf(STR_NEWLINE, offset) + 1;
		if (i == 0)
			i = textBuffer.length();
		return i;
	}

	protected boolean isIndexValid(int index) {
		return index > -1 && index < textBuffer.length();
	}

	protected boolean isRangeValid(int start, int end) {
		return start >= 0 && end <= textBuffer.length() && start < end;
	}

	protected boolean isCursorValid() {
		return isIndexValid(cursor);
	}

	protected char getChar() {
		return getCharAt(cursor);
	}

	protected char getCharAt(int position) {
		return (isIndexValid(position)) ? textBuffer.charAt(position) : CHAR_NULL;
	}

	protected boolean deleteChar() {
		return deleteChar(cursor);
	}

	protected boolean deleteChar(int position) {
		if (isRangeValid(position, position + 1)) {
			textBuffer.delete(position, position + 1);
			return formatText();
		}
		return false;
	}

	protected boolean deleteLine() {
		return deleteLine(getCursorLine());
	}

	protected boolean deleteLine(int line) {
		if (line > -1 && lineBreaks.size() > line) {
			int start = (line > 0) ? lineBreaks.get(line - 1) : 0;
			return delete(start, lineBreaks.get(line), start);
		}
		return false;
	}

	protected boolean delete(int start, int end, int cursorPos) {
		if (isRangeValid(start, end)) {
			textBuffer.delete(start, end);
			cursor = cursorPos;
			correctCursor();
			return formatText();
		}
		return false;
	}

	public void clear() {
		lineBreaks.clear();
		formattedText.clear();
		textBuffer.delete(0, textBuffer.length());
		cursor = 0;
	}

	protected boolean insert(char c) {
		if (charLimit > 0 && textBuffer.length() >= charLimit) {
			return false;
		}
		textBuffer.insert(cursor++, c);
		if (!formatText()) { // if function call wasn't successful, revert changes
			deleteChar(--cursor);
			return false;
		}
		return true;
	}

	protected boolean insert(String s) {
		if (s == null || (charLimit > 0 && textBuffer.length() + s.length() >= charLimit)) {
			return false;
		}

		textBuffer.insert(cursor, s);
		if (!formatText()) { // if function call wasn't successful, revert changes
			textBuffer.delete(cursor, cursor + s.length());
			formatText();
			return false;
		}
		cursor += s.length();
		return true;
	}

	public Iterator<String> iterator() {
		return formattedText.iterator();
	}

	public void setText(String str) {
		clear();
		if (str.length() > 0) {
			if (charLimit > 0 && str.length() > charLimit)
				str = str.substring(0, charLimit);

			textBuffer.append(str);
			cursor = str.length();
			formatText();
		}
	}

	protected boolean formatText() {
		StringTokenizer st = new StringTokenizer(textBuffer.toString(), STR_NEWLINE + STR_SPACE, true);
		String word = null;
		int wordWidth = 0;
		int lineWidth = 0;
		int position = 0;
		int positionOld = 0;
		boolean previousSpace = false;
		boolean skipIterator = false;
		final int spaceCharWidth = font.getTextWidth(STR_SPACE);

		// virtually split text in parts that don't exceed the line width
		lineBreaks.clear();
		while (st.hasMoreTokens() || skipIterator) {
			// get word and its length
			if (!skipIterator) {
				word = st.nextToken();
			}
			skipIterator = false;
			wordWidth = font.getTextWidth(word);
			position += word.length();

			// if word is a newline directive, add a linebreak
			if (word.equals(STR_NEWLINE)) {
				lineWidth = 0;
				lineBreaks.add(position);
				continue;
			}
			// allow one whitespace not to be handled as part of the previous word (word-wrapping)
			else if (!previousSpace && word.equals(STR_SPACE)) {
				lineWidth += spaceCharWidth;
				previousSpace = true;
				continue;
			}

			// split very long words
			if (wordWidth > width) {
				int i = word.length();
				while (i > 0 && wordWidth > width)
					wordWidth -= font.getTextWidth(String.valueOf(word.charAt(--i)));
				position = position - word.length() + i;
				lineBreaks.add(position);
				lineWidth = 0;
				word = word.substring(i);
				skipIterator = true;
			}
			// check if this word would exceed the max-width of the line
			else if (lineWidth + wordWidth > width) {
				if (lineBreaks.size() + 1 < lineLimit) {
					lineBreaks.add(position - word.length());
					lineWidth = wordWidth;
				} else
					return false;
			} else {
				lineWidth += wordWidth;
			}
			previousSpace = false;
		}
		// check if we're at line limit
		if (lineBreaks.size() >= lineLimit) {
			return false;
		}

		// create a line break at the end (–> indizes easier)
		if (!lineBreaks.contains(textBuffer.length())) {
			lineBreaks.add(textBuffer.length());
		}

		// split text into parts using the virtual line breaks
		formattedText.clear();
		for (int i = 0; i < lineBreaks.size() && i < lineLimit; ++i) {
			position = lineBreaks.get(i);
			formattedText.add(textBuffer.substring(positionOld, position));
			positionOld = position;
		}
		return true;
	}

	public String getText() {
		return textBuffer.toString();
	}

	public int getMaximumCharacters() {
		return charLimit;
	}

	public void setMaximumCharacters(int max) {
		this.charLimit = max;
	}

	public int getMaximumLines() {
		return lineLimit;
	}

	public void setMaximumLines(int max) {
		this.lineLimit = max;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		if (!formatText()) {
			clear();
		}
	}

	public boolean handleInput(char key, int keyId) {
		boolean ctrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
		if (keyId == Keyboard.KEY_RETURN.getKeyCode()) {
			insert(CHAR_NEWLINE);
			return true;
		}
		if (keyId == Keyboard.KEY_BACK.getKeyCode()) {
			if (ctrl) {
				int p = getPreviousWordPosition(cursor);
				return delete(p, cursor, p);
			} else if (cursor > 0) {
				return deleteChar(--cursor);
			}
			return false;
		}
		if (keyId == Keyboard.KEY_DELETE.getKeyCode()) {
			if (ctrl)
				return delete(cursor, getNextWordPosition(cursor), cursor);
			else
				return deleteChar();
		}
		if (keyId == Keyboard.KEY_LEFT.getKeyCode()) {
			if (ctrl)
				cursor = getPreviousWordPosition(cursor - 1);
			else
				cursorLeft();
			return false;
		}
		if (keyId == Keyboard.KEY_RIGHT.getKeyCode()) {
			if (ctrl)
				cursor = getNextWordPosition(cursor + 1);
			else
				cursorRight();
			return false;
		}
		if (keyId == Keyboard.KEY_UP.getKeyCode()) {
			cursorUp();
			return false;
		}
		if (keyId == Keyboard.KEY_DOWN.getKeyCode()) {
			cursorDown();
			return false;
		}
		if (keyId == Keyboard.KEY_HOME.getKeyCode()) {
			cursor = 0;
			return false;
		}
		if (keyId == Keyboard.KEY_END.getKeyCode()) {
			cursor = textBuffer.length();
			return false;
		}
		if (keyId == Keyboard.KEY_V.getKeyCode()) {
			if (ctrl) {
				return insert(getClipboardString());
			}
		}
		if (keyId == Keyboard.KEY_C.getKeyCode()) {
			if (ctrl) {
				clear();
				return true;
			}
		}
		if (keyId == Keyboard.KEY_D.getKeyCode()) {
			if (ctrl) {
				return deleteLine();
			}
		}
		if (font.isAllowedChar(key)) {
			return insert(key);
		}
		return false;
	}

	private static String getClipboardString() {
		try {
			Transferable transfer = Toolkit.getDefaultToolkit().getSystemClipboard().getContents((Object) null);
			if (transfer != null && transfer.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String var1 = (String) transfer.getTransferData(DataFlavor.stringFlavor);
				return var1;
			}
		} catch (Exception var2) {
			;
		}

		return null;
	}
}
