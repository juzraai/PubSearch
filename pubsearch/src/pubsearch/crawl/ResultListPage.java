package pubsearch.crawl;

import java.util.List;

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

    /*
     * private Crawler crawler;
     * private PubDb pubdb;
     * private String html;
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
    public List<String> getResultURLs() {
        return null;
    }

    public String getNextResultListPageURL() {
        return null; // TODO null, ha nem talál
    }
}
