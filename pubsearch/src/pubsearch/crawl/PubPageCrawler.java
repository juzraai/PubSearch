package pubsearch.crawl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import pubsearch.StringTools;
import pubsearch.data.Connection;
import pubsearch.data.PDatabase;
import pubsearch.data.Publication;

/**
 * Egy publikáció adatait tartalmazó oldalt kezel le.
 *
 * @author Zsolt
 */
public class PubPageCrawler {

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

    /**
     * Begyűjt minden adatot a publikációról, majd feltölti az adatbázisba.
     */
    public void crawl() {
        // TODO bibtex, authors, title, year
        // ha van bibtex link, akkor lekéri a HTML-t (byte hozzáadást megoldani a Crawler-hez!) - talán azt is át kéne adni konstruktorba/fieldbe
        // ha van bibtex block
        //      lekéri azt + levágja 4096 karakterre
        // ha nincs, akkor authors, title, year

        // ha semmilyen adat nem nyerhető ki (pl. bibtexnél kapcsolódási hiba), akkor publication=null; return;

        // összerak egy publication-t.

        // VIGYÁZZUNK, HOGY A REFPUBS-OT NE AZ EXTERNAL BIBTEX OLDALON KERESSE, OTT NINCS !

        // ezt csak akkor, ha a transLev > 0 !
        /*
         * refPubListPageURL = StringTools.findFirstMatch(html, pdb.getRefPubListPageLinkPattern(), 1);
         * if (null != refPubListPageURL) {
         * refPubListPageURL = pdb.getBaseUrl() + refPubListPageURL;
         * }
         */

        // -------------------------------------------
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

                    // <debug html output>
                    try {
                        BufferedWriter w = new BufferedWriter(new FileWriter("bib.html"));
                        w.write(bibhtml);
                        w.close();
                    } catch (IOException e) {
                    }
                    // </debug html output>

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
                if (null != authors) {
                    authors = authors.trim().replaceAll("\\\\'\\{|\\}", ""); // kiszedjuk az ekezetes betuk BibTeX jelölését \'{a} -> a
                }

                title = StringTools.findFirstMatch(bibtex, "[^(book)]title.*?=.*?(?:\"|\\{)(.*?)(?:\"|\\}),", 1);
                if (null != title) {
                    title = title.trim();
                }

                try {
                    year = Integer.parseInt(StringTools.findFirstMatch(bibtex, "year.*?=.*?(?:\"|\\{)([0-9]{4})(?:\"|\\}),", 1));
                } catch (NumberFormatException nfe) {
                }
            } else {
                authors = StringTools.findFirstMatch(html, pdb.getAuthorsPattern(), 1);
                if (null != authors) {
                    authors = authors.replaceAll("[0-9]{1,2}", "");
                    authors = StringTools.clean(authors).replaceAll(" , ", " and ").trim();
                }

                title = StringTools.findFirstMatch(html, pdb.getTitlePattern(), 1);
                if (null != title) {
                    title = title.trim();
                }

                try {
                    year = Integer.parseInt(StringTools.findFirstMatch(html, pdb.getYearPattern(), 1));
                } catch (NumberFormatException nfe) {
                }
            }

            // TODO valahol itt a refPub crawl

            System.out.println("      a = " + authors);
            System.out.println("      t = " + title);
            System.out.println("      y = " + year);
            if (null != authors && null != title) {
                Connection.getEm().getTransaction().begin();
                Publication pub = Publication.getReferenceFor(authors, title, year, pdb);
                pub.setBibtex(bibtex);
                pub.setUrl(url);
                Connection.getEm().persist(pub);
                Connection.getEm().getTransaction().commit();
            }
        }

        /*
         * String bibtex = html;
         * String bibtexLink = StringTools.findFirstMatch(html, pdb.getBibtexLinkPattern(), 1);
         * if (null != bibtexLink) {
         * HTTPRequestEx req = new HTTPRequestEx(pdb.getBaseUrl() + bibtexLink);
         * if (req.submit(3)) {
         * bibtex = req.getHtml();
         * bytes += bibtex.length();
         * }
         * }
         * String bibtexPattern = pdb.getBibtexPattern();
         * if (null != bibtexPattern) {
         * bibtex = StringTools.findFirstMatch(bibtex, bibtexPattern, 1);
         *
         * }
         */


    }

    public Publication getPublication() {
        return publication;
    }

    public long getBytes() {
        return bytes;
    }
}
