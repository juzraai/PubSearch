package pubsearch.crawl;

import java.util.HashSet;
import java.util.Set;
import pubsearch.StringTools;
import pubsearch.data.PDatabase;
import pubsearch.data.Publication;

/**
 *
 * @author Zsolt
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
            String bibtexLink = StringTools.findFirstMatch(html, pdb.getBibtexLinkPattern(), 1);
            if (null != bibtexLink) {

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
                bibtex = bibtex.replace("\\\"", "");
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

                // TODO refPubMode based au/ti/y patterns!!!! --------------------------------------<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

                authors = extract.authors(pdb.getAuthorsPattern());
                title = extract.title(pdb.getTitlePattern());
                year = extract.year(pdb.getYearPattern());
            }

            if (null != authors && null != title) {
                Set<Publication> citedBy = new HashSet<Publication>();

                if (transLev > 0 && !isInterrupted()) {
                    String refPubListURL = new Extract(html).URL(pdb.getRefPubListPageLinkPattern(), pdb.getBaseUrl(), "");

                    if (null != refPubListURL && null == pdb.getRefPubListPattern()) { // CiteSeerX, Google Scholar
                        PubListCrawler plc = new PubListCrawler(pdb, refPubListURL, transLev - 1, true);
                        plc.launch(false);
                        citedBy.addAll(plc.getPublications());
                    } else if (null != pdb.getRefPubListPattern()) { // ACM, MetaPress, Springer
                        String refPubListHTML = null;
                        if (null != refPubListURL) { // Springer
                            HTTPRequestEx req = new HTTPRequestEx(refPubListURL);
                            if (req.submit()) {
                                refPubListHTML = req.getHtml();
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
                System.out.println("Parse failed: " + page);
            }
        }
    }
}
