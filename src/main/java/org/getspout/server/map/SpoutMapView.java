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
		this.x = world.getSpawnLocation().getBlockX();
		this.z = world.getSpawnLocation().getBlockZ();
		this.scale = Scale.FAR;
		addRenderer(new SpoutMapRenderer(this));
	}

	public short getId() {
		return id;
	}

	public boolean isVirtual() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Scale getScale() {
		return scale;
	}

	public void setScale(Scale scale) {
		if (scale == null) {
			throw new NullPointerException();
		}
		this.scale = scale;
	}

	public int getCenterX() {
		return x;
	}

	public int getCenterZ() {
		return z;
	}

	public void setCenterX(int x) {
		this.x = x;
	}

	public void setCenterZ(int z) {
		this.z = z;
	}

	public SpoutWorld getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = (SpoutWorld) world;
	}

	public List<MapRenderer> getRenderers() {
		return renderers;
	}

	public void addRenderer(MapRenderer renderer) {
		if (!renderers.contains(renderer)) {
			renderers.add(renderer);
			canvases.put(renderer, new HashMap<SpoutPlayer, SpoutMapCanvas>());
			renderer.initialize(this);
		}
	}

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
