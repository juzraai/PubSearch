package pubsearch.crawl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import pubsearch.StringTools;
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
            System.out.println(pdb.getName() + "\t" + url);
            HTTPRequestEx req = new HTTPRequestEx(url);
            if (req.submit()) {
                String html = req.getHtml();

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

                    authors = extractAuthors(bibtex, "author.*?=[^{]*?\"(.*?)\"");
                    if (null == authors) {
                        authors = extractAuthors(bibtex, "author.*?=.*?\\{([^=]*)\\}");
                    }
                    title = extractTitle(bibtex, "[^k]title.*?=[^{]*?\"(.*?)\"");
                    if (null == title) {
                        title = extractTitle(bibtex, "[^k]title.*?=.*?\\{(.*?)\\}");
                    }
                    year = extractYear(bibtex, "year.*?=.*?([0-9]{4})");
                } else {
                    authors = extractAuthors(html, pdb.getAuthorsPattern());
                    title = extractTitle(html, pdb.getTitlePattern());
                    year = extractYear(html, pdb.getYearPattern());
                }

                // cited by
                Set<Publication> citedBy = new HashSet<Publication>();
                if (transLev > 0 && !isInterrupted()) {

                    String refPubListURL = StringTools.findFirstMatch(html, pdb.getRefPubListPageLinkPattern(), 1);
                    if (null != refPubListURL && !refPubListURL.startsWith("http:")) {
                        refPubListURL = pdb.getBaseUrl() + refPubListURL;
                    }

                    if (null != refPubListURL && null == pdb.getRefPubListBlockPattern()) {
                        // külső oldal, formailag egyezik a ResultList-tel (CiteSeerX)
                        String[] up = refPubListURL.split("\\?");
                        refPubListURL = up[0];
                        String qs = null;
                        if (up.length > 1) {
                            qs = up[1];
                        }
                        ResultListCrawler rlc = new ResultListCrawler(pdb, refPubListURL, qs, "GET", transLev - 1);
                        crawlers.add(rlc);
                        rlc.crawl();
                        citedBy.addAll(rlc.getPublications());

                    } else if (null != pdb.getRefPubListBlockPattern()) {
                        // lista formailag nem egyezik a ResultList-tel (ACM, Springer, MetaPress)

                        // külső oldal? (Springer)
                        String refPubHTML = html;
                        if (null != refPubListURL) {
                            HTTPRequestEx refPubReq = new HTTPRequestEx(refPubListURL);
                            if (refPubReq.submit()) {
                                refPubHTML = refPubReq.getHtml();
                            }
                        }

                        // listablokk (ACM, Springer, MetaPress)
                        String refPubListBlock = StringTools.findFirstMatch(refPubHTML, pdb.getRefPubListBlockPattern(), 1);
                        if (null != refPubListBlock) {
                            // linkek bejárása, ha lehetséges (ACM)
                            List<String> refPubURLs = new Parser(refPubListBlock, pdb).extractPubPageURLs();
                            if (refPubURLs.size() > 0) {
                                for (String refPubURL : refPubURLs) {
                                    PubPageCrawler ppc = new PubPageCrawler(pdb, refPubURL, transLev - 1);
                                    ppc.crawl();
                                    citedBy.add(ppc.getPublication());
                                }
                            } else {
                                // nincsenek linkek, listaelemblokkonkénti parszolás (Springer, MetaPress)
                                List<String> refPubBlocks = StringTools.findAllMatch(refPubListBlock, pdb.getRefPubBlockPattern(), 1);
                                if (null != refPubBlocks) {
                                    for (String refPubBlock : refPubBlocks) {
                                        String refPubAuthor = extractAuthors(refPubBlock, pdb.getRefPubAuthorsPattern());
                                        String refPubTitle = extractAuthors(refPubBlock, pdb.getRefPubTitlePattern());
                                        int refPubYear = extractYear(refPubBlock, pdb.getRefPubYearPattern());
                                        if (null != refPubAuthor && null != refPubTitle) {
                                            Publication rp = Publication.getReferenceFor(refPubAuthor, refPubTitle, refPubYear, pdb);
                                            Publication.store(rp);
                                            citedBy.add(rp);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // build object
                if (null != authors && null != title) {
                    publication = Publication.getReferenceFor(authors, title, year, pdb);
                    publication.setBibtex(bibtex);
                    publication.setUrl(url);
                    publication.getCitedBy().addAll(citedBy);
                    Publication.store(publication);
                }
            }
        } catch (InterruptedException e) {
            System.err.println(pdb.getName() + "\t" + url);
        }
    }

    private String extractAuthors(String html, String pattern) {
        String authors = StringTools.findFirstMatch(html, pattern, 1);
        if (null != authors) {
            authors = authors.replaceAll("\\\\('|\")\\{", "").replaceAll("\\{\\\\('|\")", "").replaceAll("\\}", "").replaceAll("\\\\", "");
            authors = authors.replaceAll("[0-9]{1,2}", "");
            authors = authors.replaceAll("\n", " ").replaceAll("\t", " ").replaceAll("\"", "\\\"");
            authors = StringTools.clean(authors).trim();
            authors = authors.replaceAll(" , ", " and ").trim();
        }
        return authors;
    }

    private String extractTitle(String html, String pattern) {
        String title = StringTools.findFirstMatch(html, pattern, 1);
        if (null != title) {
            title = title.replaceAll("\n", " ").replaceAll("\t", " ").replaceAll("\"", "\\\"");
            title = StringTools.clean(title).trim();
        }
        return title;
    }

    private int extractYear(String html, String pattern) {
        int year = -1;
        try {
            year = Integer.parseInt(StringTools.findFirstMatch(html, pattern, 1));
        } catch (NumberFormatException nfe) {
        }
        return year;
    }
}
