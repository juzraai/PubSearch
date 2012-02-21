package pubsearch.crawl;

import java.util.ArrayList;
import java.util.List;

/**
 * The main functionalities of a crawler thread.
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
 */
public abstract class ACrawler extends Thread {

    /**
     * Child crawler objects stored here.
     */
    protected List<ACrawler> crawlers = new ArrayList<ACrawler>();
    /**
     * Crawl time in nanoseconds. Measured in run().
     */
    protected long time;

    public ACrawler() {
        setDaemon(true); // if app is shot down, this thread stops too
        setName(getClass().getSimpleName() + " " + getId());
    }

    /**
     * This method should implement the crawler algorhytm.
     */
    protected abstract void crawl();

    public long getTime() {
        return time;
    }

    /**
     * Interrupts all child threads too.
     */
    @Override
    public void interrupt() {
        super.interrupt();
        for (ACrawler c : crawlers) {
            c.interrupt();
        }
    }

    /**
     * Starts the crawler algorhytm.
     * @param asNewThread if true, it calls start(), otherwise it calls run().
     */
    public void launch(boolean asNewThread) {
        if (asNewThread) {
            start();
        } else {
            run();
        }
    }

    /**
     * Calls crawl(), and measures running time in 'time' field.
     */
    @Override
    public void run() {
        time = System.nanoTime();
        crawl();
        time = System.nanoTime() - time;
    }

    /**
     * Waits for the child crawler threads (stored in 'crawlers' field) to stop.
     */
    protected void waitForCrawlers() {
        boolean done = false;
        while (!done) {
            done = true;
            for (int i = 0; i < crawlers.size() && done; i++) {
                done = done && !crawlers.get(i).isAlive();
            }
        }
    }
}
