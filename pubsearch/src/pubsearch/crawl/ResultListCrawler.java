package pubsearch.crawl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import pubsearch.StringTools;
import pubsearch.data.PDatabase;
import pubsearch.data.Publication;

/**
 * Találati oldalak bejárását végzi el: kigyűjti a linkeket és levezényli a
 * publikációk adatlapjainak feldolgozását.
 *
 * @author Zsolt
 */
public class ResultListCrawler extends ACrawler {

    //in
    private PDatabase pdb;
    private String url;
    private String queryString;
    private String method;
    private int transLev;
    //inside
    private Set<String> alreadyCrawled = new HashSet<String>(); // találati listán belüli duplikátokra (pl. liinwww page1=page2)
    //out
    private List<Publication> publications = new ArrayList<Publication>();

    public ResultListCrawler(PDatabase pdb, String url, String queryString, String method, int transLev) {
        this.pdb = pdb;
        this.url = url;
        this.queryString = (null != queryString) ? queryString : "";
        this.method = method;
        this.transLev = transLev;
        setPriority(6);
    }

    public List<Publication> getPublications() {
        return publications;
    }

    /**
     * Bejárja a találati lista oldalakat, kinyeri a linkeket, és PubPageCrawler
     * szálakat indít, melyek feldolgozzák a találatok adatait. Ezután megvárja
     * azok befejeződését, majd lekéri az eredményeket.
     */
    protected void crawl() {
        crawlers.clear();
        publications.clear();
        alreadyCrawled.clear();

        int startIndex = pdb.getFirstIndex();
        int resultsPerPage = pdb.getResultsPerPage();
        int resultPageNo = 0;
        int newResultCount;
        do {

            if (isInterrupted()) {
                break;
            }

            resultPageNo++;
            newResultCount = 0;

            /*
             * Download result list page
             */
            String startModifier = String.format("&%s=%d", pdb.getStartField(), startIndex);
            //System.out.println(pdb.getName() + " RLC page=" + resultPageNo);
            HTTPRequestEx req = new HTTPRequestEx(url, queryString + startModifier, method);
            if (req.submit()) {
                String html = req.getHtml();

                if (isInterrupted()) {
                    break;
                }

                if (null != StringTools.findFirstMatch(html, pdb.getNoResultsTextPattern(), 1)) {
                    break;
                }

                /*
                 * Crawl results on this page
                 */
                crawlers.clear();

                String listPattern = pdb.getResultListPattern();
                String listItemPattern = pdb.getResultListItemPattern();

                if (null != listPattern) {
                    html = StringTools.findFirstMatch(html, listPattern, 1);
                }
                if (null != html) {
                    if (null != listItemPattern) {
                        List<String> listItems = StringTools.findAllMatch(html, listItemPattern, 1);

                        for (String listItem : listItems) {
                            Extract extract = new Extract(listItem);
                            String u = extract.URL(pdb.getPubPageLinkPattern(), pdb.getBaseUrl(), pdb.getPubPageLinkModFormat());
                            if (!pdb.getPubPageLinkModFormat().equals("NOFOLLOW")) {
                                if (startPPCfor(u)) {
                                    newResultCount++;
                                }
                            } else {
                                newResultCount++; // TODO ebben a blokkban is kéne valahogy ellenőrizni a duplikátokat...
                                String authors = extract.authors(pdb.getAuthorsPattern());
                                String title = extract.title(pdb.getTitlePattern());
                                int year = extract.year(pdb.getYearPattern());

                                if (null != authors && null != title) {

                                    Set<Publication> citedBy = new HashSet<Publication>();
                                    if (transLev > 0) {
                                        String refPubListURL = extract.URL(pdb.getRefPubListPageLinkPattern(), pdb.getBaseUrl(), "");
                                        if (null != refPubListURL) {
                                            ResultListCrawler rlc = new ResultListCrawler(pdb, refPubListURL, "", pdb.getSubmitMethod(), transLev - 1);
                                            rlc.launch(false);
                                            citedBy.addAll(rlc.getPublications());
                                        }
                                    }

                                    Publication publication = Publication.getReferenceFor(authors, title, year, pdb);
                                    publication.setUrl(u);
                                    publication.getCitedBy().addAll(citedBy);
                                    Publication.store(publication);
                                    publications.add(publication);
                                }
                            }
                        }
                    } else {
                        List<String> current = new Extract(html).URLs(pdb.getPubPageLinkPattern(), pdb.getBaseUrl(), pdb.getPubPageLinkModFormat());
                        for (String u : current) {
                            if (startPPCfor(u)) {
                                newResultCount++;
                            }
                        }
                    }
                }

                /*
                 * Wait for threads to finish and get grabbed publications
                 */
                waitForCrawlers(null);

                for (ACrawler c : crawlers) {
                    if (c instanceof PubPageCrawler) {
                        PubPageCrawler ppc = (PubPageCrawler) c;
                        if (null != ppc.getPublication()) {
                            publications.add(ppc.getPublication());
                        }
                    }
                }

                // <liinwww.ira.uka.de fix>
                if (pdb.getBaseUrl().equals("http://liinwww.ira.uka.de/") && 0 < newResultCount) {
                    //System.out.println(pdb.getName() + " " + "Force page advance.");
                    newResultCount = resultsPerPage;
                    /*
                     * Azért kell, mert a liinwww.ira.uka.de összevonja a linkeket, nem mindig pont rpp
                     * db találat van az oldalon, így a newResultCount==rpp feltétellel kiszállna az első
                     * oldalnál. A newResultCount>0 feltétel lenne neki jó, de így viszont minden más
                     * adatbázis esetén +1 oldal letöltődne, ami pazarlás.
                     */
                }
                // </liinwww.ira.uka.de fix>
            }

            startIndex += resultsPerPage;
        } while (newResultCount == resultsPerPage);
    }

    private boolean startPPCfor(String url) {
        if (!alreadyCrawled.contains(url)) {
            alreadyCrawled.add(url);

            PubPageCrawler ppc = new PubPageCrawler(pdb, url, transLev);
            crawlers.add((ACrawler) ppc);
            ppc.launch(true);
            return true;
        }
        return false;
    }
}
