package pubsearch.crawl;

import java.util.List;
import pubsearch.StringTools;
import pubsearch.data.PDatabase;

/**
 * Egy találati lista oldalt kezel le.
 *
 * @author Zsolt
 */
public class ResultListPage {

    // in
    private PDatabase pDatabase;
    private String html;
    // out
    private List<String> resultURLs;
    private String nextPageURL;

    public ResultListPage(PDatabase pubdb, String html) {
        this.pDatabase = pubdb;
        this.html = html;
    }

    /**
     * Kiszedi a HTML kódból a találatokra és a következő találati lista oldalra
     * mutató URL-eket.
     */
    public void extractURLs() {
        resultURLs = StringTools.findAllMatch(html, pDatabase.getPubPageLinkPattern(), 1);
        String modFormat;
        if (null != (modFormat = pDatabase.getPubPageLinkModFormat())) {
            for (int i = 0; i < resultURLs.size(); i++) {
                resultURLs.set(i, pDatabase.getBaseUrl() + String.format(modFormat, resultURLs.get(i)));
            }
        }
        
        nextPageURL = StringTools.findFirstMatch(html, pDatabase.getNextPageLinkPattern(), 1);
        if (null != nextPageURL) {
            nextPageURL = pDatabase.getBaseUrl() + nextPageURL;
        }

    }

    /** 
     * @return A következő találati lista oldal URL-je.
     */
    public String getNextPageURL() {
        return nextPageURL;
    }

    /**
     * @return A talált publikációk adataihoz vezető URL-lek listája.
     */
    public List<String> getResultURLs() {
        return resultURLs;
    }
}
