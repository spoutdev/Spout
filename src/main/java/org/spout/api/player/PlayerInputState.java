package org.spout.api.player;

import org.spout.api.math.MathHelper;

/**
 *  Represents the current player input state
 *
 */
public class PlayerInputState {
	
	static final int NUM_AXIS = 6;
	
	static final float MAX_AXIS = 1.f;
	static final float MIN_AXIS = -1.f;
	
	
	float[] axis = new float[NUM_AXIS];
	/**
	 * Gets the current depression of the forward buttons.
	 * 1.0 reprensents full force forward
	 * -1.0 represents full force backward
	 * @return
	 */
	public float getForward(){
		return axis[0];
	}
	
	public float getHorizantal(){
		return axis[1];
	}
	
	public float getJump(){
		return axis[2];
	}
	
	public float getLookX(){
		return axis[3];
	}
	
	public float getLookY(){
		return axis[4];
	}
	
	public float getSprint(){
		return axis[5];
	}
	
	public void setForward(float value){
		axis[0] = (float)MathHelper.clamp(value, MAX_AXIS, MIN_AXIS);
	}
	
	public void setHorizantal(float value){
		axis[1] = (float)MathHelper.clamp(value, MAX_AXIS, MIN_AXIS);
	}
	
	public void setJump(float value){
		axis[2] = (float)MathHelper.clamp(value, MAX_AXIS, MIN_AXIS);
	}
	
	public void setLookX(float value){
		axis[3] = (float)MathHelper.clamp(value, MAX_AXIS, MIN_AXIS);
	}
	
	public void setLookY(float value){
		axis[4] = (float)MathHelper.clamp(value, MAX_AXIS, MIN_AXIS);
	}
	
	public void setSprint(float value){
		axis[5] = (float)MathHelper.clamp(value, MAX_AXIS, MIN_AXIS);
	}

}
