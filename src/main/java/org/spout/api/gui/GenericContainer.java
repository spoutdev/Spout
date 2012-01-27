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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.spout.api.ClientOnly;
import org.spout.api.plugin.Plugin;

public class GenericContainer extends AbstractBlock implements Container {

	/** Current version for serialisation and packet handling.*/
	private static final long serialVersionUID = 0L;
	private List<Widget> children = new ArrayList<Widget>();
	private ContainerType type = ContainerType.VERTICAL;
	private WidgetAnchor align = WidgetAnchor.TOP_LEFT;
	private boolean reverse = false;
	private int minWidthCalc = 0, maxWidthCalc = 427, minHeightCalc = 0, maxHeightCalc = 240;
	private boolean auto = true;
	private boolean recalculating = false;
	private boolean needsLayout = true;
	private boolean needsSize = true;

	public GenericContainer() {
	}

	public GenericContainer(int width, int height) {
		super(width, height);
	}

	public GenericContainer(int X, int Y, int width, int height) {
		super(X, Y, width, height);
	}

	public GenericContainer(Widget... children) {
		for (Widget child : children) {
			addChild(child);
		}
	}

	public GenericContainer(int width, int height, Widget... children) {
		super(width, height);
		for (Widget child : children) {
			addChild(child);
		}
	}

	public GenericContainer(int X, int Y, int width, int height, Widget... children) {
		super(X, Y, width, height);
		for (Widget child : children) {
			addChild(child);
		}
	}

	@Override
	public Container addChild(Widget child) {
		return insertChild(-1, child);
	}

	@Override
	public Container insertChild(int index, Widget child) {
		if (child != null) {
			if (index < 0 || index > this.children.size()) {
				this.children.add(child);
			} else {
				this.children.add(index, child);
			}
			child.setParent(this);
			deferSize();
			deferLayout();
		}
		return this;
	}

	@Override
	public Container addChildren(Widget... children) {
		for (Widget child : children) {
			this.insertChild(-1, child);
		}
		return this;
	}

	@Override
	public Container removeChild(Widget child) {
		if (children.contains(child)) {
			children.remove(child);
			child.setParent(null);
			// TODO
			//		if (!child.getType().isServerOnly()) {
			//			Spout.getPlayerFromId(playerId).sendPacket(new PacketWidgetRemove(widget, getId()));
			//		}
			updateSize();
			deferLayout();
		}
		return this;
	}

	@Override
	public Container removeChildren(Widget... children) {
		for (Widget child : children) {
			this.removeChild(child);
		}
		return this;
	}

	@Override
	public Container removeChildren(Plugin plugin) {
		for (Widget child : new ArrayList<Widget>(children)) {
			if (child.getPluginName().equals(plugin)) {
				removeChild(child);
			} else if (child instanceof Container) {
				((Container) child).removeChildren(plugin);
			}
		}
		return this;
	}

	@Override
	public Widget[] getChildren() {
		return getChildren(false);
	}

	@Override
	public Widget[] getChildren(boolean deep) {
		List<Widget> descendents;
		if (deep) {
			descendents = new ArrayList<Widget>(children);
			for (Widget child : children) {
				if (child instanceof Container) {
					for (Widget grandchild : ((Container) child).getChildren(true)) {
						descendents.add(grandchild);
					}
				}
			}
		} else {
			descendents = children;
		}
		Widget[] list = new Widget[descendents.size()];
		descendents.toArray(list);
		return list;
	}

	@Override
	public boolean containsChild(Widget widget) {
		return containsChild(widget.getUID());
	}

	@Override
	public boolean containsChild(int id) {
		return getChild(id) != null;
	}

	@Override
	public Widget getChild(int id) {
		return getChild(id, true);
	}

	@Override
	public Widget getChild(int id, boolean deep) {
		// Check direct children first for speed
		for (Widget child : children) {
			if (child.getUID() == id) {
				return child;
			}
		}
		// Then check down the tree if required
		if (deep) {
			for (Widget child : children) {
				if (child instanceof Container) {
					Widget widget = ((Container) child).getChild(id, true);
					if (widget != null) {
						return widget;
					}
				}
			}
		}
		return null;
	}

	@Override
	public WidgetType getType() {
		return WidgetType.CONTAINER;
	}

	@Override
	public Container setLayout(ContainerType type) {
		if (this.type != type) {
			this.type = type;
			deferLayout();
		}
		return this;
	}

	@Override
	public ContainerType getLayout() {
		return type;
	}

	@Override
	public Container setAlign(WidgetAnchor align) {
		if (this.align != align) {
			this.align = align;
			deferLayout();
		}
		return this;
	}

	@Override
	public WidgetAnchor getAlign() {
		return align;
	}

	@Override
	public Container setReverse(boolean reverse) {
		if (this.reverse != reverse) {
			this.reverse = reverse;
			deferLayout();
		}
		return this;
	}

