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

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import org.spout.api.Engine;
import org.spout.api.component.block.BlockComponent;
import org.spout.api.component.entity.EntityComponent;
import org.spout.api.component.entity.NetworkComponent;
import org.spout.api.component.entity.SceneComponent;
import org.spout.api.component.widget.WidgetComponent;
import org.spout.api.component.world.WorldComponent;
import org.spout.api.data.ValueHolder;
import org.spout.api.entity.Entity;
import org.spout.api.entity.EntityPrefab;
import org.spout.api.entity.EntitySnapshot;
import org.spout.api.entity.Player;
import org.spout.api.entity.spawn.SpawnArrangement;
import org.spout.api.event.Cause;
import org.spout.api.event.player.PlayerInteractEvent;
import org.spout.api.event.player.input.PlayerClickEvent;
import org.spout.api.event.player.input.PlayerKeyEvent;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.generator.biome.Biome;
import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.geo.discrete.Transform2D;
import org.spout.api.gui.FocusReason;
import org.spout.api.gui.Screen;
import org.spout.api.gui.Widget;
import org.spout.api.gui.render.RenderPartPack;
import org.spout.api.lighting.LightingManager;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.DynamicUpdateEntry;
import org.spout.api.material.range.EffectRange;
import org.spout.api.math.IntVector2;
import org.spout.api.math.IntVector3;
import org.spout.api.math.Rectangle;
import org.spout.api.math.Vector3;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.util.cuboid.CuboidBlockMaterialBuffer;
import org.spout.api.util.cuboid.CuboidLightBuffer;

public final class ComponentTest {
	@Test
	public void test() throws Exception {
		//Mock owners
		BaseComponentOwner base = new BaseComponentOwner();
		TestEntity entity = new TestEntity();
		TestWidget widget = new TestWidget();
		TestWorld world = new TestWorld();
		BlockComponentOwner block = new BlockComponentOwner(0, 0, 0, world);
		//Add some components
		GenericSubComponent c1 = base.add(GenericSubComponent.class);
		GenericSubComponent2 c2 = base.add(GenericSubComponent2.class);
		OtherGenericComponent c3 = base.add(OtherGenericComponent.class);
		//Guarantee datatables are always there
		assertNotNull(base.getDatatable());
		assertNotNull(block.getDatatable());
		assertNotNull(entity.getDatatable());
		assertNotNull(widget.getDatatable());
		assertNotNull(world.getDatatable());
		//Test block
		GenericBlockComponent bc = block.add(GenericBlockComponent.class);
		assertNotNull(bc);
		//Test entity
		GenericEntityComponent ec = entity.add(GenericEntityComponent.class);
		assertNotNull(ec);
		//Test widget
		GenericWidgetComponent wic = widget.add(GenericWidgetComponent.class);
		assertNotNull(wic);
		//Test world
		GenericWorldComponent woc = world.add(GenericWorldComponent.class);
		assertNotNull(woc);

		assertNull(base.getExact(GenericComponent.class));
		assertNull(base.get(GenericEntityComponent.class));
		
		Collection<GenericComponent> generic = base.getAll(GenericComponent.class);
		assertTrue(generic.size() == 2);
		assertThat(generic, hasItems(c1, c2));
		
		Collection<Component> components = base.values();
		assertTrue(components.size() == 4);
		assertThat(components, hasItems(c1, c2, c3, base.getDatatable()));

		GenericComponentWithInterface cwi = base.add(GenericComponentWithInterface.class);
		Interface type = base.getType(Interface.class);
		assertNotNull(type);
		Collection<Interface> allOfType = base.getAllOfType(Interface.class);
		assertTrue(allOfType.size() == 1);
		assertEquals(cwi, type);
		
	}

	public static abstract class GenericComponent extends Component {
		public GenericComponent() {
		}
	}

	public static class GenericBlockComponent extends BlockComponent {
		public GenericBlockComponent() {
		}
	}

	public static class GenericEntityComponent extends EntityComponent {
		public GenericEntityComponent() {
		}
	}

	public static class GenericWidgetComponent extends WidgetComponent {
		public GenericWidgetComponent() {
		}
	}

