package org.spout.api.gui;

import java.util.Iterator;
import java.util.LinkedList;

import org.spout.api.signal.SignalSubscriberObject;
import org.spout.api.tickable.Tickable;

public class ScreenStack extends SignalSubscriberObject implements Tickable {
	LinkedList<Screen> screens = new LinkedList<Screen>();
	LinkedList<Screen> visibleScreens = null;
	
	public ScreenStack(FullScreen root) {
		screens.add(root);
		dirty();
	}
	
	public void openScreen(Screen screen) {
		synchronized (screens) {
			screens.add(screen);
		}
		dirty();
	}
	
	public void closeTopScreen() {
		synchronized (screens) {
			screens.removeLast();
		}
		dirty();
	}
	
	public void closeScreen(Screen screen) {
		synchronized (screens) {
			if (screen == screens.getFirst()) {
				Screen second = screens.get(1);
				if (!(second instanceof FullScreen)) {
					throw new IllegalStateException("The lowest screen must be instance of FullScreen!");
				}
			}
			screens.remove(screen);
		}
		dirty();
	}
	
	/**
	 * Gets an ordered list of visible screens
	 * The first item in the list is the bottom-most fullscreen, the last item in the list is the top-most fullscreen/popupscreen.
	 * @return
	 */
	public LinkedList<Screen> getVisibleScreens() {
		synchronized (visibleScreens) {
			if (visibleScreens == null) {
				visibleScreens = new LinkedList<Screen>();
				
				synchronized (screens) {
					Iterator<Screen> iter = screens.descendingIterator();
					
					Screen next = null;
					
					while (iter.hasNext()) {
						next = iter.next();
						visibleScreens.addFirst(next);
						if (next instanceof FullScreen) {
							break;
						}
					}
				}
			}
			return visibleScreens;
		}
	}
	
	private void dirty() {
		synchronized (visibleScreens) {
			visibleScreens = null;
		}
	}
	
	@Override
	public void onTick(float dt) {
		for (Screen screen:getVisibleScreens()) {
			screen.tick(dt);
		}
	}
}
