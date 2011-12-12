package org.getspout.commons.addon;

public class AddonSecureThread extends Thread {
	
	public AddonSecureThread() {
		
	}

	public AddonSecureThread(Runnable target) {
		super(target);
	}

	public AddonSecureThread(Runnable target, String name) {
		super(target, name);
	}

	public AddonSecureThread(String name) {
		super(name);
	}

	public AddonSecureThread(ThreadGroup group, Runnable target) {
		super(group, target);
	}

	public AddonSecureThread(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
	}

	public AddonSecureThread(ThreadGroup group, Runnable target, String name, long stackSize) {
		super(group, target, name, stackSize);
	}

	public AddonSecureThread(ThreadGroup group, String name) {
		super(group, name);
	}



}
