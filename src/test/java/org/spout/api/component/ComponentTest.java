/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.component;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Collection;

import org.junit.Test;

import org.spout.api.component.BaseComponentHolder;
import org.spout.api.component.impl.DatatableComponent;
import org.spout.api.component.type.EntityComponent;

public final class ComponentTest {
	@Test
	public void test() throws Exception {
		BaseComponentHolder holder = new ComponentHolderTest();
		DatatableComponent data = holder.getData();
		GenericSubComponent c1 = holder.add(GenericSubComponent.class);
		GenericSubComponent2 c2 = holder.add(GenericSubComponent2.class);
		OtherGenericComponent c3 = holder.add(OtherGenericComponent.class);
		holder.add(GenericEntityComponent.class);
		
		assertNotNull(holder.get(GenericComponent.class));
		assertNull(holder.getExact(GenericComponent.class));
		assertNull(holder.get(GenericEntityComponent.class));
		
		Collection<GenericComponent> generic = holder.getAll(GenericComponent.class);
		assertTrue(generic.size() == 2);
		assertThat(generic, hasItems(c1, c2));
		
		Collection<Component> components = holder.values();
		assertTrue(components.size() == 4);
		assertThat(components, hasItems(c1, c2, c3, data));

		GenericComponentWithInterface cwi = holder.add(GenericComponentWithInterface.class);
		Interface type = holder.getType(Interface.class);
		assertNotNull(type);
		Collection<Interface> allOfType = holder.getAllOfType(Interface.class);
		assertTrue(allOfType.size() == 1);
		assertEquals(cwi, type);
		
	}
	
	public static class ComponentHolderTest extends BaseComponentHolder {
	}

	public static abstract class GenericComponent extends Component {
		public GenericComponent() {
		}
	}

	public static class GenericEntityComponent extends EntityComponent {
		public GenericEntityComponent() {
		}
	}

	public static class GenericSubComponent extends GenericComponent {
		public GenericSubComponent() {
		}
	}

	public static class GenericSubComponent2 extends GenericComponent {
		public GenericSubComponent2() {
		}
	}

	public static class OtherGenericComponent extends Component {
		public OtherGenericComponent() {
		}
	}
	
	public static interface Interface {	
	}

	public static class GenericComponentWithInterface extends GenericComponent implements Interface {
	}
}
