package pubsearch.crawl;

import com.sun.glass.ui.Application;
import java.util.List;
import pubsearch.Config;
import pubsearch.StringTools;
import pubsearch.data.PDatabase;
import pubsearch.gui.tab.MainTab;

/**
 * Levezényli az egész crawling procedúrát.
 *
 * @author Zsolt
 */
public class Crawler extends ACrawler {

    //in
    private final MainTab caller;
    private String authorFilter;
    private String titleFilter;
    private boolean multithreaded;
    private int transLev;

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
        super.run();
        System.out.println("Crawler thread ended. time = " + StringTools.formatNanoTime(time, true, true) + ", bytes = " + HTTPRequest.getBytes() + " B (= " + StringTools.formatDataSize(HTTPRequest.getBytes()) + ")\n\n");
    }

    protected void crawl() {
        System.out.println("\nCrawler thread started. (au:" + authorFilter + "; ti:" + titleFilter + ")");

        HTTPRequest.zeroBytes();
        crawlers.clear();

        List<PDatabase> pdbs = PDatabase.getAll();
        for (PDatabase pdb : pdbs) {

            if (isInterrupted()) {
                break;
            }

            //XXX To test only one database:

            if (pdb.getName().equals("Google Scholar")) {
                continue;
            }

            String url = pdb.getBaseUrl() + pdb.getSubmitUrl();
            String qs = pdb.getSubmitParamsFormat().replaceFirst("%s", authorFilter);
            if (null != titleFilter) {
                qs = pdb.getSubmitParamsWithTitleFormat().replaceFirst("%s", qs).replaceFirst("%s", titleFilter);
            }

            ResultListCrawler rlc = new ResultListCrawler(pdb, url, qs, pdb.getSubmitMethod(), transLev);
            crawlers.add((ACrawler) rlc);
            rlc.launch(multithreaded);
            if (!multithreaded) {
                System.out.println(pdb.getName() + " DONE");
            }

        }

        if (multithreaded) {
            waitForCrawlers("Crawler thread interrupted.");
        }

        Config.saveProxyList();
        notifyCaller();
    }

    /**
     * Értesíti a hívó MainTab-ot, hogy kész a keresés, lekérdezheti az eredményeket.
     */
    private void notifyCaller() {
        Application.invokeLater(new Runnable() {

            public void run() {
                caller.showResults(HTTPRequest.getBytes());
            }
        });
    }
}
