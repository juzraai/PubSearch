package hu.juranyi.zsolt.pubsearch.crawl;

import com.sun.glass.ui.Application;
import hu.juranyi.zsolt.pubsearch.Config;
import hu.juranyi.zsolt.pubsearch.StringTools;
import hu.juranyi.zsolt.pubsearch.data.PDatabase;
import hu.juranyi.zsolt.pubsearch.gui.tab.MainTab;
import java.util.List;

/**
 * Controls the whole publication searching procedure. For every known publication
 * database: builds up the search URL and starts PubListCrawler to crawl them.
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
 */
public class Crawler extends ACrawler {

    private final MainTab caller;
    private String authorFilter;
    private String titleFilter;
    private boolean multithreaded;
    private int transLev;

    /**
     * Sets up the crawler.
     * @param caller The caller MainTab object, that Crawler will notify at the end.
     * @param authorFilter Search for this author.
     * @param titleFilter Filter for this title.
     * @param multithreaded If true, databases will be crawled multithreaded, otherwise iterated.
     * @param transLev 0: only search results, 1: referrer publications also 2: referrer of referrers also will be grabbed.
     */
    public Crawler(MainTab caller, String authorFilter, String titleFilter, boolean multithreaded, int transLev) {
        this.caller = caller;
        this.authorFilter = authorFilter;
        this.titleFilter = (titleFilter != null && titleFilter.trim().length() > 0) ? titleFilter : null;
        this.multithreaded = multithreaded;
        this.transLev = transLev;
        setName("Crawler");
    }

    @Override
    public void run() {
        System.out.println("\nCrawler thread started. (au:" + authorFilter + "; ti:" + titleFilter + ")");
        super.run();
        System.out.println("Crawler thread ended. time = " + StringTools.formatNanoTime(time, true, true) + ", bytes = " + HTTPRequest.getBytes() + " B (= " + StringTools.formatDataSize(HTTPRequest.getBytes()) + ")\n\n");
    }

    /**
     * Gets publication database list, sets up search URL and starts the crawl
     * for all of them. Waits for the child threads then notifies 'caller'.
     */
    protected void crawl() {
        HTTPRequest.zeroBytes();
        crawlers.clear();

        List<PDatabase> pdbs = PDatabase.getAll();
        for (PDatabase pdb : pdbs) {

            if (isInterrupted()) {
                break;
            }

            String url = pdb.getBaseUrl() + pdb.getSubmitUrl();
            String qs = pdb.getSubmitParamsFormat().replaceFirst("%s", authorFilter);
            if (null != titleFilter) {
                qs = pdb.getSubmitParamsWithTitleFormat().replaceFirst("%s", qs).replaceFirst("%s", titleFilter);
            }
            url += "?" + qs;
            PubListCrawler rlc = new PubListCrawler(pdb, url, transLev, false);
            crawlers.add(rlc);
            rlc.launch(multithreaded);
            if (!multithreaded) {
                System.out.println(pdb.getName() + " DONE");
            }
        }

        if (multithreaded) {
            waitForCrawlers();
        }

        Config.saveProxyList();
        notifyCaller();
    }

    private void notifyCaller() {
        Application.invokeLater(new Runnable() {

            public void run() {
                caller.showResults(HTTPRequest.getBytes());
            }
        });
    }
}
