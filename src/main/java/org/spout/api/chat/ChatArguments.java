/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
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
package org.spout.api.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.chat.style.StyleHandler;
import org.spout.api.chat.style.fallback.DefaultStyleHandler;
import org.spout.api.util.SpoutToStringStyle;

/**
 * A class to hold the arguments in a chat message
 */
public class ChatArguments implements Cloneable, ChatSection {
	private final ReentrantLock lock = new ReentrantLock();
	private final ArrayList<Object> elements = new ArrayList<Object>();
	private final Map<Placeholder, Value> placeholders = new HashMap<Placeholder, Value>();
	private StringBuilder plainStringBuilder = new StringBuilder();
	private String plainString = "";

	public ChatArguments(Collection<?> elements) {
		append(elements);
	}

	public ChatArguments(Object... elements) {
		append(elements);
	}

	public ChatArguments(ChatSection... words) {
		append(words);
	}

	public List<Object> getArguments() {
		return Collections.unmodifiableList(elements);
	}

	public ChatArguments append(Collection<?> elements) {
		lock.lock();
		try {
			this.elements.ensureCapacity(this.elements.size() + elements.size());
			for (Object o : elements) {
				append(o);
			}
		} finally {
			lock.unlock();
		}
		return this;
	}

	public ChatArguments append(Object... objects) {
		return append(Arrays.asList(objects));
	}

	public ChatArguments append(final ChatArguments args) {
		return append(args.getArguments());
	}

	public ChatArguments append(final ChatSection section) {
		final AtomicInteger previousIndex = new AtomicInteger();
		int i;
		for (Map.Entry<Integer, List<ChatStyle>> entry : section.getActiveStyles().entrySet()) {
			i = entry.getKey();
			if (entry.getKey() != -1) {
				append(section.getPlainString().substring(previousIndex.getAndSet(i), i));
			}
			append(entry.getValue());
		}
		if (previousIndex.get() < section.length()) {
			append(section.getPlainString().substring(previousIndex.get(), section.getPlainString().length()));
		}
		return this;
	}

	public ChatArguments append(int... elements) {
		for (int i : elements) {
			append(i);
		}
		return this;
	}

	public ChatArguments append(short... elements) {
		for (short i : elements) {
			append(i);
		}
		return this;
	}

	public ChatArguments append(byte... elements) {
		for (byte i : elements) {
			append(i);
		}
		return this;
	}

	public ChatArguments append(long... elements) {
		for (long i : elements) {
			append(i);
		}
		return this;
	}

	public ChatArguments append(boolean... elements) {
		for (boolean i : elements) {
			append(i);
		}
		return this;
	}

	public ChatArguments append(float... elements) {
		for (float i : elements) {
			append(i);
		}
		return this;
	}

	public ChatArguments append(double... elements) {
		for (double i : elements) {
			append(i);
		}
		return this;
	}

	public ChatArguments append(Object o) {
		lock.lock();
		try {
			if (o instanceof Collection<?>) {
				append((Collection<?>) o);
			} else if (o.getClass().isArray() && Object.class.isAssignableFrom(o.getClass().getComponentType())) {
				append((Object[]) o);
			} else if (o instanceof Placeholder) {
				elements.add(o);
				Value value = new Value();
				value.index = elements.size() - 1;
				placeholders.put(((Placeholder) o), value);
				plainStringBuilder.append("{").append(((Placeholder) o).getName()).append('}');
				plainString = plainStringBuilder.toString();
			} else if (o instanceof ChatArguments) {
				append(((ChatArguments) o).getExpandedPlaceholders());
			} else if (o instanceof ChatSection) {
				append((ChatSection) o);
			} else if (o instanceof ChatStyle) {
				elements.add(o);
			} else {
				String oStr = String.valueOf(o);
				if (oStr.length() > 0) {
					elements.add(oStr);
					plainStringBuilder.append(oStr);
					plainString = plainStringBuilder.toString();
				}
			}
		} finally {
			lock.unlock();
		}
		return this;
	}

