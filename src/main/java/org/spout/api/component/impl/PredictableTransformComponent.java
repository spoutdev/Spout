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
package org.spout.api.component.impl;

import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;

public class PredictableTransformComponent extends TransformComponent {

	private Transform transformRender;

	private long lastTime;

	private Vector3 speed = Vector3.ONE;
	private Vector3 rotate = Vector3.ONE;
	private Vector3 scale = Vector3.ONE;

	@Override
	public void onAttached(){
		super.onAttached();
		transformRender = getTransform();
		lastTime = System.currentTimeMillis();
	}

	public void updateRender(float dt) {
		transformRender.translate(speed.multiply(dt));
		Quaternion q = transformRender.getRotation();
		transformRender.setRotation(MathHelper.rotation(q.getPitch() + rotate.getX() * dt, q.getYaw() + rotate.getY() * dt, q.getRoll() + rotate.getZ() * dt));
		transformRender.setScale(transformRender.getScale().add(scale.multiply(dt)));
	}

	public Transform getRenderTransform() {
		return transformRender;
	}


	@Override
	public void copySnapshot(){
		super.copySnapshot();

		Transform t = getTransform();

		//float delay = (lastTime - System.currentTimeMillis()) / 1000f;
		float ratio = 80f / 20f;

		speed = t.getPosition().subtract(transformRender.getPosition()).multiply(ratio);

		float rPitch = t.getRotation().getPitch() - transformRender.getRotation().getPitch();
		float rYaw = t.getRotation().getYaw() - transformRender.getRotation().getYaw();
		float rRoll = t.getRotation().getRoll() - transformRender.getRotation().getRoll();

		rPitch = (rPitch<-180f) ? rPitch+360f : rPitch;
		rYaw = (rYaw<-180f) ? rYaw+360f : rYaw;
		rRoll = (rRoll<-180f) ? rRoll+360f : rRoll;

		rPitch = (rPitch>180f) ? rPitch-360f : rPitch;
		rYaw = (rYaw>180f) ? rYaw-360f : rYaw;
		rRoll = (rRoll>180f) ? rRoll-360f : rRoll;

		rotate = new Vector3(rPitch,rYaw,rRoll).multiply(ratio);

		scale = t.getScale().subtract(transformRender.getScale()).multiply(ratio);

		//System.out.println(speed); //Debug
		lastTime = System.currentTimeMillis();
	}
}