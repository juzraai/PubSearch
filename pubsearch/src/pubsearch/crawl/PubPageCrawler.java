package pubsearch.crawl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.EntityTransaction;
import pubsearch.StringTools;
import pubsearch.data.Connection;
import pubsearch.data.PDatabase;
import pubsearch.data.Publication;

/**
 * Egy publikáció adatlapját tölti le, elemzi és ha kell, begyűjti a hivatkozó
 * publikációk adatait is.
 *
 * @author Zsolt
 */
public class PubPageCrawler extends ACrawler {

    //in
    private PDatabase pdb;
    private String url;
    private int transLev;
    //out
    private Publication publication;

    public PubPageCrawler(PDatabase pdb, String url, int transLev) {
        this.pdb = pdb;
        this.url = url;
        this.transLev = transLev;
    }

    public Publication getPublication() {
        return publication;
    }

    /**
     * Begyűjt minden adatot a publikációról, majd feltölti az adatbázisba.
     */
    protected void crawl() {
        try {
            System.out.println(StringTools.rpad(pdb.getName(), 20, ' ') + url);
            HTTPRequestEx req = new HTTPRequestEx(url);
            if (req.submit()) {
                String html = req.getHtml();
                bytes += req.getBytes();

                // bibtex
                String bibtex = null;
                String bibtexLink = StringTools.findFirstMatch(html, pdb.getBibtexLinkPattern(), 1);
                if (null != bibtexLink) {

                    if (isInterrupted()) {
                        throw new InterruptedException();
                    }

                    HTTPRequestEx bibreq = new HTTPRequestEx(pdb.getBaseUrl() + bibtexLink);
                    if (bibreq.submit()) {
                        String bibhtml = bibreq.getHtml();
                        bytes += bibreq.getBytes();
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
                    bibtex = bibtex.replaceAll("<br />|<br/>|<br>", "\n");
                    bibtex = StringTools.clean(bibtex);
                    //bibtex = bibtex.replaceFirst("abstract =.*?(\"|\\{).*?(\"|\\}),", ""); // TODO FIX (remove abstract field)

                    authors = StringTools.findFirstMatch(bibtex, "author.*?=.*?(?:\"|\\{)(.*?)(?:\"|\\}),", 1);

                    String titlePattern = "[^k]title.*?=.*?\"(.*?)\",|[^k]title.*?=.*?\\{(.*?)\\},";
                    title = StringTools.findFirstMatch(bibtex, titlePattern, 1);
                    if (null == title) {
                        title = StringTools.findFirstMatch(bibtex, titlePattern, 2);
                    }

                    try {
                        year = Integer.parseInt(StringTools.findFirstMatch(bibtex, "year.*?=.*?(?:\"|\\{)([0-9]{4})(?:\"|\\})", 1));
                    } catch (NumberFormatException nfe) {
                    }
                } else {
                    authors = StringTools.findFirstMatch(html, pdb.getAuthorsPattern(), 1);

                    title = StringTools.findFirstMatch(html, pdb.getTitlePattern(), 1);

                    try {
                        year = Integer.parseInt(StringTools.findFirstMatch(html, pdb.getYearPattern(), 1));
                    } catch (NumberFormatException nfe) {
                    }
                }

                if (null != authors) {
                    authors = authors.replaceAll("\\\\('|\")\\{", "").replaceAll("\\{\\\\('|\")", "").replaceAll("\\}", "").replaceAll("\\\\", "");
                    authors = authors.replaceAll("[0-9]{1,2}", "");
                    authors = authors.replaceAll("\n", " ").replaceAll("\t", " ").replaceAll("\"", "\\\"");
                    authors = StringTools.clean(authors).trim();
                    authors = authors.replaceAll(" , ", " and ").trim();
                }
                if (null != title) {
                    title = title.replaceAll("\n", " ").replaceAll("\t", " ").replaceAll("\"", "\\\"");
                    title = StringTools.clean(title).trim();
                }

                // cited by
                Set<Publication> citedBy = new HashSet<Publication>();
                if (transLev > 0 && !isInterrupted()) {
                    if (null != pdb.getRefPubListPageLinkPattern() && null == pdb.getRefPubListBlockPattern()) {
                        // external oldal, de ugyanolyan lista, mint a sima találati lista
                        String u = StringTools.findFirstMatch(html, pdb.getRefPubListPageLinkPattern(), 1);
                        if (null != u) {
                            if (!u.startsWith("http:")) {
                                u = pdb.getBaseUrl() + u;
                            }
                            String[] up = u.split("\\?");
                            u = up[0];
                            String qs = null;
                            if (up.length > 1) {
                                qs = up[1];
                            }
                            ResultListCrawler rlc = new ResultListCrawler(pdb, u, qs, "GET", transLev - 1);
                            crawlers.add(rlc);
                            rlc.crawl();
                            bytes += rlc.getBytes();
                            citedBy.addAll(rlc.getPublications());
                        } else {
                            System.out.println("no citedby link");
                        }
                    } else if (null != pdb.getRefPubListBlockPattern()) {
                        // ha van link, letölti a html-t, azt elemzi tovább
                        // list blokkra szűkít
                        // match all result block
                        // kiszedi a 3 adatot
                        // TODO csinálni függvényeket: extractAuthors(from,pattern,group), cleanAuthors(authors), alkalmazni fent is!
                    }
                    /*
                     * refPubListPageURL = StringTools.findFirstMatch(html, pdb.getRefPubListPageLinkPattern(), 1);
                     * if (null != refPubListPageURL) {
                     * refPubListPageURL = pdb.getBaseUrl() + refPubListPageURL;
                     * } else { b módszer: block-olós }
                     */
                }

                // build object
                if (null != authors && null != title) {
                    publication = Publication.getReferenceFor(authors, title, year, pdb);
                    publication.setBibtex(bibtex);
                    publication.setUrl(url);
                    publication.getCitedBy().addAll(citedBy);
                } else {
                    System.err.println("Invalid pubpage or download error: " + url);
                }
            }
        } catch (InterruptedException e) {
            System.err.println(StringTools.rpad(pdb.getName(), 25, ' ') + url);
        }
    }
}
