package pubsearch.crawl;

import pubsearch.StringTools;
import pubsearch.data.Connection;
import pubsearch.data.PDatabase;
import pubsearch.data.Publication;

/**
 * Egy publikáció adatait tartalmazó oldalt kezel le.
 *
 * @author Zsolt
 */
public class PubPageCrawler extends Thread {

    //in
    private PDatabase pdb;
    private String url;
    private int transLev;
    //out
    private Publication publication;
    private long bytes;

    public PubPageCrawler(PDatabase pdb, String url, int transLev) {
        this.pdb = pdb;
        this.url = url;
        this.transLev = transLev;
    }

    @Override
    public void run() {
        crawl();
    }

    /**
     * Begyűjt minden adatot a publikációról, majd feltölti az adatbázisba.
     */
    public void crawl() {
        HTTPRequestEx req = new HTTPRequestEx(url);
        if (req.submit(3)) {
            String html = req.getHtml();
            bytes += html.length();

            // bibtex
            String bibtex = null;
            String bibtexLink = StringTools.findFirstMatch(html, pdb.getBibtexLinkPattern(), 1);
            if (null != bibtexLink) {
                HTTPRequestEx bibreq = new HTTPRequestEx(pdb.getBaseUrl() + bibtexLink);
                if (bibreq.submit(3)) {
                    String bibhtml = bibreq.getHtml();
                    bytes += bibhtml.length();
                    bibtex = StringTools.findFirstMatch(bibhtml, pdb.getBibtexPattern(), 1);
                }
            } else {
                bibtex = StringTools.findFirstMatch(html, pdb.getBibtexPattern(), 1);
            }

            // author, title, year
            String authors;
            String title;
            int year = -1;
            if (null != bibtex) {
                bibtex = StringTools.clean(bibtex);

                authors = StringTools.findFirstMatch(bibtex, "author.*?=.*?(?:\"|\\{)(.*?)(?:\"|\\}),", 1);
                /*
                 * if (null != authors) {
                 * authors = authors.replaceAll("\\\\'\\{", "").replaceAll("\\}", ""); // kiszedjuk az ekezetes betuk BibTeX jelölését \'{a} -> a
                 * authors = authors.replaceAll("\n", " ").replaceAll("\t", " ");
                 * authors = StringTools.clean(authors).trim();
                 * }
                 */

                String titlePattern = "[^k]title.*?=.*?\"(.*?)\",|[^k]title.*?=.*?\\{(.*?)\\},";
                title = StringTools.findFirstMatch(bibtex, titlePattern, 1);
                if (null == title) {
                    title = StringTools.findFirstMatch(bibtex, titlePattern, 2);
                }

                try {
                    year = Integer.parseInt(StringTools.findFirstMatch(bibtex, "year.*?=.*?(?:\"|\\{)([0-9]{4})(?:\"|\\}),", 1));
                } catch (NumberFormatException nfe) {
                }
            } else {
                authors = StringTools.findFirstMatch(html, pdb.getAuthorsPattern(), 1);
                /*
                 * if (null != authors) {
                 * authors = authors.replaceAll("[0-9]{1,2}", "");
                 * authors = StringTools.clean(authors).replaceAll(" , ", " and ").trim();
                 * }
                 */

                title = StringTools.findFirstMatch(html, pdb.getTitlePattern(), 1);

                try {
                    year = Integer.parseInt(StringTools.findFirstMatch(html, pdb.getYearPattern(), 1));
                } catch (NumberFormatException nfe) {
                }
            }

            if (null != authors) {
                authors = authors.replaceAll("\\\\'\\{", "").replaceAll("\\{\\\\'", "").replaceAll("\\}", "");
                authors = authors.replaceAll("[0-9]{1,2}", "");
                authors = authors.replaceAll("\n", " ").replaceAll("\t", " ");
                authors = StringTools.clean(authors).trim();
                authors = authors.replaceAll(" , ", " and ").trim();
            }
            if (null != title) {
                title = title.replaceAll("\n", " ").replaceAll("\t", " ");
                title = StringTools.clean(title).trim();
            }

            if (transLev > 0) {
                // TODO valahol itt a refPub crawl, transLev-re vigyázni
                // kiszedi a listát és a PubPageCrawlereket    transLev-1  -el indítja!
                // + vigyázni arra, hogy a bytes-ot mindig szedjük ki!!!
                // TODO a ciklusokban figyelni az isInterrupted()-et!!!!
                /*
                 * refPubListPageURL = StringTools.findFirstMatch(html, pdb.getRefPubListPageLinkPattern(), 1);
                 * if (null != refPubListPageURL) {
                 * refPubListPageURL = pdb.getBaseUrl() + refPubListPageURL;
                 * } else { b módszer: block-olós }
                 */
            }

            /*System.out.println("      a = " + authors);
            System.out.println("      t = " + title);
            System.out.println("      y = " + year);*/
            if (null != authors && null != title) {
                Connection.getEm().getTransaction().begin();
                Publication pub = Publication.getReferenceFor(authors, title, year, pdb);
                pub.setBibtex(bibtex);
                pub.setUrl(url);
                Connection.getEm().persist(pub);
                Connection.getEm().getTransaction().commit();
            }
        }
    }

    public Publication getPublication() {
        return publication;
    }

    public long getBytes() {
        return bytes;
    }
}
