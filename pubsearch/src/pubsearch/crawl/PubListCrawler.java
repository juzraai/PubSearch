package pubsearch.crawl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import pubsearch.StringTools;
import pubsearch.data.PDatabase;
import pubsearch.data.Publication;

/**
 * Végiglapoz és feldolgoz egy találati listát.
 *
 * @author Zsolt
 */
public class PubListCrawler extends ACrawler {

    //in
    private PDatabase pdb;
    private String url;
    private int transLev;
    private boolean refPubMode;
    //inside
    Set<String> alreadyCrawled = new HashSet<String>();
    //out
    List<Publication> publications = new ArrayList<Publication>();

    public PubListCrawler(PDatabase pdb, String url, int transLev, boolean refPubMode) {
        this.pdb = pdb;
        this.url = url;
        this.transLev = transLev;
        this.refPubMode = refPubMode;
        setName("Crawler, ListURL tr=" + transLev + ", pdb=" + pdb.getName() + "; " + getName());
        setPriority(6);
    }

    public List<Publication> getPublications() {
        return publications;
    }

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
        // liinwww és CiteSeerX: newResultCount néha < ResultsPerPage
        // ezért használom inkább a >0 továbblapozási feltételt
        // ezzel globálisan áthidalom a problémát, nincs site specifikus égetett kód,
        // viszont így minden site esetén +1 oldal letöltést jelent... :S
    }
}
