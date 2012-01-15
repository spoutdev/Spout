/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.xml.bind.TypeConstraintException;
import org.spout.api.packet.PacketUtil;
import org.spout.api.plugin.Plugin;
import org.spout.api.util.Color;

public abstract class AbstractWidget /*extends AbstractEventSource*/ implements Widget {

	/** Name of the default plugin when none is set. */
	public static final String PLUGIN = "Spoutcraft";
	/** Current version for serialisation and packet handling.*/
	private static final long serialVersionUID = 5L;
	/** Used for generating unique ids, numbers below 16 are reserved for static widgets. */
	private static int lastId = 0xf;
	/** Position. */
	private int x = 0, y = 0;
	/** Dimensions. */
	private int width = 50, height = 50;
	private boolean visible = true;
	private transient boolean dirty = true;
	private byte priority = 0;
	private int id = -1;
	private String tooltip = "";
	private String plugin = PLUGIN;
	private WidgetAnchor anchor = WidgetAnchor.SCALE;
	private Overflow overflow = Overflow.VISIBLE;
	// Layout
	private Container parent = null;
	private boolean fixed = false;
	private final Box margin, padding;
	private final BoxColor border;
	private int minWidth = 0, maxWidth = 427, minHeight = 0, maxHeight = 240;
	private boolean autoDirty = true;
	private Display display = Display.INLINE;
	private Position position = Position.STATIC;
	private transient boolean hasPosition = false;
	private transient boolean hasSize = false;
	// Animation
	private WidgetAnim animType = WidgetAnim.NONE;
	private float animValue = 1f;
	private short animCount = 0;
	private short animTicks = 20;
	private static final byte ANIM_REPEAT = (1 << 0);
	private static final byte ANIM_RESET = (1 << 1);
	private static final byte ANIM_RUNNING = (1 << 2);
	private static final byte ANIM_STOPPING = (1 << 3);
	private byte animFlags = 0;
	private transient int animTick = 0; // Current tick
	private transient int animFrame = 0; // Current frame

	public AbstractWidget() {
		margin = new WidgetBox(this);
		padding = new WidgetBox(this);
		border = new WidgetBoxColor(this);
	}

	public AbstractWidget(int width, int height) {
		this();
		this.width = width;
		this.height = height;
	}

	public AbstractWidget(int X, int Y, int width, int height) {
		this(width, height);
		this.x = X;
		this.y = Y;
	}

	@Override
	public int getNumBytes() {
		return 48 + PacketUtil.getNumBytes(tooltip) + PacketUtil.getNumBytes(plugin != null ? plugin : PLUGIN);
	}

	@Override
	public int getVersion() {
		return (int) serialVersionUID;
	}

	@Override
	public Widget setAnchor(WidgetAnchor anchor) {
		if (anchor != null && !getAnchor().equals(anchor)) {
			this.anchor = anchor;
			autoDirty();
		}
		return this;
	}

	@Override
	public WidgetAnchor getAnchor() {
		return anchor;
	}

	@Override
	public Plugin getPlugin() {
//		return Spout.getServer().getPluginManager().getPlugin(getPluginName());
		return null;
	}

	@Override
	public String getPluginName() {
		return plugin;
	}

	@Override
	public Widget setPlugin(Plugin plugin) {
		return setPlugin(plugin == null ? null : plugin.getDescription().getName());
	}

	@Override
	public Widget setPlugin(String name) {
		if (name == null || name.length() == 0) {
			plugin = PLUGIN;
		}
		if (!this.plugin.equals(name)) {
			this.plugin = name;
			autoDirty();
		}
		return this;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		setX(input.readInt()); // 0 + 4 = 4
		setY(input.readInt()); // 4 + 4 = 8
		setWidth(input.readInt()); // 8 + 4 = 12
		setHeight(input.readInt()); // 12 + 4 = 16
		setAnchor(WidgetAnchor.getAnchorFromId(input.readByte())); // 6 + 1 = 17
		setVisible(input.readBoolean()); // 17 + 1 = 18
		setPriority(input.readByte()); // 18 + 1 = 19
		this.id = input.readInt(); // 22 + 4 = 26
		setTooltip(PacketUtil.readString(input)); // String
		setPlugin(PacketUtil.readString(input)); // String
		animType = WidgetAnim.getAnimationFromId(input.readByte()); // 38 + 1 + 39
		animFlags = input.readByte(); // 39 + 1 = 40
		animValue = input.readFloat(); // 40 + 4 = 44
		animTicks = input.readShort(); // 44 + 2 = 46
		animCount = input.readShort(); // 46 + 2 = 48
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		output.writeInt(getX()); // 0 + 4 = 4
		output.writeInt(getY()); // 4 + 4 = 8
		output.writeInt(getWidth()); // 8 + 4 = 12
		output.writeInt(getHeight()); // 12 + 4 = 16
		output.writeByte(getAnchor().getId()); // 16 + 1 = 17
		output.writeBoolean(isVisible()); // 17 + 1 = 18
		output.writeByte(priority); // 18 + 1 = 19
		output.writeInt(getUID()); // 22 + 4 = 26
		PacketUtil.writeString(output, getTooltip()); // String
		PacketUtil.writeString(output, plugin != null ? plugin : PLUGIN); // String
		output.writeByte(animType.getId()); // 38 + 1 = 39
		output.writeByte(animFlags); // 39 + 1 = 40
		output.writeFloat(animValue); // 40 + 4 = 44
		output.writeShort(animTicks); // 44 + 2 = 46
		output.writeShort(animCount); // 46 + 2 = 48
	}

