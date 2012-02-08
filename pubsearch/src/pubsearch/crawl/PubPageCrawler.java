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
        setName(getName() + " " + pdb.getName());
        setPriority(7);
    }

    public Publication getPublication() {
        return publication;
    }

    /**
     * Begyűjt minden adatot a publikációról, majd feltölti az adatbázisba.
     */
    protected void crawl() {
        try {
            HTTPRequestEx req = new HTTPRequestEx(url);
            if (req.submit()) {
                String html = req.getHtml();

                /*
                 * Get BibTeX if can
                 */
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

                /*
                 * Extract basic info
                 */
                String authors;
                String title;
                int year = -1;
                if (null != bibtex) {
                    bibtex = bibtex.replaceAll("<br />|<br/>|<br>", "\n");
                    bibtex = StringTools.clean(bibtex);
                    //bibtex = bibtex.replaceFirst("abstract =.*?(\"|\\{).*?(\"|\\}),", ""); // TODO FIX (remove abstract field)
                    Extract extract = new Extract(bibtex);
                    authors = extract.authors("author.*?=[^{]*?\"(.*?)\"");
                    if (null == authors) {
                        authors = extract.authors("author.*?=.*?\\{([^=]*)\\}");
                    }
                    title = extract.title("[^k]title.*?=[^{]*?\"(.*?)\"");
                    if (null == title) {
                        title = extract.title("[^k]title.*?=.*?\\{(.*?)\\}");
                    }
                    year = extract.year("year[^=]*?=[^=]*?([0-9]{4})");
                } else {
                    Extract extract = new Extract(html);
                    authors = extract.authors(pdb.getAuthorsPattern());
                    title = extract.title(pdb.getTitlePattern());
                    year = extract.year(pdb.getYearPattern());
                }

                /*
                 * Crawl cited by list
                 */
                Set<Publication> citedBy = new HashSet<Publication>();
                if (transLev > 0 && !isInterrupted()) {

                    String refPubListURL = StringTools.findFirstMatch(html, pdb.getRefPubListPageLinkPattern(), 1);
                    if (null != refPubListURL && !refPubListURL.startsWith("http:")) {
                        refPubListURL = pdb.getBaseUrl() + refPubListURL;
                    }

                    if (null != refPubListURL && null == pdb.getRefPubListPattern()) {
                        // külső oldal, formailag egyezik a ResultList-tel (CiteSeerX)
                        String[] up = refPubListURL.split("\\?");
                        refPubListURL = up[0];
                        String qs = null;
                        if (up.length > 1) {
                            qs = up[1];
                        }
                        ResultListCrawler rlc = new ResultListCrawler(pdb, refPubListURL, qs, "GET", transLev - 1);
                        rlc.crawl();
                        citedBy.addAll(rlc.getPublications());

                    } else if (null != pdb.getRefPubListPattern()) {
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
                        String refPubList = StringTools.findFirstMatch(refPubHTML, pdb.getRefPubListPattern(), 1);
                        if (null != refPubList) {
                            // linkek bejárása, ha lehetséges (ACM)
                            List<String> refPubURLs = new Extract(refPubList).URLs(pdb.getPubPageLinkPattern(), pdb.getBaseUrl(), pdb.getPubPageLinkModFormat());
                            if (refPubURLs.size() > 0) {
                                for (String refPubURL : refPubURLs) {
                                    PubPageCrawler ppc = new PubPageCrawler(pdb, refPubURL, transLev - 1);
                                    ppc.crawl();
                                    citedBy.add(ppc.getPublication());
                                }
                            } else {
                                // nincsenek linkek, listaelemblokkonkénti parszolás (Springer, MetaPress)
                                List<String> refPubListItems = StringTools.findAllMatch(refPubList, pdb.getRefPubListItemPattern(), 1);
                                if (null != refPubListItems) {
                                    for (String refPubListItem : refPubListItems) {
                                        Extract extract = new Extract(refPubListItem);
                                        String refPubAuthor = extract.authors(pdb.getRefPubAuthorsPattern());
                                        String refPubTitle = extract.title(pdb.getRefPubTitlePattern());
                                        int refPubYear = extract.year(pdb.getRefPubYearPattern());
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

                /*
                 * Build and store Publication object
                 */
                if (null != authors && null != title) {
                    publication = Publication.getReferenceFor(authors, title, year, pdb);
                    publication.setBibtex(bibtex);
                    publication.setUrl(url);
                    publication.getCitedBy().addAll(citedBy);
                    Publication.store(publication);
                }
            }
        } catch (InterruptedException e) {
        } finally {
            if (null == publication) {
                System.err.println("Parse failed: " + url);
            }
        }
    }
}
