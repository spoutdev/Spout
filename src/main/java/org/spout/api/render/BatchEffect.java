package org.spout.api.render;

public interface BatchEffect {

	/**
	 * Called right before batch
	 * @param snapshotRender 
	 */
	public abstract void preBatch(SnapshotRender snapshotRender);

	/**
	 * Called right after batch
	 * @param snapshotRender 
	 */
	public abstract void postBatch(SnapshotRender snapshotRender);

}
