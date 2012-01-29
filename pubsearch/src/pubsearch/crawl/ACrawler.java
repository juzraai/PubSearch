package pubsearch.crawl;

import java.util.ArrayList;
import java.util.List;

/**
 * Crawler szálak közös tulajdonságai
 *
 * @author Zsolt
 */
public abstract class ACrawler extends Thread {

    //inside
    protected List<ACrawler> crawlers = new ArrayList<ACrawler>();
    //out
    protected long time;

    public ACrawler() {
        setDaemon(true); // ha a főprogram leáll, akkor ez is :-)
        setName(getClass().getSimpleName() + " " + getId());
    }

    @Override
    public void run() {
        time = System.nanoTime();
        crawl();
        time = System.nanoTime() - time;
    }

    protected abstract void crawl();

    /**
     * Várakozik a crawler szálak befejeződésére. Ha ezt a szálat megszakítják,
     * megszakítja a crawler szálakat is, és megvárja a befejeződésüket.
     */
    protected void waitForCrawlers(String msgOnInterrupt) {
        try {
            boolean done = false;
            while (!done) {
                if (isInterrupted()) {
                    throw new InterruptedException();
                }
                done = true;
                for (int i = 0; i < crawlers.size() && done; i++) {
                    done = done && !crawlers.get(i).isAlive();
                }
            }
        } catch (InterruptedException e) {
            if (null != msgOnInterrupt) {
                System.err.println(msgOnInterrupt);
            }

            for (ACrawler c : crawlers) {
                c.interrupt();
            }

            boolean done = false;
            while (!done) {
                done = true;
                for (int i = 0; i < crawlers.size() && done; i++) {
                    done = done && !crawlers.get(i).isAlive();
                }
            }
        }
    }
}
