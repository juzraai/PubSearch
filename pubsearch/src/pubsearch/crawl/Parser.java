package pubsearch.crawl;

import java.util.List;
import pubsearch.StringTools;
import pubsearch.data.PDatabase;

/**
 * Parszolási segédfüggvények, melyet több crawler is használ.
 *
 * @author Zsolt
 */
public class Parser {

    //in
    private String html;
    private PDatabase pdb;

    public Parser(String html, PDatabase pdb) {
        this.html = html;
        this.pdb = pdb;
    }

    /**
     * Kiszedi a HTML kódból a találatokra mutató URL-eket.
     * @param html A feldolgozandó HTML.
     * @return A kinyert linkek listája.
     */
    public List<String> extractPubPageURLs() {
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
    }
}
