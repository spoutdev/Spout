package org.spout.api.gui.layout;

import java.awt.Dimension;
import java.awt.Rectangle;

import org.spout.api.gui.LayoutType;
import org.spout.api.gui.Widget;

public class VerticalLayout extends ManagedLayout {

	@Override
	public void relayout() {
		//Calculate minimum size
		
		//TODO add paddings when the attribute system is in
		int minimumHeight = getMargin().getTop();
		int minimumWidth = getMargin().getLeft();
		for(Widget w:getWidgets()) {
			if(w.getMinimumSize() != null) {
				minimumHeight += w.getMinimumSize().getHeight();
				minimumWidth = (int) Math.max(minimumWidth, w.getMinimumSize().getWidth());
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
		int remainingHeight = height;
		int heightPerWidget;
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
			heightPerWidget = remainingHeight / (numWidgets - handledWidgets);
			
			Rectangle geometry = new Rectangle();
			geometry.x = x;
			geometry.y = y;
			if(w.getMaximumSize().width < width) {
				geometry.width = w.getMaximumSize().width;
			} else {
				geometry.width = width;
			}
			if(w.getMaximumSize().height < heightPerWidget) {
				geometry.height = w.getMaximumSize().height;
			} else {
				geometry.height = heightPerWidget;
			}
			w.setGeometry(geometry);
			
			remainingHeight -= geometry.height;
			x += geometry.height;
			
			handledWidgets ++;
		}
	}

	@Override
	public LayoutType getLayoutType() {
		return LayoutType.VERTICALLAYOUT;
	}

}
