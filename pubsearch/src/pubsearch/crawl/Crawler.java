package pubsearch.crawl;

import com.sun.glass.ui.Application;
import java.util.ArrayList;
import java.util.List;
import pubsearch.Config;
import pubsearch.data.PDatabase;
import pubsearch.gui.tab.MainTab;

/**
 * Levezényli az egész crawling procedúrát.
 *
 * @author Zsolt
 */
public class Crawler extends Thread {

    private final MainTab caller;
    private String authorFilter;
    private String titleFilter;
    private int transLev;
    private long bytes = 0;

    public Crawler(MainTab caller, String authorFilter, String titleFilter, int transLev) {
        this.caller = caller;
        this.authorFilter = authorFilter;
        this.titleFilter = (titleFilter != null && titleFilter.trim().length() > 0) ? titleFilter : null;
        this.transLev = transLev;
    }

    @Override
    public void run() { // TODO megoldani valahogy, hogy a programból leállítható legyen gombbal / kilépéskor
        System.out.println("Crawler thread started.");
        try {
            //Thread.sleep(5000);
            // TODO majd bent a szóközöket +-ra cseréli! vagy majd a Request-en belül... ?
            List<PDatabase> pDatabases = PDatabase.getAll();
            for (PDatabase pDatabase : pDatabases) {
                System.out.println("\t" + pDatabase.getName());

                List<String> resultURLs = new ArrayList<String>();

                /*
                 * Submit form, get result links
                 */
                String url = pDatabase.getBaseUrl() + pDatabase.getSubmitUrl();
                String qs = String.format(pDatabase.getSubmitParamsFormat(), authorFilter);
                if (null != titleFilter) {
                    qs = String.format(pDatabase.getSubmitParamsWithTitleFormat(), titleFilter);
                }
                int rpp = pDatabase.getResultsPerPage();
                int si = pDatabase.getFirstIndex();
                int newResultsCount = 0;
                int rlpi = 0;
                boolean success = true;
                do {
                    String setStartField = "&" + pDatabase.getStartField() + "=" + si;
                    System.out.println("\t\tresult page #" + rlpi++ + " URL: " + url + "?" + qs + setStartField);
                    HTTPRequestEx req = new HTTPRequestEx(url, qs + setStartField, pDatabase.getSubmitMethod());
                    if (req.submit(3)) {
                        String html = req.getHtml();
                        //System.out.println("--\n"+html+"\n--");
                        bytes += html.length();
                        ResultListPage rlp = new ResultListPage(pDatabase, html);
                        rlp.extractURLs();
                        List<String> newResults = rlp.getResultURLs();
                        newResultsCount = newResults.size();
                        System.out.println("\t\t\t+ " + newResultsCount);
                        resultURLs.addAll(newResults);
                        si += rpp;
                    }
                } while (newResultsCount > 0 && success && rlpi < 2); // rlpi limit is for testing!
                // lehetne newResultsCount==rpp is a feltétel a >0 helyett, viszont
                // a liinwww.ira.uka.de összevonja a linkeket, ezért ott kiszállna
                // az első oldalnál.

                System.out.println("\t\t|links| = " + resultURLs.size());
                for (String u : resultURLs) {
                    System.out.println("\t\t\t" + u);
                }
                System.out.println("\t\t|links| = " + resultURLs.size());

                /*
                 * Extract publication data (bibtex, authors, title, year)
                 */

                /*for (String resultURL : resultURLs) {
                    HTTPRequest req = new HTTPRequest(resultURL);
                    req.setProxy(Config.getRandomProxy());
                    req.submit();
                    html = req.getHtml();
                    bytes += html.length();

                    PubPage pubPage = new PubPage(pDatabase, html);
                    pubPage.extractData();
                    // a pubPage-ben kéne valahogy a refPubs részt
                    // és továbbadja rekurzívan magának a transLev-1-et.
                }*/


                // PubPage, extract, visszakapunk egy Publication-t és a linket a refPubListPage-re
                // utóbbit egy RefPubListPage-el járjuk be,
                //  ha van link, akkor letöltjük a HTML-t, ha nincs, akkor a pubpage HTML megy oda is


                // vigyázzunk, hogy a link-pub, link-db, pub-pub kapcsolatoknál MINDKÉT irányba állítsuk be!!!!!!!

            }
        } catch (Throwable t) {
            System.err.println("Crawler thread caught exception.");
            t.printStackTrace();
        } finally {
            System.out.println("Crawler thread stops.");
            notifyCaller();
        }
    }

    /**
     * Értesíti a hívó MainTab-ot, hogy kész a keresés, lekérdezheti az eredményeket.
     */
    private void notifyCaller() {
        Application.invokeLater(new Runnable() {

            public void run() {
                caller.showResults(bytes);
            }
        });
    }

    public void addBytes(int b) {
        bytes += b;
    }

    public long getBytes() {
        return bytes;
    }
}
