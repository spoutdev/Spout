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
package org.spout.server.util;

/**
 * Class for automatically wrapping chat lines while maintaining color.
 */
public final class TextWrapper {
	private static final int[] characterWidths = new int[]{1, 9, 9, 8, 8, 8, 8, 7, 9, 8, 9, 9, 8, 9, 9, 9, 8, 8, 8, 8, 9, 9, 8, 9, 8, 8, 8, 8, 8, 9, 9, 9, 4, 2, 5, 6, 6, 6, 6, 3, 5, 5, 5, 6, 2, 6, 2, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 2, 2, 5, 6, 5, 6, 7, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 4, 6, 6, 3, 6, 6, 6, 6, 6, 5, 6, 6, 2, 6, 5, 3, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 5, 2, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 3, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 3, 6, 6, 6, 6, 6, 6, 6, 7, 6, 6, 6, 2, 6, 6, 8, 9, 9, 6, 6, 6, 8, 8, 6, 8, 8, 8, 8, 8, 6, 6, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 6, 9, 9, 9, 5, 9, 9, 8, 7, 7, 8, 7, 8, 8, 8, 7, 8, 8, 7, 9, 9, 6, 7, 7, 7, 7, 7, 9, 6, 7, 8, 7, 6, 6, 9, 7, 6, 7, 1};
	private static final char COLOR_CHAR = '\u00A7';
	private static final int CHAT_WINDOW_WIDTH = 320;
	private static final int CHAT_STRING_LENGTH = 119;
	private static final String allowedChars = " !\"#$%&\'()*+,-./" + "0123456789:;<=>?" + "@ABCDEFGHIJKLMNO" + "PQRSTUVWXYZ[\\]^_" + "\'abcdefghijklmno" + "pqrstuvwxyz{|}~\u2302" + "\u00C7\u00FC\u00E9\u00E2\u00E4\u00E0\u00E5\u00E7\u00EA\u00EB\u00E8\u00EF\u00EE\u00EC\u00C4\u00C5" + "\u00C9\u00E6\u00C6\u00F4\u00F6\u00F2\u00FB\u00F9\u00FF\u00D6\u00DC\u00F8\u00A3\u00D8\u00D7\u0192";

	/**
	 * Wrap the specified text taking character size and colors into account.
	 * @param text The text to wrap.
	 * @return A String[] containing the wrapped lines.
	 */
	public static String[] wrapText(String text) {
		StringBuilder result = new StringBuilder();
		char currentColor = 'f';
		int lineWidth = 0;
		int lineLength = 0;

		for (int i = 0; i < text.length(); ++i) {
			char ch = text.charAt(i);

			// Check for a color code
			if (ch == COLOR_CHAR && i + 1 < text.length()) {
				// Make sure we're not exceeding the line length
				if (lineLength + 2 > CHAT_STRING_LENGTH) {
					result.append('\n');
					lineLength = 0;
					if (Character.toLowerCase(currentColor) != 'f') {
						result.append(COLOR_CHAR).append(currentColor);
						lineLength += 2;
					}
				}
				currentColor = text.charAt(++i);
				result.append(COLOR_CHAR).append(currentColor);
				lineLength += 2;
				continue;
			}

			int index = allowedChars.indexOf(ch);
			if (index < 0) {
				continue;
			}

			int width = characterWidths[index + 32];

			if (lineLength + 1 > CHAT_STRING_LENGTH || lineWidth + width >= CHAT_WINDOW_WIDTH) {
				result.append('\n');
				lineLength = 0;
				if (Character.toLowerCase(currentColor) != 'f') {
					result.append(COLOR_CHAR).append(currentColor);
					lineLength += 2;
				}
				lineWidth = 0;
			}

			++lineLength;
			lineWidth += width;
			result.append(ch);
		}

		return result.toString().split("\n");
	}
}
