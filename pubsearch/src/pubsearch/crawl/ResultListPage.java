package pubsearch.crawl;

import java.util.List;
import pubsearch.StringTools;
import pubsearch.data.PDatabase;

/**
 * Egy találati lista oldalt elemez.
 *
 * @author Zsolt
 */
public class ResultListPage {

    // in
    private PDatabase pDatabase;
    private String html;
    // out
    private List<String> resultURLs;

    public ResultListPage(PDatabase pubdb, String html) {
        this.pDatabase = pubdb;
        this.html = html;
    }

    /**
     * Kiszedi a HTML kódból a találatokra mutató URL-eket.
     */
    public void extractURLs() {
        resultURLs = StringTools.findAllMatch(html, pDatabase.getPubPageLinkPattern(), 1);
        String modFormat = pDatabase.getPubPageLinkModFormat();
        for (int i = 0; i < resultURLs.size(); i++) {
            String url = resultURLs.get(i);
            if (modFormat.matches(".*%s.*")) {
                url = String.format(modFormat, resultURLs.get(i));
            }
            if (!url.startsWith("http:")) {
                url = pDatabase.getBaseUrl() + url;
            }
            url = url.replaceAll("&amp;", "&"); // liinwww.ira.uka.de fix
            resultURLs.set(i, url);
        }

    }

    /**
     * @return A talált publikációk adataihoz vezető URL-lek listája.
     */
    public List<String> getResultURLs() {
        return resultURLs;
    }
}
