package pubsearch.crawl;

import com.sun.glass.ui.Application;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
            List<PDatabase> pDatabases = PDatabase.getAll();
            for (PDatabase pdb : pDatabases) {
                if (!pdb.getName().equals("ACM")) { //TODO JUST FOR TEST ONLY ONE PDB
                    continue;
                }
                System.out.println("  " + pdb.getName());

                Set<String> resultURLs = new HashSet<String>();

                /*
                 * Submit form, get result links
                 */
                String url = pdb.getBaseUrl() + pdb.getSubmitUrl();
                String qs = String.format(pdb.getSubmitParamsFormat(), authorFilter);

                if (null != titleFilter) {
                    qs = String.format(pdb.getSubmitParamsWithTitleFormat(), qs, titleFilter);
                }
                byte rpp = pdb.getResultsPerPage();
                byte si = pdb.getFirstIndex();
                int newResultsCount;
                int rlpi = 0;
                do {
                    newResultsCount = 0;
                    // TODO a RLPnek kellene továbbadni a transLevet
                    String setStartField = "&" + pdb.getStartField() + "=" + si;
                    System.out.println("    result page #" + rlpi++ + " (" + url + "?" + qs + setStartField + ")");
                    HTTPRequestEx req = new HTTPRequestEx(url, qs + setStartField, pdb.getSubmitMethod());
                    if (req.submit(3)) {
                        String html = req.getHtml();
                        bytes += html.length();

                        // <debug html output>
                        /*
                         * try {
                         * BufferedWriter w = new BufferedWriter(new FileWriter(pdb.getName() + rlpi + ".html"));
                         * w.write(html);
                         * w.close();
                         * } catch (IOException e) {
                         * }
                         */
                        // </debug html output>

                        ResultListPage rlp = new ResultListPage(pdb, html);
                        rlp.extractURLs();
                        List<String> newResults = rlp.getResultURLs();
                        if (resultURLs.containsAll(newResults)) {
                            System.err.println("Repeating result list page.");
                            // újra megkaptunk egy korábbi találati lista oldalt, lelépünk erről az oldalról.
                            break;
                        }
                        newResultsCount = newResults.size();
                        System.out.println("      + " + newResultsCount);
                        resultURLs.addAll(newResults);
                        si += rpp;
                    }

                    // <liinwww.ira.uka.de fix>
                    if (pdb.getBaseUrl().equals("http://liinwww.ira.uka.de/") && 0 < newResultsCount) {
                        System.err.println("Forced page advance.");
                        newResultsCount = rpp;
                        /*
                         * Azért kell, mert a liinwww.ira.uka.de összevonja a linkeket, nem mindig pont rpp
                         * db találat van az oldalon, így a newResultCount==rpp feltétellel kiszállna az első
                         * oldalnál. A newResultCount>0 feltétel lenne neki jó, de így viszont minden más
                         * adatbázis esetén +1 oldal letöltődne, ami pazarlás.
                         */
                    }
                    // </liinwww.ira.uka.de fix>

                } while (newResultsCount == rpp && rlpi < 2); // TODO rlpi limit is for testing!

                System.out.println("    " + resultURLs.size() + " results");

                /*
                 * Extract publication data (bibtex, authors, title, year)
                 */
                for (String resultURL : resultURLs) {
                    //resultURL = resultURL.replaceAll(";jsessionid=.*?\\?", "?");
                    System.out.println("    crawling pubpage (" + resultURL + ")");
                    PubPageCrawler pubPageCrawler = new PubPageCrawler(pdb, resultURL, transLev);
                    pubPageCrawler.crawl();
                    bytes += pubPageCrawler.getBytes();
                }



                // PubPageCrawler, extract, visszakapunk egy Publication-t és a linket a refPubListPage-re
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