	@Override
	public boolean getReverse() {
		return reverse;
	}

	@Override
	public Container deferLayout() {
		needsLayout = true;
		return this;
	}

	@Override
	public Container updateLayout() {
		if (!recalculating && super.getWidth() > 0 && super.getHeight() > 0 && !children.isEmpty()) {
			recalculating = true; // Prevent us from getting into a loop
			List<Widget> visibleChildren = new ArrayList<Widget>();
			int totalwidth = 0, totalheight = 0, newwidth, newheight, vcount = 0, hcount = 0;
			int availableWidth = auto ? getWidth() : getMinWidth(), availableHeight = auto ? getHeight() : getMinHeight();
			// We only layout visible children, invisible ones have zero physical presence on screen
			for (Widget widget : children) {
				if (widget.isVisible()) {
					visibleChildren.add(widget);
				}
			}
			// Reverse drawing order if we need to
			if (reverse) {
				Collections.reverse(visibleChildren);
			}
			// First - get the total space by fixed widgets and borders
			if (type == ContainerType.OVERLAY) {
				newwidth = availableWidth;
				newheight = availableHeight;
			} else {
				for (Widget widget : visibleChildren) {
					Box margin = widget.getMargin();
					int horiz = margin.getLeft() + margin.getRight();
					int vert = margin.getTop() + margin.getBottom();
					if (widget.isFixed()) {
						horiz += widget.getWidth();
						vert += widget.getHeight();
					}
					if (type == ContainerType.VERTICAL) {
						totalheight += vert;
						if (!widget.isFixed()) {
							vcount++;
						}
					} else if (type == ContainerType.HORIZONTAL) {
						totalwidth += horiz;
						if (!widget.isFixed()) {
							hcount++;
						}
					}
				}
				// Work out the width and height for children
				newwidth = (availableWidth - totalwidth) / Math.max(1, hcount);
				newheight = (availableHeight - totalheight) / Math.max(1, vcount);
				// Deal with minWidth and minHeight - change newwidth/newheight if needed
				for (Widget widget : visibleChildren) {
					if (!widget.isFixed()) {
						if (type == ContainerType.VERTICAL) {
							if (widget.getMinHeight() > newheight) {
								totalheight += widget.getMinHeight() - newheight;
								newheight = (availableHeight - totalheight) / Math.max(1, vcount);
							} else if (newheight >= widget.getMaxHeight()) {
								totalheight += widget.getMaxHeight();
								vcount--;
								newheight = (availableHeight - totalheight) / Math.max(1, vcount);
							}
						} else if (type == ContainerType.HORIZONTAL) {
							if (widget.getMinWidth() > newwidth) {
								totalwidth += widget.getMinWidth() - newwidth;
								newwidth = (availableWidth - totalwidth) / Math.max(1, hcount);
							} else if (newwidth >= widget.getMaxWidth()) {
								totalwidth += widget.getMaxWidth();
								hcount--;
								newwidth = (availableWidth - totalwidth) / Math.max(1, hcount);
							}
						}
					}
				}
				newheight = Math.max(newheight, 0);
				newwidth = Math.max(newwidth, 0);
			}
			totalheight = totalwidth = 0;
			// Resize any non-fixed widgets
			for (Widget widget : visibleChildren) {
				Box margin = widget.getMargin();
				int vMargin = margin.getTop() + margin.getBottom();
				int hMargin = margin.getLeft() + margin.getRight();
				if (!widget.isFixed()) {
					if (auto) {
						widget.setHeight(Math.max(widget.getMinHeight(), Math.min(newheight - (this.type == ContainerType.VERTICAL ? 0 : vMargin), widget.getMaxHeight())));
						widget.setWidth(Math.max(widget.getMinWidth(), Math.min(newwidth - (this.type == ContainerType.HORIZONTAL ? 0 : hMargin), widget.getMaxWidth())));
					} else {
						widget.setHeight(widget.getMinHeight() == 0 ? newheight - vMargin : widget.getMinHeight());
						widget.setWidth(widget.getMinWidth() == 0 ? newwidth - hMargin : widget.getMinWidth());
					}
				}
				if (type == ContainerType.VERTICAL) {
					totalheight += widget.getHeight() + vMargin;
				} else {
					totalheight = Math.max(totalheight, widget.getHeight() + vMargin);
				}
				if (type == ContainerType.HORIZONTAL) {
					totalwidth += widget.getWidth() + hMargin;
				} else {
					totalwidth = Math.max(totalwidth, widget.getWidth() + hMargin);
				}
			}
			// Work out the new top-left position taking into account Align
			int left = super.getX();
			int top = super.getY();
			if (align == WidgetAnchor.TOP_CENTER || align == WidgetAnchor.CENTER_CENTER || align == WidgetAnchor.BOTTOM_CENTER) {
				left += (super.getWidth() - totalwidth) / 2;
			} else if (align == WidgetAnchor.TOP_RIGHT || align == WidgetAnchor.CENTER_RIGHT || align == WidgetAnchor.BOTTOM_RIGHT) {
				left += super.getWidth() - totalwidth;
			}
			if (align == WidgetAnchor.CENTER_LEFT || align == WidgetAnchor.CENTER_CENTER || align == WidgetAnchor.CENTER_RIGHT) {
				top += (super.getHeight() - totalheight) / 2;
			} else if (align == WidgetAnchor.BOTTOM_LEFT || align == WidgetAnchor.BOTTOM_CENTER || align == WidgetAnchor.BOTTOM_RIGHT) {
				top += super.getHeight() - totalheight;
			}
			// Move all children into the correct position
			for (Widget widget : visibleChildren) {
				Box margin = widget.getMargin();
				int realtop = top + margin.getTop();
				int realleft = left + margin.getLeft();
				if (widget.getY() != realtop || widget.getX() != realleft) {
					widget.setY(realtop).setX(realleft);
				}
				if (type == ContainerType.VERTICAL) {
					top += widget.getHeight() + margin.getTop() + margin.getBottom();
				} else if (type == ContainerType.HORIZONTAL) {
					left += widget.getWidth() + margin.getLeft() + margin.getRight();
				}
			}
			recalculating = false;
		}
		needsLayout = false;
		return this;
	}

