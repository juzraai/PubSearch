package pubsearch.crawl;

import com.sun.glass.ui.Application;
import java.util.List;
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
    private int transLev;

    public Crawler(MainTab caller, String authorFilter, String titleFilter, int transLev) {
        this.caller = caller;
        this.authorFilter = authorFilter;
        this.titleFilter = (titleFilter != null && titleFilter.trim().length() > 0) ? titleFilter : null;
        this.transLev = transLev;
        setName("Crawler");
    }

    @Override
    public void run() {
        super.run();
        System.out.println("Crawler thread ended. time = " + StringTools.formatNanoTime(time, true, true) + ", bytes = " + HTTPRequest.getBytes() + " B (= " + StringTools.formatDataSize(HTTPRequest.getBytes()) + ")\n~~~\n");
    }

    protected void crawl() {
        System.out.println("~~~\nCrawler thread started. (au:" + authorFilter + "; ti:" + titleFilter + ")");

        HTTPRequest.zeroBytes();
        crawlers.clear();

        /*
         * Start threads
         */
        List<PDatabase> pdbs = PDatabase.getAll();
        for (PDatabase pdb : pdbs) {
            if (!pdb.getName().equals("CiteSeerX")) { //XXX just for testing
                continue;
            }

            String url = pdb.getBaseUrl() + pdb.getSubmitUrl();
            String qs = pdb.getSubmitParamsFormat().replaceFirst("%s", authorFilter);
            if (null != titleFilter) {
                qs = pdb.getSubmitParamsWithTitleFormat().replaceFirst("%s", qs).replaceFirst("%s", titleFilter);
            }

            ResultListCrawler rlc = new ResultListCrawler(pdb, url, qs, pdb.getSubmitMethod(), transLev);
            crawlers.add((ACrawler) rlc);
            rlc.start();
            //TODO az egész RLC a submitMethod-dal megy... szóval ha a form POST-os, akkor a RL-nek is POST-osnak kell lennie...
            //lehet ezt át kéne alakítani, általánosabbá
        }

        /*
         * Wait for threads to finish
         */
        waitForCrawlers("Crawler thread interrupted.");

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
