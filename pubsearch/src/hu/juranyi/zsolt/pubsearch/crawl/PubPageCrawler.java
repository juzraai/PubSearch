package hu.juranyi.zsolt.pubsearch.crawl;

import hu.juranyi.zsolt.pubsearch.StringTools;
import hu.juranyi.zsolt.pubsearch.data.PDatabase;
import hu.juranyi.zsolt.pubsearch.data.Publication;
import java.util.HashSet;
import java.util.Set;

/**
 * Processes a pubpage (page which contains details of a publication): extracts
 * basic information and starts the list crawler for referring publications if
 * needed (if transitivity level is above 0).
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
 */
public class PubPageCrawler extends ACrawler {

    //in
    private PDatabase pdb;
    private String page; // URL or HTML, 'download' field will tell us
    private int transLev;
    private boolean refPubMode;
    private boolean downloadMode;
    //out
    private Publication publication;

    /**
     * Sets up the pubpage crawling.
     * @param pdb PDatabase object which contains information for database specific crawling.
     * @param page Pubpage URL or HTML content - 'download' parameter will tell.
     * @param transLev 0: only basic information, 1: referrer publications also 2: referrer of referrers also will be grabbed.
     * @param refPubMode If true, it handles the pubpage as pubpage of a referring publication which may need different patterns to be used.
     * @param download If true, URL given in 'page' parameter will be used to download the page, otherwise 'page' will be used as the downloaded HTML content.
     */
    public PubPageCrawler(PDatabase pdb, String page, int transLev, boolean refPubMode, boolean download) {
        this.pdb = pdb;
        this.page = page;
        this.transLev = transLev;
        this.refPubMode = refPubMode;
        this.downloadMode = download;
        setName("Crawler, PubPage tr=" + transLev + ", pdb=" + pdb.getName() + "; " + getName());
        setPriority(7);
    }

    public Publication getPublication() {
        return publication;
    }

    /**
     * Downloads the page if needed, grabs BibTeX if any, extracts authors, title
     * and year, and crawls referring publications if transitivity level is above 0.
     */
    @Override
    protected void crawl() {
        String html = null;

        /*
         * Download page if needed
         */
        if (downloadMode) {
            if (isInterrupted()) {
                return;
            }

            HTTPRequestEx req = new HTTPRequestEx(page);
            if (req.submit()) {
                html = req.getHtml();
            }
        } else {
            html = page;

        }

        if (null != html) {

            /*
             * Get BibTeX if can
             */
            String bibtex = null;
            String bibtexURL = new Extract(html).URL(pdb.getBibtexLinkPattern(), pdb.getBaseUrl(), "");
            if (null != bibtexURL) {

                if (isInterrupted()) {
                    return;
                }

                HTTPRequestEx bibreq = new HTTPRequestEx(bibtexURL);
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
                bibtex = bibtex.replace("\\\"", "");

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

                String authorsPattern = pdb.getAuthorsPattern();
                String titlePattern = pdb.getTitlePattern();
                String yearPattern = pdb.getYearPattern();

                if (refPubMode) {
                    if (null != pdb.getRefPubAuthorsPattern()) {
                        authorsPattern = pdb.getRefPubAuthorsPattern();
                    }
                    if (null != pdb.getRefPubTitlePattern()) {
                        titlePattern = pdb.getRefPubTitlePattern();
                    }
                    if (null != pdb.getRefPubYearPattern()) {
                        yearPattern = pdb.getRefPubYearPattern();
                    }
                }

                authors = extract.authors(authorsPattern);
                title = extract.title(titlePattern);
                year = extract.year(yearPattern);
            }

            if (null != authors && null != title) {
                Set<Publication> citedBy = new HashSet<Publication>();

                if (transLev > 0 && !isInterrupted()) {
                    String refPubListURL = new Extract(html).URL(pdb.getRefPubListPageLinkPattern(), pdb.getBaseUrl(), "");

                    if (null != refPubListURL && null == pdb.getRefPubListPattern()) { // CiteSeerX, Google Scholar

                        if (!isInterrupted()) {
                            PubListCrawler plc = new PubListCrawler(pdb, refPubListURL, transLev - 1, true);
                            plc.launch(false);
                            citedBy.addAll(plc.getPublications());
                        }

                    } else if (null != pdb.getRefPubListPattern()) { // ACM, MetaPress, Springer
                        String refPubListHTML = null;
                        if (null != refPubListURL) { // Springer

                            if (!isInterrupted()) {
                                HTTPRequestEx req = new HTTPRequestEx(refPubListURL);
                                if (req.submit()) {
                                    refPubListHTML = req.getHtml();
                                }
                            }

                        } else if (null == pdb.getRefPubListPageLinkPattern()) { // ACM, MetaPress
                            refPubListHTML = html;
                        }
                        if (null != refPubListHTML) {
                            PubListHTMLCrawler plc = new PubListHTMLCrawler(pdb, refPubListHTML, transLev - 1, true);
                            plc.launch(false);
                            citedBy.addAll(plc.getPublications());
                        }
                    }
                }

                publication = Publication.getReferenceFor(authors, title, year, pdb);
                publication.setBibtex(bibtex);
                if (downloadMode) {
                    publication.setUrl(page);
                } else {
                    publication.setUrl(new Extract(page).URL(pdb.getPubPageLinkPattern(), pdb.getBaseUrl(), pdb.getPubPageLinkModFormat()));
                }
                publication.getCitedBy().addAll(citedBy);
                Publication.store(publication);
            } else if (downloadMode) {
                System.err.println("Parse failed: " + page);
            }
        }
    }
}
