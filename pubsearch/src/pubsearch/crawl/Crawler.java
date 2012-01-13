package pubsearch.crawl;

import com.sun.glass.ui.Application;
import java.util.List;
import pubsearch.data.PubDb;
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
    public void run() {
        System.out.println("Crawler thread started.");
        try {
            //Thread.sleep(5000);
            // TODO majd bent a szóközöket +-ra cseréli! vagy majd a Request-en belül... ?
            List<PubDb> pubdbs = PubDb.getAll();
            for (PubDb pubdb : pubdbs) {
                System.out.println("\t" + pubdb.getName());

                /*
                 * List<String> resultURLs = new ArrayList<String>();
                 * ResultListPage resultListPage = new ResultListPage(PubDb, formURLwithGETparams);
                 * do {
                 * resultURLs.addAll(resultListPage.getResultURLs());
                 *
                 * } while (null != resultListPage.getNextResultListPageURL());
                 */
                /*
                 * 1. elküldi a formot -> resultlistpage
                 * 2. lekéri a linkeket
                 * 3. lapoz(getNextRLPURL) -> resultlistpage
                 * 4. -> (2.)
                 *
                 * 5. új ciklus: linklista bejárása, extract pub data
                 * (majd arra is egy külön osztályt, paramétere egy boolean extractCitesToo is!!!)
                 *
                 */

            }
        } catch (Throwable t) {
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

    public String getAuthorFilter() {
        return authorFilter;
    }

    public long getBytes() {
        return bytes;
    }

    public String getTitleFilter() {
        return titleFilter;
    }
}
