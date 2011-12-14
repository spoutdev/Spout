package org.getspout.commons.plugin;

public class PluginSecureThread extends Thread {
	
	public PluginSecureThread() {
		
	}

	public PluginSecureThread(Runnable target) {
		super(target);
	}

	public PluginSecureThread(Runnable target, String name) {
		super(target, name);
	}

	public PluginSecureThread(String name) {
		super(name);
	}

	public PluginSecureThread(ThreadGroup group, Runnable target) {
		super(group, target);
	}

	public PluginSecureThread(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
	}

	public PluginSecureThread(ThreadGroup group, Runnable target, String name, long stackSize) {
		super(group, target, name, stackSize);
	}

	public PluginSecureThread(ThreadGroup group, String name) {
		super(group, name);
	}



}
