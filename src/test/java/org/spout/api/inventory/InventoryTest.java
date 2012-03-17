package org.spout.api.inventory;

import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.spout.api.material.GenericItemMaterial;

public class InventoryTest {

	private int size = 10;
	private Inventory subject = new Inventory(size);
	private ItemStack testing = new ItemStack(new GenericItemMaterial("Testing", 1), 1, (short) 1), testing2 = new ItemStack(new GenericItemMaterial("Testing2", 1), 1, (short) 1);

	@Before
	public void setUp() {
		subject.addItem(testing);
	}

	@Test
	public void testContains() {
		assertTrue(subject.contains(testing));
	}
}
