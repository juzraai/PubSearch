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
     * This method should implement the crawler algorhytm.
     */
    protected abstract void crawl();

    /**
     * Waits for the child crawler threads (stored in 'crawlers' field) to stop.
     * When this thread gets an interrupt, it will send it to all child.
     * @param msgOnInterrupt Error message to be written on console when getting
     * an interrupt. Pass null to be quiet.
     */
    protected void waitForCrawlers(String msgOnInterrupt) {
        boolean done = false;
        while (!done && !isInterrupted()) {
            done = true;
            for (int i = 0; i < crawlers.size() && done; i++) {
                done = done && !crawlers.get(i).isAlive();
            }
        }
        if (isInterrupted()) {
            if (null != msgOnInterrupt) {
                System.err.println(msgOnInterrupt);
            }
            for (ACrawler c : crawlers) {
                c.interrupt();
            }

            done = false;
            while (!done) {
                done = true;
                for (int i = 0; i < crawlers.size() && done; i++) {
                    done = done && !crawlers.get(i).isAlive();
                }
            }
        }
    }
}