	private void buildPlainString(List<Object> vals) {
		StringBuilder builder = new StringBuilder();
		for (Object o : vals) {
			if (o instanceof ChatStyle) {
			} else if (o instanceof Placeholder) {
				Value v = placeholders.get(o);
				if (v != null && v.value != null) {
					builder.append(v.value.getPlainString());
				}
			} else {
				builder.append(o);
			}
		}
		plainStringBuilder = builder;
		plainString = builder.toString();
	}

	@Override
	public Map<Integer, List<ChatStyle>> getActiveStyles() {
		int curIndex = 0;
		LinkedHashMap<Integer, List<ChatStyle>> map = new LinkedHashMap<Integer, List<ChatStyle>>();
		for (Object obj : getExpandedPlaceholders()) {
			if (obj instanceof ChatStyle) {
				ChatStyle style = (ChatStyle) obj;
				List<ChatStyle> list = map.get(curIndex);
				if (list == null) {
					list = new ArrayList<ChatStyle>();
					map.put(curIndex, list);
				}
				ChatSectionUtils.removeConflicting(list, style);
				list.add(style);
			} else {
				curIndex += String.valueOf(obj).length();
			}
		}
		return map;
	}

	@Override
	public String getPlainString() {
		return plainString;
	}

	@Override
	public SplitType getSplitType() {
		return SplitType.ALL;
	}

	@Override
	public ChatArguments toChatArguments() {
		return this;
	}

	@Override
	public ChatSection subSection(int startIndex, int endIndex) {
		return new ChatSectionImpl(getSplitType(), getActiveStyles(), getPlainString()).subSection(startIndex, endIndex);
	}

	@Override
	public int length() {
		return getPlainString().length();
	}

	public ChatArguments setPlaceHolder(Placeholder placeHolder, ChatArguments value) {
		lock.lock();
		try {
			if (!placeholders.containsKey(placeHolder)) {
				throw new IllegalArgumentException("Placeholder " + placeHolder.getName() + " is not present in these arguments!");
			}
			placeholders.get(placeHolder).value = value;
			buildPlainString(elements);
		} finally {
			lock.unlock();
		}
		return this;
	}

	public boolean hasPlaceholder(Placeholder placeholder) {
		return placeholders.containsKey(placeholder);
	}

