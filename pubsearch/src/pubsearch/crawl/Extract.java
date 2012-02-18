package pubsearch.crawl;

import java.util.List;
import pubsearch.StringTools;

/**
 * Specific tool for extracting basic data of a publication from text block.
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
 */
public class Extract {

    private String html;

    /**
     * @param from The input text, can be HTML code.
     */
    public Extract(String from) {
        this.html = from;
    }

    /**
     * Extracts one URL.
     * @param pattern Pattern for the URL.
     * @param baseUrl Base URL - used when matching URL is relative.
     * @param modFormat Modification, for example an additional suffix.
     * @return The extracted and modified URL.  If there's no match, returns null.
     */
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
     * Extracts all URLs.
     * @param pattern Pattern for one URL.
     * @param baseUrl Base URL - used when matching URL is relative.
     * @param modFormat Modification, for example an additional suffix.
     * @return The extracted and modified URLs.
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

    /**
     * Extracts the authors list and cleans it.
     * @param pattern Pattern for authors list.
     * @return Extracted and cleaned authors list. If there's no match, returns null.
     */
    public String authors(String pattern) {
        String authors = StringTools.findFirstMatch(html, pattern, 1);
        if (null != authors) {
            authors = authors.replaceAll("\\\\('|\")\\{", "").replaceAll("\\{\\\\('|\")", "").replaceAll("\\{", "").replaceAll("\\}", "").replaceAll("\\\\", "");
            authors = authors.replaceAll("[0-9]{1,2}", "");
            authors = authors.replaceAll("\n", " ").replaceAll("\t", " ").replaceAll("\"", "\\\"");
            authors = authors.replaceAll("&hellip;,?", " ");
            authors = StringTools.clean(authors).trim();
            authors = authors.replaceAll(" ,", ",").replace(" and", ",").trim();
        }
        return authors;
    }

    /**
     * Extracts the title and cleans it.
     * @param pattern Pattern for title
     * @return Extracted and cleaned title. If there's no match, returns null.
     */
    public String title(String pattern) {
        String title = StringTools.findFirstMatch(html, pattern, 1);
        if (null != title) {
            title = title.replaceAll("\n", " ").replaceAll("\t", " ").replaceAll("\"", "\\\"");
            title = StringTools.clean(title).trim();
        }
        return title;
    }

    /**
     * Extracts the year.
     * @param pattern Pattern for year.
     * @return Extracted year. If there's no match, returns -1.
     */
    public int year(String pattern) {
        int year = -1;
        try {
            year = Integer.parseInt(StringTools.findFirstMatch(html, pattern, 1));
        } catch (NumberFormatException nfe) {
        }
        return year;
    }
}
