package net.glowstone.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class DeadlockMonitor extends Thread {

    public void run() {

        while(!interrupted()) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Checking for deadlocks");
            ThreadMXBean tmx = ManagementFactory.getThreadMXBean();
            long[] ids = tmx.findDeadlockedThreads();
            if (ids != null) {
                ThreadInfo[] infos = tmx.getThreadInfo(ids, true, true);
                System.out.println("The following threads are deadlocked:");
                for (ThreadInfo ti : infos) {
                    System.out.println(ti);
                }
            }

        }

    }

}