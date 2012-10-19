package org.spout.api.gui.component;

import org.spout.api.map.DefaultedKey;
import org.spout.api.signal.Signal;

public class CheckBoxComponent extends ButtonComponent {
	public static final Signal SIGNAL_CHECKED = new Signal("checked", Boolean.class);
	private static final DefaultedKey<Boolean> KEY_CHECKED = new DefaultedKey<Boolean>() {
		public Boolean getDefaultValue() {
			return false;
		};
		
		public String getKeyString() {
			return "checked";
		};
	};
	
	public CheckBoxComponent() {
		super();
		registerSignal(SIGNAL_CHECKED);
		try {
			subscribe(SIGNAL_CLICKED, this, "onClicked");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public void onClicked() {
		boolean checked = isChecked();
		checked = !checked;
		setChecked(checked);
		
		getOwner().update();
	}
	
	public boolean isChecked() {
		return getData().get(KEY_CHECKED);
	}
	
	public void setChecked(boolean checked) {
		getData().put(KEY_CHECKED, checked);
		emit(SIGNAL_CHECKED, checked);
	}
}
