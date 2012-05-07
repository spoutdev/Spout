package org.spout.api.gui.layout;

import java.awt.Dimension;
import java.awt.Rectangle;

import org.spout.api.gui.LayoutType;
import org.spout.api.gui.Widget;

public class HorizontalLayout extends ManagedLayout {

	@Override
	public void relayout() {
		//Calculate minimum size

		//TODO add paddings when the attribute system is in
		int minimumHeight = getMargin().getTop();
		int minimumWidth = getMargin().getLeft();
		for(Widget w:getWidgets()) {
			if(w.getMinimumSize() != null) {
				minimumHeight = Math.max(minimumHeight, w.getMinimumSize().height);
				minimumWidth = w.getMinimumSize().width;
			}
		}
		minimumHeight += getMargin().getBottom();
		minimumWidth += getMargin().getRight();
		Rectangle size = new Rectangle(new Dimension(minimumWidth, minimumHeight));
		if(!size.equals(getParent().getMinimumSize())) {
			getParent().setMinimumSize(size);
		}
		
		int numWidgets = getWidgets().length;
		int height = getParent().getGeometry().height - getMargin().getTop() - getMargin().getBottom();
		int width = getParent().getGeometry().width - getMargin().getLeft() - getMargin().getRight();
		int remainingWidth = width;
		int widthPerWidget;
		if(height < minimumHeight || width < minimumWidth) {
			//TODO handle exception
			System.out.println("Not enough space");
			return;
		}
		int x = getMargin().getLeft();
		int y = getMargin().getRight();
		int handledWidgets = 0;
		
		//Calculate the actual layout
		for(Widget w:getWidgets()) {
			widthPerWidget = remainingWidth / (numWidgets - handledWidgets);
			
			Rectangle geometry = new Rectangle();
			geometry.x = x;
			geometry.y = y;
			if(w.getMaximumSize().width < width) {
				geometry.width = w.getMaximumSize().width;
			} else {
				geometry.width = widthPerWidget;
			}
			if(w.getMaximumSize().height < height) {
				geometry.height = w.getMaximumSize().height;
			} else {
				geometry.height = height;
			}
			w.setGeometry(geometry);
			
			remainingWidth -= geometry.width;
			y += geometry.width;
			
			handledWidgets ++;
		}
	}

	@Override
	public LayoutType getLayoutType() {
		return LayoutType.HORIZONTALLAYOUT;
	}

}
