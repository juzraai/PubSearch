package pubsearch.crawl;

import java.util.List;
import pubsearch.StringTools;

/**
 * Adatok kinyerése, megtisztítva - találati lista és publikáció adatainak elemzéséhez.
 *
 * @author Zsolt
 */
public class Extract {

    private String html;

    public Extract(String from) {
        this.html = from;
    }

    public String URL(String pattern, String baseUrl, String modFormat) {
        String u = StringTools.findFirstMatch(html, pattern, 1);
        if (null != u) {
            if (modFormat.matches(".*%s.*")) {
                u = modFormat.replace("%s", u);
            }
            if (!u.startsWith("http:")) {
                u = baseUrl + u;
            }

            u = u.replaceAll("&amp;", "&"); // liinwww.ira.uka.de bug fix
            u = u.replaceFirst(";jsessionid=.*?\\?", "?"); // citeseerx bug fix
        }
        return u;
    }

    /**
     * Kiszedi a HTML kódból a találatokra mutató URL-eket.
     * @param html A feldolgozandó HTML.
     * @return A kinyert linkek listája.
     */
    public List<String> URLs(String pattern, String baseUrl, String modFormat) {
        List<String> resultURLs = StringTools.findAllMatch(html, pattern, 1);
        if (null != resultURLs) {
            for (int i = 0; i < resultURLs.size(); i++) {
                String u = resultURLs.get(i);
                if (modFormat.matches(".*%s.*")) {
                    u = modFormat.replace("%s", resultURLs.get(i));
                }
                if (!u.startsWith("http:")) {
                    u = baseUrl + u;
                }

                u = u.replaceAll("&amp;", "&"); // liinwww.ira.uka.de bug fix
                u = u.replaceFirst(";jsessionid=.*?\\?", "?"); // citeseerx bug fix

                resultURLs.set(i, u);
            }
        }
        return resultURLs;
    }

    public String authors(String pattern) {
        String authors = StringTools.findFirstMatch(html, pattern, 1);
        if (null != authors) {
            authors = authors.replaceAll("\\\\('|\")\\{", "").replaceAll("\\{\\\\('|\")", "").replaceAll("\\}", "").replaceAll("\\\\", "");
            authors = authors.replaceAll("[0-9]{1,2}", "");
            authors = authors.replaceAll("\n", " ").replaceAll("\t", " ").replaceAll("\"", "\\\"");
            authors = authors.replaceAll("&hellip;,?", " ");
            authors = StringTools.clean(authors).trim();
            authors = authors.replaceAll(" ,", ",").replace(" and",",").trim();
        }
        return authors;
    }

    public String title(String pattern) {
        String title = StringTools.findFirstMatch(html, pattern, 1);
        if (null != title) {
            title = title.replaceAll("\n", " ").replaceAll("\t", " ").replaceAll("\"", "\\\"");
            title = StringTools.clean(title).trim();
        }
        return title;
    }

    public int year(String pattern) {
        int year = -1;
        try {
            year = Integer.parseInt(StringTools.findFirstMatch(html, pattern, 1));
        } catch (NumberFormatException nfe) {
        }
        return year;
    }
}
