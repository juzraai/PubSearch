package pubsearch.crawl;

import java.util.List;
import pubsearch.data.PubDb;
import pubsearch.gui.MainWindow;

/**
 * Levezényli az egész crawling procedúrát.
 *
 * @author Zsolt
 */
public class Crawler extends Thread {

    private final MainWindow caller;
    private String authorFilter;
    private String titleFilter;
    private long bytes = 0;

    public Crawler(MainWindow caller, String authorFilter, String titleFilter) {
        this.caller = caller;
        this.authorFilter = authorFilter;
        this.titleFilter = (titleFilter != null && titleFilter.trim().length() > 0) ? titleFilter : null;
    }

    @Override
    public void run() {
        try {
            // TODO majd bent a szóközöket +-ra cseréli! vagy majd a Request-en belül... ?
            List<PubDb> pubdbs = PubDb.getAll();
            for (PubDb pubdb : pubdbs) {
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
            caller.showResults(bytes);
        }
    }

    /*public void addBytes(long b) {
        bytes += b;
    }*/
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
