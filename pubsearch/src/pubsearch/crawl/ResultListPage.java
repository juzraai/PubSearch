package pubsearch.crawl;

import java.util.List;
import pubsearch.StringTools;
import pubsearch.data.PubDb;

/**
 * Egy találati lista oldalt kezel le.
 *
 * @author Zsolt
 */
public class ResultListPage {
    // TODO megtervezni jobban! lehet, hogy Request-et kéne tárolni inkább?
    // vagy eredeti terv: a Request kívülről oldja meg a POST kérdést,
    // és akkor nem URL-t továbbít, hanem a POST submit HTML eredményét,
    // és lenne egy ilyen contstr.: RLP(PubDb, String URLorHTML, boolean isHTML),
    // az RLP(PubDb, String URL) pedig annyi lenne: this(pubdb, URL, false)

    private PubDb pubdb;
    private String html;
    /*
     * private Crawler crawler;
     * 
     * 
     *
     * public ResultListPage(Crawler crawler, PubDb pubdb) {
     * this.crawler = crawler;
     * this.pubdb = pubdb;
     * }
     *
     * public ResultListPage(Crawler crawler, PubDb pubdb, String url) {
     * this.crawler = crawler;
     * this.pubdb = pubdb;
     * Request r = new Request(url);
     * crawler.addBytes(r.getBytes());
     * html = r.getHtml();
     * }
     */

    /**
     * Kiszedi a HTML kódból a találatokra mutató linkeket, kiegészíti őket
     * a megfelelő módon (pub. adatb. függő), és a bázis URL mögé illeszti őket.
     * @return A találatokra mutató linkek listája.
     */
    public List<String> getResultURLs() {
        List<String> resultURLs = StringTools.findAllMatch(html, pubdb.getPubPageLinkPattern());
        String modFormat;
        if (null != (modFormat = pubdb.getPubPageLinkModFormat())) {
            for (int i = 0; i < resultURLs.size(); i++) {
                resultURLs.set(i, pubdb.getBaseUrl() + String.format(modFormat, resultURLs.get(i)));
            }
        }
        return resultURLs;
    }

    /**
     * Kiszedi a HTML kódból a következő találati lista oldalra mutató linket (lapozáshoz),
     * és a bázis URL mögé illeszti.
     * @return Az URL.
     */
    public String getNextResultListPageURL() {
        String nextURL = StringTools.findFirstMatch(html, pubdb.getNextPageLinkPattern());
        return (null == nextURL) ? null : pubdb.getBaseUrl() + nextURL;
    }
}
