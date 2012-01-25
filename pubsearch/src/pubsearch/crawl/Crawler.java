package pubsearch.crawl;

import com.sun.glass.ui.Application;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
        setDaemon(true); // ha a főprogram leáll, akkor ez is :-)
    }

    @Override
    public void run() {
        System.out.println("Crawler thread started.");

        List<PubPageCrawler> pubPageCrawlers = new ArrayList<PubPageCrawler>();

        try {
            List<PDatabase> pdbs = PDatabase.getAll();
            for (PDatabase pdb : pdbs) {
                if (!pdb.getName().equals("liinwww.ira.uka.de")) { //XXX just for testing
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

                    if (isInterrupted()) {
                        throw new InterruptedException();
                    }

                    newResultsCount = 0;
                    String setStartField = "&" + pdb.getStartField() + "=" + si;
                    System.out.println("    result list page #" + rlpi++ + " (" + url + "?" + qs + setStartField + ")");
                    HTTPRequestEx req = new HTTPRequestEx(url, qs + setStartField, pdb.getSubmitMethod());
                    if (req.submit(3)) {
                        String html = req.getHtml();
                        bytes += html.length();

                        // <debug html output>
                        try {
                            BufferedWriter w = new BufferedWriter(new FileWriter(pdb.getName() + rlpi + ".html"));
                            w.write(html);
                            w.close();
                        } catch (IOException e) {
                        }
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

                } while (newResultsCount == rpp && rlpi < 2); // XXX rlpi limit is just for testing

                System.out.println("    " + resultURLs.size() + " results");

                /*
                 * Start pub page crawler threads
                 */
                for (String resultURL : resultURLs) {

                    if (isInterrupted()) {
                        throw new InterruptedException();
                    }

                    System.out.println(" pubpage crawler starts (" + resultURL + ")");
                    PubPageCrawler pubPageCrawler = new PubPageCrawler(pdb, resultURL, transLev);
                    pubPageCrawlers.add(pubPageCrawler);
                    pubPageCrawler.start();
                }
            }

            /*
             * Wait for pubpage crawler threads to finish
             */
            boolean done = false;
            while (!done) {

                if (isInterrupted()) {
                    throw new InterruptedException();
                }

                done = true;
                for (int i = 0; i < pubPageCrawlers.size() && done; i++) {
                    done = !pubPageCrawlers.get(i).isAlive();
                }
            }
            System.out.println("CRAWLER: ALL DONE !");
            for (PubPageCrawler ppc : pubPageCrawlers) {
                bytes += ppc.getBytes();
            }

        } catch (InterruptedException ie) {
            System.err.println("Crawler thread interrupted.");

            for (PubPageCrawler ppc : pubPageCrawlers) {
                ppc.interrupt();
            }

            notifyCaller();
        } catch (Exception e) {
            System.err.println("Crawler thread caught exception: " + e.getMessage());
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
