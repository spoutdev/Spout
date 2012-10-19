package org.spout.api.gui.component;

import org.spout.api.gui.Widget;
import org.spout.api.map.DefaultedKey;
import org.spout.api.signal.Signal;

public class RadioComponent extends ButtonComponent {
	public static final Signal SIGNAL_SELECTED = new Signal("selected", Boolean.class);
	
	private static final DefaultedKey<Boolean> KEY_SELECTED = new DefaultedKey<Boolean>() {
		@Override
		public Boolean getDefaultValue() {
			return false;
		}
		
		@Override
		public String getKeyString() {
			return "selected";
		}
	};
	
	public RadioComponent() {
		super();
		registerSignal(SIGNAL_SELECTED);
		try {
			subscribe(SIGNAL_CLICKED, this, "onClicked");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isSelected() {
		return getData().get(KEY_SELECTED);
	}
	
	public void setSelected(boolean selected) {
		getData().put(KEY_SELECTED, selected);
		emit(SIGNAL_SELECTED, selected);
		if (selected) {
			for (Widget widget:getOwner().getContainer().getWidgets()) {
				if (widget.hasExact(RadioComponent.class)) {
					RadioComponent other = widget.get(RadioComponent.class);
					other.setSelected(false);
				}
			}
		}
	}
	
	public void onClicked() {
		boolean selected = isSelected();
		if (!selected) {
			setSelected(true);
		}
	}
}
