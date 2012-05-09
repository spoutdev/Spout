package org.spout.api.entity.type;

import org.spout.api.entity.Controller;

/**
 * @author zml2008
 */
public class UncreatableControllerType extends ControllerType {
	public UncreatableControllerType(Class<? extends Controller> controllerClass, String name) {
		super(controllerClass, name);
	}

	@Override
	public boolean canCreateController() {
		return false;
	}

	@Override
	public Controller createController() {
		return null;
	}
}