	/**
	 * Expands all the placeholders in {@link #getArguments()} to their values
	 *
	 * @return A {@link List List&lt;Object>} with all the placeholders in these arguments replaced with their correct values
	 */
	public List<Object> getExpandedPlaceholders() throws MissingPlaceholderException {
		lock.lock();
		try {
			if (placeholders.size() == 0) {
				return new ArrayList<Object>(elements);
			}

			List<Object> newList = new ArrayList<Object>(elements.size());
			for (Object obj : elements) {
				if (obj instanceof Placeholder) {
					ChatArguments value = placeholders.get(obj).value;
					if (value == null) {
						throw new MissingPlaceholderException("No value for " + ((Placeholder) obj).getName());
					}
					newList.addAll(value.getExpandedPlaceholders());
				} else {
					newList.add(obj);
				}
			}
			return newList;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Splits this ChatArguments instance into sections
	 *
	 * @param type How these arguments are to be split into sections
	 * @return The split sections
	 */
	public List<ChatSection> toSections(SplitType type) {
		List<ChatSection> sections = new ArrayList<ChatSection>();
		StringBuilder currentWord = new StringBuilder();
		LinkedHashMap<Integer, List<ChatStyle>> map;
		switch (type) {
			case WORD:
				map = new LinkedHashMap<Integer, List<ChatStyle>>();
				int curIndex = 0;
				for (Object obj : getExpandedPlaceholders()) {
					if (obj instanceof ChatStyle) {
						ChatStyle style = (ChatStyle) obj;
						List<ChatStyle> list = map.get(curIndex);
						if (list == null) {
							list = new ArrayList<ChatStyle>();
							map.put(curIndex, list);
						}
						ChatSectionUtils.removeConflicting(list, style);
						list.add(style);
					} else {
						String val = String.valueOf(obj);
						for (int i = 0; i < val.length(); ++i) {
							int codePoint = val.codePointAt(i);
							if (Character.isWhitespace(codePoint)) {
								sections.add(new ChatSectionImpl(type, new LinkedHashMap<Integer, List<ChatStyle>>(map), currentWord.toString()));
								curIndex = 0;
								currentWord = new StringBuilder();
								if (map.size() > 0) {
									final List<ChatStyle> previousStyles = map.containsKey(-1) ? new ArrayList<ChatStyle>(map.get(-1)) : new ArrayList<ChatStyle>();

									for (Map.Entry<Integer, List<ChatStyle>> entry : map.entrySet()) {
										if (entry.getKey() != -1) {
											for (ChatStyle style : entry.getValue()) {
												ChatSectionUtils.removeConflicting(previousStyles, style);
												previousStyles.add(style);
											}
										}
									}
									map.clear();
									map.put(-1, previousStyles);
								}
							} else {
								currentWord.append(val.substring(i, i + 1));
								curIndex++;
							}
						}
					}
				}

				if (currentWord.length() > 0) {
					sections.add(new ChatSectionImpl(type, map, currentWord.toString()));
				}
				break;

			case STYLE_CHANGE:
				StringBuilder curSection = new StringBuilder();
				List<ChatStyle> activeStyles = new ArrayList<ChatStyle>(3);
				for (Object obj : getExpandedPlaceholders()) {
					if (obj instanceof ChatStyle) {
						ChatStyle style = (ChatStyle) obj;
						ChatSectionUtils.removeConflicting(activeStyles, style);
						activeStyles.add(style);

						map = new LinkedHashMap<Integer, List<ChatStyle>>();
						map.put(-1, new ArrayList<ChatStyle>(activeStyles));
						sections.add(new ChatSectionImpl(type, map, curSection.toString()));
						curSection = new StringBuilder();
					} else {
						curSection.append(obj);
					}
				}
				break;

			case ALL:
				return Collections.<ChatSection>singletonList(new ChatSectionImpl(getSplitType(), getActiveStyles(), getPlainString()));

			default:
				throw new IllegalArgumentException("Unknown SplitOption " + type + "!");
		}
		return sections;
	}

	/**
	 * Represents these ChatArguments as a string using {@link DefaultStyleHandler}
	 *
	 * @return These ChatArguments as a string
	 * @see #asString(int)
	 */
	public String asString() {
		return asString(DefaultStyleHandler.ID);
	}

	/**
	 * Starting from end of elements, append strings as they appear. If a ChatStyle appears, apply it to existing text.
	 * If this existing text has already been formatted, check for conflicts
	 * If no conflicts, append existing text to the area to be formatted
	 *
	 * @param handlerId The handlerId to use to get the {@link org.spout.api.chat.style.StyleFormatter StyleFormatters} for ChatStyles
	 * @return The stringified version of this object
	 */
	public String asString(int handlerId) {
		StringBuilder finalBuilder = new StringBuilder();
		StringBuilder singleBuilder = new StringBuilder();
		StyleHandler handler = StyleHandler.get(handlerId);
		ChatStyle previousStyle = null;
		List<Object> values = getExpandedPlaceholders();

		for (int i = values.size() - 1; i >= 0; --i) {
			Object element = values.get(i);
			if (element instanceof ChatStyle) {
				ChatStyle style = (ChatStyle) element;
				if (previousStyle != null) {
					if (previousStyle.conflictsWith(style)) {
						finalBuilder.insert(0, handler.getFormatter(style).format(singleBuilder.toString()));
					} else {
						// oh god teh ugliness
						String formatted = handler.getFormatter(style).format(finalBuilder.toString());
						finalBuilder.delete(0, finalBuilder.length());
						finalBuilder.append(formatted);
						finalBuilder.insert(0, handler.getFormatter(style).format(singleBuilder.toString()));
					}
				} else {
					finalBuilder.insert(0, handler.getFormatter(style).format(singleBuilder.toString()));
				}
				previousStyle = style;
				singleBuilder.delete(0, singleBuilder.length());
			} else {
				singleBuilder.insert(0, handler.escapeString(String.valueOf(element)));
			}
		}

		if (singleBuilder.length() > 0) {
			finalBuilder.insert(0, singleBuilder);
		}
		return finalBuilder.toString();
	}

	/**
	 * Create an instance of ChatArguments by extracting arguments from a string in
	 * the format specified by the given style handler.
	 *
	 * @param str The string to extract styles from
	 * @return The new ChatArguments instance
	 * @see #fromString(String, int)
	 */
	public static ChatArguments fromString(String str) {
		return fromString(str, DefaultStyleHandler.ID);
	}

	/**
	 * Create an instance of ChatArguments by extracting arguments from a string in
	 * the format specified by the given style handler. This method currently just delegates to the StyleHandler,
	 *
	 * @param str The string to extract styles from
	 * @param handlerId The ID of the {@link StyleHandler} to use to extract style information
	 * @return The new ChatArguments instance
	 * @see StyleHandler#extractArguments(String)
	 */
	public static ChatArguments fromString(String str, int handlerId) {
		return StyleHandler.get(handlerId).extractArguments(str);
	}

	/**
	 * Provides a format string representation of a ChatArguments object.
	 * In a format string, parameters surrounded with a  <pre>{{arg}}</pre> are interpreted as ChatStyles
	 * and parameters surrounded with a <pre>{arg}</pre> are interpreted as placeholders.
	 *
	 * @see #fromFormatString(String)
	 * @return The format string
	 */
	public String toFormatString() {
		StringBuilder builder = new StringBuilder();
		for (Object element : elements) {
			if (element instanceof ChatStyle) {
				builder.append("{{").append(((ChatStyle) element).getLookupName()).append("}}");
			} else if (element instanceof Placeholder) {
				builder.append('{').append(((Placeholder) element).getName()).append('}');
			} else {
				builder.append(element);
			}
		}
		return builder.toString();
	}

	private static final Pattern STYLE_FORMAT_PATTERN = Pattern.compile("(?:\\{\\{([^\\}]+)\\}\\})?([^\\{]*)");
	private static final Pattern PLACEHOLDER_FORMAT_PATTERN = Pattern.compile("((?:[^\\{]|\\{\\{)*)(?:\\{([^{][^}]*)\\})?");

	/**
	 * Creates a arguments object with content in a format string
	 * {@link #toFormatString()} describes the format for format strings.
	 *
	 * @see #toFormatString()
	 * @param format The format string. The format for format strings is described in {@link #toFormatString()}
	 * @return The {@link ChatArguments} object created from the format string
	 */
	public static ChatArguments fromFormatString(String format) {
		ChatArguments args = new ChatArguments();
		Matcher matcher = PLACEHOLDER_FORMAT_PATTERN.matcher(format);
		while (matcher.find()) {
			if (matcher.group(1).length() > 0) {
				Matcher matcher2 = STYLE_FORMAT_PATTERN.matcher(matcher.group(1));
				while (matcher2.find()) {
					if (matcher.group(1) != null) {
						ChatStyle style = ChatStyle.byName(matcher2.group(1));
						if (style == null) {
							args.append(matcher2.group(0));
							continue;
						}
						args.append(style);
					}
					args.append(matcher2.group(2));
				}
			}
			if (matcher.group(2) != null) {
				args.append(new Placeholder(matcher.group(2)));
			}
		}
		return args;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
				.append("elements", elements)
				.append("placeholders", placeholders)
				.append("plainString", plainString)
				.toString();
	}
}
