package hu.juranyi.zsolt.pubsearch.crawl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ThreadScheduler can help you limit the count of actually running threads.
 * Just create an instance, add your threads with add() and call start().
 * ThreadScheduler will start your threads in order, and guarantees that the
 * count of running threads at the same time will never be above the limit.
 * ThreadScheduler thread will stop when all of your threads end, so you can
 * wait for it with a simple while (threadScheduler.isAlive()); cycle or you
 * can use startAndWait() method which does the same. When ThreadScheduler
 * gets an interrupt, sends it to all running threads, waits for them to die,
 * then clears the list and stops.
 *
 * @version 1.0
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
 */
public class ThreadScheduler extends Thread {

    private static int schedulerID = 0;
    private static final int DEFAULT_LIMIT = 3;
    private int limit;
    private List<Thread> threads = new ArrayList<Thread>();

    public ThreadScheduler() {
        this(DEFAULT_LIMIT);
    }

    public ThreadScheduler(int limit) {
        this("ThreadScheduler-" + schedulerID, limit);
    }

    public ThreadScheduler(String name) {
        this(name, DEFAULT_LIMIT);
    }

    public ThreadScheduler(String name, int limit) {
        setName(name);
        this.limit = limit;
        schedulerID++;
    }

    public synchronized void add(Thread t) {
        threads.add(t);
        t.setName(this.getName() + " :: " + t.getName());
    }

    public synchronized int getAllThreadCount() {
        return threads.size();
    }

    public synchronized int getRunningThreadCount() {
        return Math.min(limit, threads.size());
    }

    public synchronized int getWaitingThreadCount() {
        return Math.max(0, threads.size() - limit);
    }

    @Override
    public synchronized void interrupt() {
        super.interrupt();
        for (int i = 0; i < limit && i < threads.size(); i++) {
            threads.get(i).interrupt();
        }
    }

    private synchronized boolean manage() {
        for (int i = 0; i < limit && i < threads.size(); i++) {
            if (!threads.get(i).isAlive()) {
                threads.remove(i);
                if (threads.size() >= limit && !isInterrupted()) {
                    threads.get(limit - 1).start();
                }
                break;
            }
        }
        return !threads.isEmpty();
    }

    @Override
    public void run() {
        for (int i = 0; i < limit && i < threads.size(); i++) {
            threads.get(i).start();
        }
        while (manage());
    }

    public void startAndWait() {
        start();
        try {
            this.join();
        } catch (InterruptedException ex) {
        }
    }
}
