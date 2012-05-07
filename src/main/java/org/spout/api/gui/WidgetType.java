package org.spout.api.gui;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.spout.api.GenericType;
import org.spout.api.gui.widget.GenericLabel;
import org.spout.api.gui.widget.ProgressBar;
import org.spout.api.gui.widget.PushButton;
import org.spout.api.gui.widget.CheckBox;
import org.spout.api.gui.widget.RadioButton;
import org.spout.api.gui.widget.Slider;
import org.spout.api.plugin.Plugin;

public class WidgetType extends GenericType<Widget> {

	public static final WidgetType SCREEN = new WidgetType(GenericScreen.class, -1);
	public static final WidgetType LABEL = new WidgetType(GenericLabel.class, 0);
	public static final WidgetType PUSHBUTTON = new WidgetType(PushButton.class, 1);
	public static final WidgetType CHECKBOX = new WidgetType(CheckBox.class, 2);
	public static final WidgetType RADIOBUTTON = new WidgetType(RadioButton.class, 3);
	public static final WidgetType SLIDER = new WidgetType(Slider.class, 4);
	public static final WidgetType PROGRESSBAR = new WidgetType(ProgressBar.class, 5);
	
	public WidgetType(Class<? extends Widget> clazz, int id) {
		super(clazz, id);
	}
	
	/**
	 * Creates a new instance of the WidgetType. 
	 * @param plugin the plugin to pass into the widgets constructor
	 * @return
	 */
	public Widget newInstance(Plugin plugin) {
		Class<? extends Widget> clazz = getClazz();
		Constructor<? extends Widget> constructor = null;
		try {
			constructor = clazz.getConstructor(Plugin.class);
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		}
		if(constructor != null) {
			try {
				return constructor.newInstance(plugin);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