	@Override
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Set the default widget id, this must be a number lower than 255 for
	 * static widget ids.
	 * @param id to use
	 */
	protected void setUID(int id) {
		if (id >= 0xf) {
			throw new UnsupportedOperationException("Static widget ids need to be under 16.");
		}
		this.id = id;
	}

	@Override
	final public int getUID() {
		if (id == -1) {
			id = lastId++;
		}
		return id;
	}

	@Override
	public byte getPriority() {
		return priority;
	}

	@Override
	public Widget setPriority(byte priority) {
		if (priority != getPriority()) {
			this.priority = priority;
			autoDirty();
		}
		return this;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public Widget setWidth(int width) {
		hasSize = true;
		width = Math.max(getMinWidth(), Math.min(width, getMaxWidth()));
		if (getWidth() != width) {
			this.width = width;
			updateSize();
			autoDirty();
		}
		return this;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public Widget setHeight(int height) {
		hasSize = true;
		height = Math.max(getMinHeight(), Math.min(height, getMaxHeight()));
		if (getHeight() != height) {
			this.height = height;
			updateSize();
			autoDirty();
		}
		return this;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public Widget setX(int pos) {
		hasPosition = true;
		if (getX() != pos) {
			x = pos;
			autoDirty();
		}
		return this;
	}

	@Override
	public Widget setY(int pos) {
		hasPosition = true;
		if (getY() != pos) {
			y = pos;
			autoDirty();
		}
		return this;
	}

	@Override
	public Widget shiftXPos(int modX) {
		setX(getX() + modX);
		return this;
	}

	@Override
	public Widget shiftYPos(int modY) {
		setY(getY() + modY);
		return this;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public Widget setVisible(boolean enable) {
		if (isVisible() != enable) {
			visible = enable;
			updateSize();
			if (hasParent()) {
				getParent().deferLayout();
			}
			autoDirty();
		}
		return this;
	}

	@Override
	public int hashCode() {
		return getUID();
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Widget && other.hashCode() == hashCode();
	}

	@Override
	public void onTick() {
	}

	@Override
	public Widget setTooltip(String t) {
		if (t != null && !getTooltip().equals(t)) {
			tooltip = t;
			autoDirty();
		}
		return this;
	}

	@Override
	public String getTooltip() {
		return tooltip;
	}

	@Override
	public Container getParent() {
		return parent;
	}

	@Override
	public boolean hasParent() {
		return getParent() != null;
	}

	@Override
	public void setParent(Container parent) {
		if (hasParent() && parent != null && !getParent().equals(parent)) {
			getParent().removeChild(this);
		}
		this.parent = parent;
	}

	@Override
	public Container getScreen() {
		if (hasParent()) {
			if (getParent() instanceof Screen) {
				return getParent();
			}
			return getParent().getScreen();
		}
		return null;
	}

	@Override
	public boolean hasScreen() {
		return getScreen() != null;
	}

	@Override
	public Widget setFixed(boolean fixed) {
		if (isFixed() != fixed) {
			this.fixed = fixed;
			updateSize();
		}
		return this;
	}

	@Override
	public boolean isFixed() {
		return fixed;
	}

	public Widget setDisplay(Display display) {
		if (this.display != display) {
			this.display = display;
			autoDirty();
		}
		return this;
	}

	public Display getDisplay() {
		return display;
	}

	public Widget setPosition(Position position) {
		if (this.position != position) {
			this.position = position;
			autoDirty();
		}
		return this;
	}

	public Position getPosition() {
		return position;
	}

	public Widget setOverflow(Overflow overflow) {
		if (getOverflow() != overflow) {
			this.overflow = overflow;
			autoDirty();
		}
		return this;
	}

	public Overflow getOverflow() {
		return overflow;
	}

	@Override
	public Box getMargin() {
		return margin;
	}

	@Override
	public Box getPadding() {
		return padding;
	}

	@Override
	public BoxColor getBorder() {
		return border;
	}

	private final class WidgetBox implements Widget.Box {

		/** The widget we're related to. */
		private Widget parent;
		/** Margin is inside the drawing box, but outside the dimensions. */
		private int top = 0, right = 0, bottom = 0, left = 0;

		WidgetBox(Widget parent) {
			this.parent = parent;
		}

		@Override
		public Widget set(int all) {
			return set(all, all, all, all);
		}

		@Override
		public Widget set(int topBottom, int leftRight) {
			return set(topBottom, leftRight, topBottom, leftRight);
		}

		@Override
		public Widget set(int top, int leftRight, int bottom) {
			return set(top, leftRight, bottom, leftRight);
		}

		@Override
		public Widget set(int top, int right, int bottom, int left) {
			setTop(top);
			setRight(right);
			setBottom(bottom);
			setLeft(left);
			return parent;
		}

		@Override
		public Widget setTop(int top) {
			if (getTop() != top) {
				this.top = top;
				updateSize();
				autoDirty();
			}
			return parent;
		}

		@Override
		public Widget setRight(int right) {
			if (getRight() != right) {
				this.right = right;
				updateSize();
				autoDirty();
			}
			return parent;
		}

		@Override
		public Widget setBottom(int bottom) {
			if (getBottom() != bottom) {
				this.bottom = bottom;
				updateSize();
				autoDirty();
			}
			return parent;
		}

		@Override
		public Widget setLeft(int left) {
			if (getLeft() != left) {
				this.left = left;
				updateSize();
				autoDirty();
			}
			return parent;
		}

		@Override
		public int getTop() {
			return top;
		}

		@Override
		public int getRight() {
			return right;
		}

		@Override
		public int getBottom() {
			return bottom;
		}

		@Override
		public int getLeft() {
			return left;
		}
	}

	private final class WidgetBoxColor implements Widget.BoxColor {

		/** The widget we're related to. */
		private Widget parent;
		/** Margin is inside the drawing box, but outside the dimensions. */
		private int top = 0, right = 0, bottom = 0, left = 0;
		private int topColor = 0, rightColor = 0, bottomColor = 0, leftColor = 0;

		WidgetBoxColor(Widget parent) {
			this.parent = parent;
		}

		@Override
		public Widget set(int all, Color color) {
			return set(all, all, all, all, color);
		}

		@Override
		public Widget set(int topBottom, int leftRight, Color color) {
			return set(topBottom, leftRight, topBottom, leftRight, color);
		}

		@Override
		public Widget set(int top, int leftRight, int bottom, Color color) {
			return set(top, leftRight, bottom, leftRight, color);
		}

		@Override
		public Widget set(int top, int right, int bottom, int left, Color color) {
			setTop(top, color);
			setRight(top, color);
			setBottom(top, color);
			setLeft(top, color);
			return parent;
		}

		@Override
		public Widget setTop(int top, Color color) {
			if (getTop() != top || !getTopColor().equals(color)) {
				this.top = top;
				updateSize();
				autoDirty();
			}
			return parent;
		}

		@Override
		public Widget setRight(int right, Color color) {
			if (getRight() != right || !getRightColor().equals(color)) {
				this.right = right;
				updateSize();
				autoDirty();
			}
			return parent;
		}

		@Override
		public Widget setBottom(int bottom, Color color) {
			if (getBottom() != bottom || !getBottomColor().equals(color)) {
				this.bottom = bottom;
				updateSize();
				autoDirty();
			}
			return parent;
		}

		@Override
		public Widget setLeft(int left, Color color) {
			if (getLeft() != left || !getLeftColor().equals(color)) {
				this.left = left;
				updateSize();
				autoDirty();
			}
			return parent;
		}

		@Override
		public int getTop() {
			return top;
		}

		@Override
		public Color getTopColor() {
			return new Color(topColor);
		}

		@Override
		public int getRight() {
			return right;
		}

		@Override
		public Color getRightColor() {
			return new Color(rightColor);
		}

		@Override
		public int getBottom() {
			return bottom;
		}

		@Override
		public Color getBottomColor() {
			return new Color(bottomColor);
		}

		@Override
		public int getLeft() {
			return left;
		}

		@Override
		public Color getLeftColor() {
			return new Color(leftColor);
		}
	}

	@Override
	public Widget setMinWidth(int min) {
		min = Math.max(min, 0);
		if (getMinWidth() != min) {
			minWidth = min;
			updateSize();
			setWidth(width); // Enforce our new size if needed
		}
		return this;
	}

	@Override
	public int getMinWidth() {
		return minWidth;
	}

	@Override
	public Widget setMaxWidth(int max) {
		max = max <= 0 ? 427 : max;
		if (getMaxWidth() != max) {
			maxWidth = max;
			updateSize();
			setWidth(width); // Enforce our new size if needed
		}
		return this;
	}

	@Override
	public int getMaxWidth() {
		return maxWidth;
	}

	@Override
	public Widget setMinHeight(int min) {
		min = Math.max(min, 0);
		if (getMinHeight() != min) {
			minHeight = min;
			updateSize();
			setHeight(height); // Enforce our new size if needed
		}
		return this;
	}

	@Override
	public int getMinHeight() {
		return minHeight;
	}

	@Override
	public Widget setMaxHeight(int max) {
		max = max <= 0 ? 240 : max;
		if (getMaxHeight() != max) {
			maxHeight = max;
			updateSize();
			setHeight(height); // Enforce our new size if needed
		}
		return this;
	}

	@Override
	public int getMaxHeight() {
		return maxHeight;
	}

	@Override
	public Widget copy() {
		try {
			Widget copy = getType().getWidgetClass().newInstance();
			copy.setX(getX()) // Easier reading
					.setY(getY()) //
					.setWidth(getWidth()) //
					.setHeight(getHeight()) //
					.setVisible(isVisible()) //
					.setPriority(getPriority()) //
					.setTooltip(getTooltip()) //
					.setAnchor(getAnchor()) //
					.setMinWidth(getMinWidth()) //
					.setMaxWidth(getMaxWidth()) //
					.setMinHeight(getMinHeight()) //
					.setMaxHeight(getMaxHeight()) //
					.setFixed(isFixed()) //
					.setAutoDirty(isAutoDirty()) //
					.animate(animType, animValue, animCount, animTicks, (animFlags & ANIM_REPEAT) != 0, (animFlags & ANIM_RESET) != 0);
			Box m = getMargin();
			copy.getMargin().set(m.getTop(), m.getRight(), m.getBottom(), m.getLeft());
			return copy;
		} catch (Exception e) {
			throw new IllegalStateException("Unable to create a copy of " + getClass() + ". Does it have a valid widget type?");
		}
	}

	@Override
	public Widget updateSize() {
		if (hasParent()) {
			getParent().deferSize();
		}
		return this;
	}

	@Override
	public Widget setAutoDirty(boolean dirty) {
		return this;
	}

	@Override
	public boolean isAutoDirty() {
		return autoDirty;
	}

	@Override
	public void autoDirty() {
		if (isAutoDirty()) {
			setDirty(true);
		}
	}

	@Override
	public Widget animate(WidgetAnim type, float value, short count, short ticks) {
		animate(type, value, count, ticks, true, true);
		return this;
	}

	@Override
	public Widget animate(WidgetAnim type, float value, short count, short ticks, boolean repeat) {
		animate(type, value, count, ticks, repeat, true);
		return this;
	}

	@Override
	public Widget animate(WidgetAnim type, float value, short count, short ticks, boolean repeat, boolean reset) {
		if (!type.check(this)) {
			throw new TypeConstraintException("Cannot use Animation." + type.name() + " on " + getType().toString());
		}
		animType = type;
		animValue = value;
		animCount = count;
		animTicks = ticks;
		animFlags = (byte) ((repeat ? ANIM_REPEAT : 0) | (reset ? ANIM_RESET : 0));
		animTick = 0;
		animFrame = 0;
		autoDirty();
		return this;
	}

	@Override
	public Widget animateStart() {
		if (animType != WidgetAnim.NONE) {
			animFlags |= ANIM_RUNNING;
			autoDirty();
		}
		return this;
	}

	@Override
	public Widget animateStop(boolean finish) {
		if ((animFlags & ANIM_RUNNING) != 0 && finish) {
			animFlags |= ANIM_STOPPING;
			autoDirty();
		} else {
			animFlags &= ~ANIM_RUNNING;
			autoDirty();
		}
		return this;
	}

	@Override
	public void onAnimate() {
		if ((animFlags & ANIM_RUNNING) == 0 || animTicks == 0 || ++animTick % animTicks != 0) {
			return;
		}
		// We're running, and it's ready for our next frame...
		if (++animFrame == animCount) {
			animFrame = 0;
			if ((animFlags & ANIM_STOPPING) != 0 || (animFlags & ANIM_REPEAT) == 0) {
				animFlags &= ~ANIM_RUNNING;
			}
		}
	}

	@Override
	public void onAnimateStop() {
	}

	@Override
	public boolean hasPosition() {
		return hasPosition;
	}

	@Override
	public boolean hasSize() {
		return hasSize;
	}
}
