package pubsearch.crawl;

import pubsearch.StringTools;
import pubsearch.data.PDatabase;
import pubsearch.data.Publication;

/**
 * Egy publikáció adatait tartalmazó oldalt kezel le.
 *
 * @author Zsolt
 */
public class PubPage {

    //in
    private PDatabase pDatabase;
    private String html;
    //out
    private Publication publication;
    private String refPubListPageURL;

    public PubPage(PDatabase pDatabase, String html) {
        this.pDatabase = pDatabase;
        this.html = html;
    }

    /**
     * Kiszedi a HTML lapból a publikáció adatait, és a hivatkozók listájára mutató
     * linket. Ha nem sikerül adatot kinyerni, akkor a publication mezőt nullra állítja,
     * így tesz az URL esetében is.
     */
    public void extractData() {
        // TODO bibtex, authors, title, year
        // ha van bibtex link, akkor lekéri a HTML-t (byte hozzáadást megoldani a Crawler-hez!) - talán azt is át kéne adni konstruktorba/fieldbe
        // ha van bibtex block
        //      lekéri azt + levágja 4096 karakterre
        // ha nincs, akkor authors, title, year

        // ha semmilyen adat nem nyerhető ki (pl. bibtexnél kapcsolódási hiba), akkor publication=null; return;

        // összerak egy publication-t.

        // VIGYÁZZUNK, HOGY A REFPUBS-OT NE AZ EXTERNAL BIBTEX OLDALON KERESSE, OTT NINCS !

        // ezt csak akkor, ha a transLev > 0 !
        /*refPubListPageURL = StringTools.findFirstMatch(html, pDatabase.getRefPubListPageLinkPattern(), 1);
        if (null != refPubListPageURL) {
            refPubListPageURL = pDatabase.getBaseUrl() + refPubListPageURL;
        }*/
    }

    public Publication getPublication() {
        return publication;
    }

    public String getRefPubListPageURL() {
        return refPubListPageURL;
    }
}
