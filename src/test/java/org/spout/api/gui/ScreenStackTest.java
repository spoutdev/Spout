package org.spout.api.gui;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ScreenStackTest {
	private ScreenStack stack;
	private Screen testScreen;
	
	@Before
	public void setup() {
		FullScreen root = new FullScreen();
		stack = new ScreenStack(root);
		testScreen = new Screen();
	}
	
	@Test
	public void testOpenAndClose() {
		stack.openScreen(testScreen);
		assertSame(testScreen, stack.getVisibleScreens().getLast());
		stack.closeScreen(testScreen);
		assertNotSame(testScreen, stack.getVisibleScreens().getLast());
	}
}
