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
    private long bytes = 0;

    public Crawler(MainTab caller, String authorFilter, String titleFilter) {
        this.caller = caller;
        this.authorFilter = authorFilter;
        this.titleFilter = (titleFilter != null && titleFilter.trim().length() > 0) ? titleFilter : null;
    }

    @Override
    public void run() { // TODO megoldani valahogy, hogy a programból leállítható legyen gombbal / kilépéskor
        System.out.println("Crawler thread started.");

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

            HTTPRequest req = new HTTPRequest(url, qs, pDatabase.getSubmitMethod());

            String html;
            int rlpi = 0;
            do {
                // TODO GOND: MINDIG UGYANAZ A HTML LAP JÖN BE O.O
                // - pedig a nextURL-t jól szedi ki
                // - jól kapja meg a HTTPRequest
                // - de mégis az első találati oldalt szedi le újból
                // TODO GOND: ACM-nél nem jó a nextpage link... át kéne strukturálni a nextpage linkeket
                // - legyen "start" mező
                // - találatok per lap (ACM: 50, Spr:10)
                // - indulás 0/1 (ACM: 1, Spr: 0)
                // - és addig megy, amíg 0 publinket tud kiszedni?!
                //    - és ha bugos? - le kell ellenőrizni a túlcsordult start értékű HTML lap struktúráját mindegyiknél
                System.out.println("\t\tresult page #" + rlpi++);
                req.setProxy(Config.getRandomProxy());
                req.submit();
                html = req.getHtml(); // TODO retry on error!!!
                /*if (null == html) {
                    System.out.println("\t\t\tRetrying...");
                    req.setProxy(Config.getRandomProxy());
                    req.submit();
                    html = req.getHtml();
                    if (null == html) {
                        System.out.println("\t\t\tFailed to download.");
                    }
                }*/
                
                //System.out.println("--\n"+html+"\n--");
                if (null != html && html.length() > 0) {
                    bytes += html.length();
                    //System.out.println("\t\t\t" + bytes + " B");
                    ResultListPage rlp = new ResultListPage(pDatabase, html);
                    rlp.extractURLs();
                    resultURLs.addAll(rlp.getResultURLs());
                    url = rlp.getNextPageURL();
                    System.out.println("NEXT: " + url);
                    if (null != url) {
                        req = new HTTPRequest(url);
                    }
                }
            } while (null != html && null != url && rlpi < 2); // az rlpi limit csak az adatforgalmam kímélése miatt van

            System.out.println("\t|links| = " + resultURLs.size());
            /*
             * for (String u : resultURLs) {
             * System.out.println("\t\t\t" + u);
             * }
             */

            /*
             * Extract publication data (bibtex, authors, title, year)
             */
            /*
             * for (String resultURL : resultURLs) {
             * req = new HTTPRequest(resultURL);
             * req.setProxy(Config.getRandomProxy());
             * req.submit();
             * html = req.getHtml();
             * bytes += html.length();
             *
             * PubPage pubPage = new PubPage(pDatabase, html);
             * pubPage.extractData();
             * // valahol majd itt a refpubs
             * }
             */

            // PubPage, extract, visszakapunk egy Publication-t és a linket a refPubListPage-re
            // utóbbit egy RefPubListPage-el járjuk be,
            //  ha van link, akkor letöltjük a HTML-t, ha nincs, akkor a pubpage HTML megy oda is


            // vigyázzunk, hogy a link-pub, link-db, pub-pub kapcsolatoknál MINDKÉT irányba állítsuk be!!!!!!!

        }

        System.out.println(
                "Crawler thread stops.");
        notifyCaller();
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
