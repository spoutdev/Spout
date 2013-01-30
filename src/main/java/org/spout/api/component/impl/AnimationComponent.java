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

import org.spout.api.component.type.EntityComponent;
import org.spout.api.model.Model;
import org.spout.api.model.animation.Animation;
import org.spout.api.model.animation.AnimationPlayed;

public abstract class AnimationComponent extends EntityComponent {
	
	/**
	 * Send a animation to play once time
	 * @param animation
	 * @return
	 */
	public abstract AnimationPlayed playAnimation(Model model, Animation animation);
	
	/**
	 * Send a animation to play
	 * @param animation
	 * @param loop true to loop the animation
	 * @return
	 */
	public abstract AnimationPlayed playAnimation(Model model, Animation animation, boolean loop);

	/**
	 * Stop the animation
	 * @param animation (Require to send the AnimationPlayed returned when send to play)
	 */
	public abstract void stopAnimation(AnimationPlayed animation);
	
	/**
	 * Stop all animations
	 */
	public abstract void stopAnimations();
	
}