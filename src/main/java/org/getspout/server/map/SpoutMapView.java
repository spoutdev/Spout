package org.getspout.server.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.SpoutPlayer;

/**
 * Represents a map item.
 */
public final class SpoutMapView implements MapView {
	//private final Map<SpoutPlayer, RenderData> renderCache = new HashMap<SpoutPlayer, RenderData>();
	private final List<MapRenderer> renderers = new ArrayList<MapRenderer>();
	private final Map<MapRenderer, Map<SpoutPlayer, SpoutMapCanvas>> canvases = new HashMap<MapRenderer, Map<SpoutPlayer, SpoutMapCanvas>>();
	private final short id;
	private Scale scale;
	private int x, z;
	private SpoutWorld world;

	protected SpoutMapView(SpoutWorld world, short id) {
		this.world = world;
		this.id = id;
		x = world.getSpawnLocation().getBlockX();
		z = world.getSpawnLocation().getBlockZ();
		scale = Scale.FAR;
		addRenderer(new SpoutMapRenderer(this));
	}

	@Override
	public short getId() {
		return id;
	}

	@Override
	public boolean isVirtual() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Scale getScale() {
		return scale;
	}

	@Override
	public void setScale(Scale scale) {
		if (scale == null) {
			throw new NullPointerException();
		}
		this.scale = scale;
	}

	@Override
	public int getCenterX() {
		return x;
	}

	@Override
	public int getCenterZ() {
		return z;
	}

	@Override
	public void setCenterX(int x) {
		this.x = x;
	}

	@Override
	public void setCenterZ(int z) {
		this.z = z;
	}

	@Override
	public SpoutWorld getWorld() {
		return world;
	}

	@Override
	public void setWorld(World world) {
		this.world = (SpoutWorld) world;
	}

	@Override
	public List<MapRenderer> getRenderers() {
		return renderers;
	}

	@Override
	public void addRenderer(MapRenderer renderer) {
		if (!renderers.contains(renderer)) {
			renderers.add(renderer);
			canvases.put(renderer, new HashMap<SpoutPlayer, SpoutMapCanvas>());
			renderer.initialize(this);
		}
	}

	@Override
	public boolean removeRenderer(MapRenderer renderer) {
		if (renderers.contains(renderer)) {
			renderers.remove(renderer);
			canvases.remove(renderer);
			return true;
		} else {
			return false;
		}
	}
}
