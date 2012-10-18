package org.spout.api.gui;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.spout.api.component.BaseComponentHolder;
import org.spout.api.component.Component;
import org.spout.api.component.components.WidgetComponent;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.map.DefaultedKey;
import org.spout.api.math.Rectangle;

public class Widget extends BaseComponentHolder {
	private List<RenderPart> renderPartCache;
	private boolean renderCacheClean = false;
	private Screen screen;
	
	private static DefaultedKey<Rectangle> GEOMETRY_KEY = new DefaultedKey<Rectangle>() {

		@Override
		public Rectangle getDefaultValue() {
			return new Rectangle(0, 0, 1, 1);
		}

		@Override
		public String getKeyString() {
			return "geometry";
		}
	};
	
	/**
	 * Returns a sorted list of render parts that consists of all render parts of the components
	 * @return a list of render parts
	 */
	public List<RenderPart> getRenderParts() {
		synchronized (renderPartCache) {
			if (!renderCacheClean) {
				renderPartCache = new LinkedList<RenderPart>();
				
				for (Component component:values()) {
					WidgetComponent wc = (WidgetComponent) component;
					renderPartCache.addAll(wc.getRenderParts());
				}
				
				Collections.sort(renderPartCache);
			}
			return renderPartCache;
		}
	}
	
	/**
	 * Invokes a render update in the next frame
	 */
	public void update() {
		synchronized (renderPartCache) {
			renderCacheClean = false;
		}
	}

	public void setScreen(Screen screen) {
		this.screen = screen;
	}
	
	public Screen getScreen() {
		return screen;
	}
	
	public boolean hasFocus() {
		return screen.getFocussedWidget() == this; // Exact instance
	}
	
	public void setFocus(FocusReason reason) {
		screen.setFocussedWidget(this);
		
		for (Component c:values()) {
			WidgetComponent wc = (WidgetComponent) c;
			wc.onFocus(reason);
		}
	}
	
	public void onFocusLost() {
		for (Component c:values()) {
			WidgetComponent wc = (WidgetComponent) c;
			wc.onFocusLost();
		}
	}
	
	public void setFocus() {
		setFocus(FocusReason.PROGRAMMED);
	}
	
	public Rectangle getGeometry() {
//		return getData().get(GEOMETRY_KEY);
		return null; //TODO need to figure out how to add non-serializeable stuff into the datamap
	}
	
	public void setGeometry(Rectangle geometry) {
//		getData().put(GEOMETRY_KEY, geometry);
	}
}
