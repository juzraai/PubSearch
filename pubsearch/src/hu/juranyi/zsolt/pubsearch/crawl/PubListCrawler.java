package hu.juranyi.zsolt.pubsearch.crawl;

import hu.juranyi.zsolt.pubsearch.StringTools;
import hu.juranyi.zsolt.pubsearch.data.PDatabase;
import hu.juranyi.zsolt.pubsearch.data.Publication;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Crawls a result list. Downloads a page, starts a PubListHTMLCrawler for it,
 * stores results and turns the page if there was any new data.
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
 */
public class PubListCrawler extends ACrawler {

    //in
    private PDatabase pdb;
    private String url;
    private int transLev;
    private boolean refPubMode;
    //inside
    private Set<String> alreadyCrawled = new HashSet<String>();
    //out
    private List<Publication> publications = new ArrayList<Publication>();

    /**
     * Sets up the publication list crawling.
     * @param pdb PDatabase object which contains information for database specific crawling.
     * @param url Base URL of the result list. Should not contain a "start" field.
     * @param transLev 0: only search results, 1: referrer publications also 2: referrer of referrers also will be grabbed.
     * @param refPubMode If true, it handles the list as list of referring publications which may need different patterns to be used.
     */
    public PubListCrawler(PDatabase pdb, String url, int transLev, boolean refPubMode) {
        this.pdb = pdb;
        this.url = url;
        this.transLev = transLev;
        this.refPubMode = refPubMode;
        setName(getName() + " (" + pdb.getName() + ")");
    }

    public List<Publication> getPublications() {
        return publications;
    }

    /**
     * Builds up the URL now with the start field, downloads the page, starts a
     * PubListHTMLCrawler, stores grabbed publications, then repeats this if there
     * was any new data on the page.
     */
    @Override
    protected void crawl() {
        int startIndex = pdb.getFirstIndex();
        int resultPageNo = 0;
        int newResultCount;

        do {
            if (isInterrupted()) {
                break;
            }

            resultPageNo++;
            newResultCount = 0;

            String startModifier = String.format("&%s=%d", pdb.getStartField(), startIndex);
            HTTPRequestEx req = new HTTPRequestEx(url + startModifier);
            if (req.submit()) {
                String html = req.getHtml();

                if (null == StringTools.findFirstMatch(html, pdb.getNoResultsTextPattern(), 1)) {
                    PubListHTMLCrawler plpc = new PubListHTMLCrawler(pdb, html, transLev, refPubMode);
                    crawlers.add(plpc);
                    plpc.launch(false);

                    List<Publication> pubs = plpc.getPublications();
                    for (Publication p : pubs) {

                        String id;
                        if (null != p.getUrl()) {
                            id = p.getUrl();
                        } else {
                            id = p.getAuthors() + p.getTitle() + p.getYearAsString();
                        }

                        if (!alreadyCrawled.contains(id)) {
                            alreadyCrawled.add(id);
                            publications.add(p);
                            newResultCount++;
                        }
                    }
                }
            }

            startIndex += pdb.getResultsPerPage();
        } while (newResultCount > 0);
    }
}
