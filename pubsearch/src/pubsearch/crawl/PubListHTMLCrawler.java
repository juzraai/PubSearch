package pubsearch.crawl;

import java.util.ArrayList;
import java.util.List;
import pubsearch.StringTools;
import pubsearch.data.PDatabase;
import pubsearch.data.Publication;

/**
 * Feldolgoz egy találati listát tartalmazó HTML kódot.
 *
 * @author Zsolt
 */
public class PubListHTMLCrawler extends ACrawler {

    //in
    private PDatabase pdb;
    private String html;
    private int transLev;
    private boolean refPubMode;
    //out
    private List<Publication> publications = new ArrayList<Publication>();

    public PubListHTMLCrawler(PDatabase pdb, String html, int transLev, boolean refPubMode) {
        this.pdb = pdb;
        this.html = html;
        this.transLev = transLev;
        this.refPubMode = refPubMode;
        setName("Crawler, ListHTML tr=" + transLev + ", pdb=" + pdb.getName() + "; " + getName());
        setPriority(6);
    }

    public List<Publication> getPublications() {
        return publications;
    }

    @Override
    protected void crawl() {
        /*
         * Get patterns
         */
        String listPattern = pdb.getResultListPattern();
        String listItemPattern = pdb.getResultListItemPattern();

        if (refPubMode) {
            if (null != pdb.getRefPubListPattern()) {
                listPattern = pdb.getRefPubListPattern();
            }
            if (null != pdb.getRefPubListItemPattern()) {
                listItemPattern = pdb.getRefPubListItemPattern();
            }
        }

        /*
         * Crop list, if needed
         */
        if (null != listPattern) {
            html = StringTools.findFirstMatch(html, listPattern, 1);
        }

        /*
         * Parse list, set up crawlers
         */
        if (null != html) {
            if (null != listItemPattern) { // ACM:ref, Google Scholar, MetaPress:ref, Springer:ref
                List<String> listItems = StringTools.findAllMatch(html, listItemPattern, 1);
                for (String listItem : listItems) {
                    String url = new Extract(listItem).URL(pdb.getPubPageLinkPattern(), pdb.getBaseUrl(), pdb.getPubPageLinkModFormat());
                    PubPageCrawler ppc;
                    if (!pdb.getPubPageLinkModFormat().equalsIgnoreCase("NOFOLLOW") && null != url) { // ACM:ref
                        ppc = new PubPageCrawler(pdb, url, transLev, refPubMode, true);
                    } else { // Google Scholar, MetaPress:ref, Springer:ref
                        ppc = new PubPageCrawler(pdb, listItem, transLev, refPubMode, false);
                    }
                    crawlers.add(ppc);
                }
            } else { // ACM:search, CiteSeerX, liinwww, MetaPress:search, Springer:search
                List<String> urls = new Extract(html).URLs(pdb.getPubPageLinkPattern(), pdb.getBaseUrl(), pdb.getPubPageLinkModFormat());
                for (String url : urls) {
                    PubPageCrawler ppc = new PubPageCrawler(pdb, url, transLev, refPubMode, true);
                    crawlers.add(ppc);
                }
            }

            /*
             * Start, wait for, and get results from crawlers
             */
            for (ACrawler crawler : crawlers) {
                crawler.launch(!refPubMode);
            }
            waitForCrawlers(null);
            for (ACrawler crawler : crawlers) {
                PubPageCrawler ppc = (PubPageCrawler) crawler;
                if (null != ppc.getPublication()) {
                    publications.add(ppc.getPublication());
                }
            }
        }
    }
}
