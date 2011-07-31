package org.getspout.io;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FileDownloadThread extends Thread{
	private static FileDownloadThread instance = null;
	private final ConcurrentLinkedQueue<Download> downloads = new ConcurrentLinkedQueue<Download>();
	private final ConcurrentLinkedQueue<Runnable> actions = new ConcurrentLinkedQueue<Runnable>();
	
	protected FileDownloadThread() {
		super("File Download Thread");
	}
	
	public static FileDownloadThread getInstance() {
		if (instance == null) {
			instance = new FileDownloadThread();
			instance.start();
		}
		return instance;
	}
	
	public void addToDownloadQueue(Download download){
		downloads.add(download);
	}
	
	public boolean isDownloading(String url) {
		Iterator<Download> i = downloads.iterator();
		while(i.hasNext()) {
			Download download = i.next();
			if (download.getDownloadUrl().equals(url)) {
				return true;
			}
		}
		return false;
	}
	
	public void onTick() {
		Iterator<Runnable> i = actions.iterator();
		while(i.hasNext()) {
			Runnable action = i.next();
			action.run();
			i.remove();
		}
	}
	
	public void run() {
		while(true) {
			Download next = downloads.poll();
			if (next != null) {
				try {
					if (!next.isDownloaded()) {
						System.out.println("Downloading File: " + next.getDownloadUrl());
						URL url = new URL(next.getDownloadUrl());
						ReadableByteChannel rbc = Channels.newChannel(url.openStream());
						FileOutputStream fos = new FileOutputStream(next.getTempFile());
						fos.getChannel().transferFrom(rbc, 0, 1 << 28);
						fos.close();
						next.move();
						try {
							sleep(10); //cool off after heavy network useage
						} catch (InterruptedException e) {}
					}
					if (next.getCompletedAction() != null) {
						actions.add(next.getCompletedAction());
					}
				}
				catch (Exception e) {
					System.out.println("-----------------------");
					System.out.println("Download Failed!");
					e.printStackTrace();
					System.out.println("-----------------------");
				}
			}
			else {
				try {
					sleep(100);
				} catch (InterruptedException e) {}
			}
		}
	}
}
