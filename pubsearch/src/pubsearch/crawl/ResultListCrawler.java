package pubsearch.crawl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import pubsearch.StringTools;
import pubsearch.data.PDatabase;
import pubsearch.data.Publication;

/**
 * Találati oldalak bejárását végzi el: kigyűjti a linkeket és levezényli a
 * publikációk adatlapjainak feldolgozását.
 *
 * @author Zsolt
 */
public class ResultListCrawler extends ACrawler {

    //in
    private PDatabase pdb;
    private String url;
    private String queryString;
    private String method;
    private int transLev;
    //out
    private List<Publication> publications = new ArrayList<Publication>();

    public ResultListCrawler(PDatabase pdb, String url, String queryString, String method, int transLev) {
        this.pdb = pdb;
        this.url = url;
        this.queryString = (null != queryString) ? queryString : "";
        this.method = method;
        this.transLev = transLev;
    }

    public List<Publication> getPublications() {
        return publications;
    }

    /**
     * Bejárja a találati lista oldalakat, kinyeri a linkeket, és PubPageCrawler
     * szálakat indít, melyek feldolgozzák a találatok adatait. Ezután megvárja
     * azok befejeződését, majd lekéri az eredményeket.
     */
    protected void crawl() {
        crawlers.clear();
        publications.clear();
        Set<String> alreadyCrawled = new HashSet<String>(); // találati listán belüli duplikátokra (pl. liinwww page1=page2)
        int startIndex = pdb.getFirstIndex();
        int resultsPerPage = pdb.getResultsPerPage();
        int resultPageNo = 0;
        int newResultCount;
        do {

            if (isInterrupted()) {
                break;
            }

            resultPageNo++;
            newResultCount = 0;


            /*
             * Download result list page
             */
            String startModifier = String.format("&%s=%d", pdb.getStartField(), startIndex);
            System.out.println(pdb.getName() + "\tResult page #" + resultPageNo + " " + url + "?" + queryString + startModifier);
            HTTPRequestEx req = new HTTPRequestEx(url, queryString + startModifier, method);
            if (req.submit()) {

                String html = req.getHtml();
                bytes += req.getBytes();

                if (isInterrupted()) {
                    break;
                }

                /*
                 * Start crawler threads
                 */
                List<String> current = new Parser(html, pdb).extractPubPageURLs();
                for (String u : current) {
                    if (!alreadyCrawled.contains(u)) {
                        alreadyCrawled.add(u);
                        newResultCount++;

                        PubPageCrawler ppc = new PubPageCrawler(pdb, u, transLev);
                        crawlers.add((ACrawler) ppc);
                        ppc.start();
                        //ppc.crawl();
                        //XXX (multi-single threaded switch)
                    }
                }

                System.out.println(pdb.getName() + "\t+" + newResultCount);

                // <liinwww.ira.uka.de fix>
                if (pdb.getBaseUrl().equals("http://liinwww.ira.uka.de/") && 0 < newResultCount) {
                    System.out.println(pdb.getName() + "\t" + "Force page advance.");
                    newResultCount = resultsPerPage;
                    /*
                     * Azért kell, mert a liinwww.ira.uka.de összevonja a linkeket, nem mindig pont rpp
                     * db találat van az oldalon, így a newResultCount==rpp feltétellel kiszállna az első
                     * oldalnál. A newResultCount>0 feltétel lenne neki jó, de így viszont minden más
                     * adatbázis esetén +1 oldal letöltődne, ami pazarlás.
                     */
                }
                // </liinwww.ira.uka.de fix>
            }

            startIndex += resultsPerPage;
        } while (newResultCount == resultsPerPage && resultPageNo <= 2); // XXX resulPageNo limit is just for testing!

        /*
         * Wait for threads to finish
         */
        waitForCrawlers(pdb.getName() + "\tInterrupted.");

        /*
         * Get results
         */
        for (ACrawler c : crawlers) {
            bytes += c.getBytes();
            PubPageCrawler ppc = (PubPageCrawler) c;
            if (null != ppc.getPublication()) {
                publications.add(ppc.getPublication());
            }
        }
        //System.out.println(StringTools.rpad(pdb.getName(), 20, ' ') + "Stops.");
    }

    /**
     * Kiszedi a HTML kódból a találatokra mutató URL-eket.
     * @param html A feldolgozandó HTML.
     * @return A kinyert linkek listája.
     */
    /*protected List<String> extractURLs(String html) {
        List<String> resultURLs = StringTools.findAllMatch(html, pdb.getPubPageLinkPattern(), 1);
        String modFormat = pdb.getPubPageLinkModFormat();
        for (int i = 0; i < resultURLs.size(); i++) {
            String u = resultURLs.get(i);
            if (modFormat.matches(".*%s.*")) {
                u = String.format(modFormat, resultURLs.get(i));
            }
            if (!u.startsWith("http:")) {
                u = pdb.getBaseUrl() + u;
            }

            u = u.replaceAll("&amp;", "&"); // liinwww.ira.uka.de bug fix
            u = u.replaceFirst(";jsessionid=.*?\\?", "?"); // citeseerx bug fix

            resultURLs.set(i, u);
        }
        return resultURLs;
    }*/
}
