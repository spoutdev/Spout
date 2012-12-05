package org.spout.api.model.animation;

public class AnimationFrame {

	private final int key;
	private BoneTransform []transforms;

	public AnimationFrame(int key){
		this.key = key;
	}

	public int getKey() {
		return key;
	}

}
