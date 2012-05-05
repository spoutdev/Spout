package org.spout.api.gui.widget;

import org.spout.api.gui.Widget;
import org.spout.api.plugin.Plugin;

public class RadioButton extends AbstractButton {
	private int group = 0;

	public RadioButton(Plugin plugin) {
		this("", plugin);
	}

	public RadioButton(String text, Plugin plugin) {
		super(text, plugin);
		setCheckable(true);
	}
	
	/**
	 * Sets the radio button group of this radio button.
	 * Only one radio button in the same group can be checked.
	 * Radio buttons in other layouts won't be taken into consideration while calculating
	 * @param group the group of this radio button
	 * @return the instance
	 */
	public RadioButton setGroup(int group) {
		this.group = group;
		return this;
	}
	
	/**
	 * Gets the group
	 * @see setGroup(int)
	 * @return
	 */
	public int getGroup() {
		return group;
	}

	@Override
	public Button setChecked(boolean check) {
		if(check) {
			for(Widget widget:getParent().getWidgets()) {
				if(widget instanceof RadioButton) {
					RadioButton r = (RadioButton) widget;
					if(r.getGroup() == getGroup()) {
						r.setChecked(false);
					}
				}
			}
		}
		return super.setChecked(check);
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

}