	@Override
	public void onTick() {
		if (needsSize) {
			updateSize();
		}
		if (needsLayout) {
			updateLayout();
		}
		for (Widget child : new ArrayList<Widget>(children)) {
			try {
				child.onTick();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (Widget child : children) {
			try {
				child.onAnimate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Container deferSize() {
		needsSize = true;
		return this;
	}

	@Override
	public Container updateSize() {
		if (!recalculating && !isFixed()) {
			recalculating = true; // Prevent us from getting into a loop due to both trickle down and push up
			int minwidth = 0, maxwidth = 0, minheight = 0, maxheight = 0, minhoriz, maxhoriz, minvert, maxvert;
			// Work out the minimum and maximum dimensions for the contents of this container
			for (Widget widget : children) {
				if (widget.isVisible()) {
					if (widget instanceof Container) { // Trickle down to children
						((Container) widget).updateSize();
					}
					Box margin = widget.getMargin();
					minhoriz = maxhoriz = margin.getLeft() + margin.getRight();
					minvert = maxvert = margin.getTop() + margin.getBottom();
					if (widget.isFixed()) {
						minhoriz += widget.getWidth();
						maxhoriz += widget.getWidth();
						minvert += widget.getHeight();
						maxvert += widget.getHeight();
					} else {
						minhoriz += widget.getMinWidth();
						maxhoriz += widget.getMaxWidth();
						minvert += widget.getMinHeight();
						maxvert += widget.getMaxHeight();
					}
					if (type == ContainerType.HORIZONTAL) {
						minwidth += minhoriz;
						maxwidth += maxhoriz;
					} else {
						minwidth = Math.max(minwidth, minhoriz);
						if (type == ContainerType.OVERLAY) {
							maxwidth = Math.max(maxwidth, maxhoriz);
						} else {
							maxwidth = Math.min(maxwidth, maxhoriz);
						}
					}
					if (type == ContainerType.VERTICAL) {
						minheight += minvert;
						maxheight += maxvert;
					} else {
						minheight = Math.max(minheight, minvert);
						if (type == ContainerType.OVERLAY) {
							maxheight = Math.max(maxheight, maxvert);
						} else {
							maxheight = Math.min(maxheight, maxvert);
						}
					}
				}
			}
			minwidth = Math.min(minwidth, 427);
			maxwidth = Math.min(maxwidth == 0 ? 427 : maxwidth, 427);
			minheight = Math.min(minheight, 240);
			maxheight = Math.min(maxheight == 0 ? 240 : maxheight, 240);
			// Check if the dimensions have changed
			if (minwidth != minWidthCalc || maxwidth != maxWidthCalc || minheight != minHeightCalc || maxheight != maxHeightCalc) {
				minWidthCalc = minwidth;
				maxWidthCalc = maxwidth;
				minHeightCalc = minheight;
				maxHeightCalc = maxheight;
				deferLayout();
				if (hasParent()) { // Push up to parents
					getParent().updateSize();
					getParent().deferLayout();
				}
			}
			recalculating = false;
		}
		needsSize = false;
		return this;
	}

	@Override
	public Container setAuto(boolean auto) {
		this.auto = auto;
		return this;
	}

	@Override
	public boolean isAuto() {
		return auto;
	}

	@Override
	public int getVersion() {
		return super.getVersion() + (int) serialVersionUID;
	}

	@Override
	@ClientOnly
	public void render() {
//		Spoutcraft.getClient().getRenderDelegate().render(this);
	}
}