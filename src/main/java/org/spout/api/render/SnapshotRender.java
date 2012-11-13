package org.spout.api.render;

import org.spout.api.geo.cuboid.ChunkSnapshotModel;
import org.spout.api.material.Material;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.Vector3;
import org.spout.api.model.Mesh;

public class SnapshotRender {

	private Mesh mesh;
	private Material material;
	private ChunkSnapshotModel snapshotModel;
	private Vector3 position;
	private BlockFace face;
	private boolean[] toRender;

	public SnapshotRender(Material material, ChunkSnapshotModel snapshotModel, Vector3 position, BlockFace face, boolean toRender[]){
		this.setMaterial(material);
		this.setSnapshotModel(snapshotModel);
		this.setPosition(position);
		this.setFace(face);
		this.setToRender(toRender);
		mesh = material.getModel().getMesh();
	}

	public Mesh getMesh() {
		return mesh;
	}

	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public ChunkSnapshotModel getSnapshotModel() {
		return snapshotModel;
	}

	public void setSnapshotModel(ChunkSnapshotModel snapshotModel) {
		this.snapshotModel = snapshotModel;
	}

	public Vector3 getPosition() {
		return position;
	}

	public void setPosition(Vector3 position) {
		this.position = position;
	}

	public BlockFace getFace() {
		return face;
	}

	public void setFace(BlockFace face) {
		this.face = face;
	}

	public boolean[] getToRender() {
		return toRender;
	}

	public void setToRender(boolean[] toRender) {
		this.toRender = toRender;
	}

}