	public static class GenericWorldComponent extends WorldComponent {
		public GenericWorldComponent() {
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

	public static class TestEntity extends BaseComponentOwner implements Entity {
		@Override
		public int getId() {
			return 0;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public UUID getUID() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Engine getEngine() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void remove() {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean isRemoved() {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean isSpawned() {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void setSavable(boolean savable) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean isSavable() {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void setViewDistance(int distance) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public int getViewDistance() {
			return 0;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void setObserver(boolean obs) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void setObserver(Iterator<IntVector3> custom) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean isObserver() {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Chunk getChunk() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Region getRegion() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void interact(PlayerInteractEvent.Action action, Entity source) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public SceneComponent getScene() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public NetworkComponent getNetwork() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public EntitySnapshot snapshot() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void onTick(float dt) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean canTick() {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void tick(float dt) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public World getWorld() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}
	}

	public static class TestWidget extends BaseComponentOwner implements Widget {
		@Override
		public void update() {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Screen getScreen() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void setScreen(Screen screen) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Transform2D getTransform() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Rectangle getBounds() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void setBounds(Rectangle bounds) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean canFocus() {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean isFocused() {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void onFocus(FocusReason reason) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void onBlur() {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void onClick(PlayerClickEvent event) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void onKey(PlayerKeyEvent event) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void onMouseMoved(IntVector2 prev, IntVector2 pos, boolean hovered) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public List<RenderPartPack> getRenderPartPacks() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void onTick(float dt) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean canTick() {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void tick(float dt) {
			//To change body of implemented methods use File | Settings | File Templates.
		}
	}

	public static class TestWorld extends BaseComponentOwner implements World {
		@Override
		public String getName() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public long getAge() {
			return 0;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public UUID getUID() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public int getSurfaceHeight(int x, int z, LoadOption loadopt) {
			return 0;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public int getSurfaceHeight(int x, int z) {
			return 0;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public BlockMaterial getTopmostBlock(int x, int z) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public BlockMaterial getTopmostBlock(int x, int z, LoadOption loadopt) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public BiomeManager getBiomeManager(int x, int z, LoadOption loadopt) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Entity getEntity(UUID uid) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Entity createEntity(Point point, Class<? extends Component>... classes) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Entity createEntity(Point point, EntityPrefab prefab) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void spawnEntity(Entity e) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Entity createAndSpawnEntity(Point point, LoadOption option, EntityPrefab prefab) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Entity createAndSpawnEntity(Point point, LoadOption option, Class<? extends Component>... classes) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Entity[] createAndSpawnEntity(Point[] points, LoadOption option, Class<? extends Component>... classes) {
			return new Entity[0];  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Entity[] createAndSpawnEntity(SpawnArrangement arrangement, LoadOption option, Class<? extends Component>... classes) {
			return new Entity[0];  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Transform getSpawnPoint() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void setSpawnPoint(Transform transform) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public long getSeed() {
			return 0;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public WorldGenerator getGenerator() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Engine getEngine() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public byte getSkyLight() {
			return 0;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void setSkyLight(byte newLight) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public List<Entity> getAll() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Entity getEntity(int id) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public List<Player> getPlayers() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public File getDirectory() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public TaskManager getParallelTaskManager() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public TaskManager getTaskManager() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public List<Entity> getNearbyEntities(Point position, Entity ignore, int range) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public List<Entity> getNearbyEntities(Point position, int range) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public List<Entity> getNearbyEntities(Entity entity, int range) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Entity getNearestEntity(Point position, Entity ignore, int range) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Entity getNearestEntity(Point position, int range) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Entity getNearestEntity(Entity entity, int range) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public List<Player> getNearbyPlayers(Point position, Player ignore, int range) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public List<Player> getNearbyPlayers(Point position, int range) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public List<Player> getNearbyPlayers(Entity entity, int range) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Player getNearestPlayer(Point position, Player ignore, int range) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Player getNearestPlayer(Point position, int range) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Player getNearestPlayer(Entity entity, int range) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean setBlockData(int x, int y, int z, short data, Cause<?> source) {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean addBlockData(int x, int y, int z, short data, Cause<?> source) {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, short data, Cause<?> source) {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean compareAndSetData(int x, int y, int z, int expect, short data, Cause<?> source) {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public short setBlockDataBits(int x, int y, int z, int bits, Cause<?> source) {
			return 0;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public short setBlockDataBits(int x, int y, int z, int bits, boolean set, Cause<?> source) {
			return 0;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public short clearBlockDataBits(int x, int y, int z, int bits, Cause<?> source) {
			return 0;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public int getBlockDataField(int x, int y, int z, int bits) {
			return 0;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean isBlockDataBitSet(int x, int y, int z, int bits) {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public int setBlockDataField(int x, int y, int z, int bits, int value, Cause<?> source) {
			return 0;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public int addBlockDataField(int x, int y, int z, int bits, int value, Cause<?> source) {
			return 0;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean containsBlock(int x, int y, int z) {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Block getBlock(int x, int y, int z) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Block getBlock(float x, float y, float z) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Block getBlock(Vector3 position) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean commitCuboid(CuboidBlockMaterialBuffer buffer, Cause<?> cause) {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void setCuboid(CuboidBlockMaterialBuffer buffer, Cause<?> cause) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void setCuboid(int x, int y, int z, CuboidBlockMaterialBuffer buffer, Cause<?> cause) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public CuboidLightBuffer getLightBuffer(short id) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public CuboidBlockMaterialBuffer getCuboid(boolean backBuffer) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public CuboidBlockMaterialBuffer getCuboid(int bx, int by, int bz, int sx, int sy, int sz) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public CuboidBlockMaterialBuffer getCuboid(int bx, int by, int bz, int sx, int sy, int sz, boolean backBuffer) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void getCuboid(int bx, int by, int bz, CuboidBlockMaterialBuffer buffer) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void getCuboid(CuboidBlockMaterialBuffer buffer) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void unload(boolean save) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean addLightingManager(LightingManager<?> manager) {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void save() {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Collection<Region> getRegions() {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Region getRegion(int x, int y, int z) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Region getRegion(int x, int y, int z, LoadOption loadopt) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Region getRegionFromChunk(int x, int y, int z) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Region getRegionFromChunk(int x, int y, int z, LoadOption loadopt) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Region getRegionFromBlock(int x, int y, int z) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Region getRegionFromBlock(int x, int y, int z, LoadOption loadopt) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Region getRegionFromBlock(Vector3 position) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Region getRegionFromBlock(Vector3 position, LoadOption loadopt) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Chunk getChunk(int x, int y, int z) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Chunk getChunk(int x, int y, int z, LoadOption loadopt) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean containsChunk(int x, int y, int z) {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Chunk getChunkFromBlock(int x, int y, int z) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Chunk getChunkFromBlock(int x, int y, int z, LoadOption loadopt) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Chunk getChunkFromBlock(Vector3 position) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Chunk getChunkFromBlock(Vector3 position, LoadOption loadopt) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean hasChunk(int x, int y, int z) {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean hasChunkAtBlock(int x, int y, int z) {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void saveChunk(int x, int y, int z) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void unloadChunk(int x, int y, int z, boolean save) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public int getNumLoadedChunks() {
			return 0;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void queueChunksForGeneration(List<Vector3> chunks) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void queueChunkForGeneration(Vector3 chunk) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public <T extends CuboidLightBuffer> T getLightBuffer(LightingManager<T> manager, int x, int y, int z, LoadOption loadopt) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public BlockMaterial getBlockMaterial(int x, int y, int z) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public int getBlockFullState(int x, int y, int z) {
			return 0;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public short getBlockData(int x, int y, int z) {
			return 0;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Biome getBiome(int x, int y, int z) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void resetDynamicBlock(int x, int y, int z) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void resetDynamicBlocks(Chunk c) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void syncResetDynamicBlock(int x, int y, int z) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, boolean exclusive) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate, boolean exclusive) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate, int data, boolean exclusive) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void queueBlockPhysics(int x, int y, int z, EffectRange range) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void updateBlockPhysics(int x, int y, int z) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public ValueHolder getData(String node) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public ValueHolder getData(World world, String node) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean hasData(String node) {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean hasData(World world, String node) {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}
	}
}
