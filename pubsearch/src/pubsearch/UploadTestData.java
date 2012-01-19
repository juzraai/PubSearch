package pubsearch;

import pubsearch.data.Connection;
import pubsearch.data.Link;
import pubsearch.data.PDatabase;
import pubsearch.data.Publication;

/**
 * Segédprogram, tesztadatok feltöltésére.
 *
 * @author Zsolt
 */
public class UploadTestData {

    public static void main(String[] args) {

        Config.load();
        Connection.tryInit();

        /*
         * PDatabase db1 = new PDatabase();
         * db1.setName("CiteSeerX");
         * db1.setBaseUrl("http://citeseerx.ist.psu.edu/");
         * db1.setSubmitUrl("search");
         * db1.setSubmitParamsFormat("sort=cite&t=doc&q=author:%s");
         * db1.setPubPageLinkPattern("href=\"/(viewdoc/summary.*?)\"");
         * db1.store();
         */
        // CiteSeerX: 10/0/start - default config

        /*
         * PDatabase db2 = new PDatabase();
         * db2.setName("ACM");
         * db2.setBaseUrl("http://dl.acm.org/");
         * db2.setSubmitUrl("results.cfm");
         * db2.setSubmitParamsFormat("adv=1&COLL=DL&DL=ACM&termzone=all&peoplezone=Author&people=%s&peoplehow=and&short=1");
         * db2.setSubmitParamsWithTitleFormat("");
         * db2.setPubPageLinkPattern("<td colspan=\"3\">.*?<a href=\"(citation.cfm\\?.*?)\"");
         * db2.setFirstIndex(1);
         * db2.setResultsPerPage(50);
         * db2.store();
         */

        PDatabase db3 = new PDatabase();
        db3.setName("liinwww.ira.uka.de");
        db3.setBaseUrl("http://liinwww.ira.uka.de/");
        db3.setSubmitUrl("csbib");
        db3.setSubmitParamsFormat("maxnum=100&query=+au:\"%s\"");
        db3.setPubPageLinkPattern("<td class=\"biblinks\".*?\"/(.*?bibshow.*?)\"");
        db3.setFirstIndex(1);
        db3.setResultsPerPage(100);
        db3.store();


        // DATA

        /*
         * Publication pub1 = new Publication("bibtex1", "authors1", "title1", 2001);
         * Publication pub2 = new Publication("bibtex2", "authors2", "title2", 2002);
         *
         * Link link1 = new Link("link2pub1", pub1, db1);
         *
         * pub1.getLinks().add(link1);
         * db1.getLinks().add(link1);
         *
         * pub1.getCitedBy().add(pub2);
         * pub2.getCites().add(pub1);
         *
         * link1.store();
         * db1.store();
         * pub1.storeWithUpdate();
         * pub2.storeWithUpdate();
         */


        /*
         * Publication pub3 = new Publication("bibtex3", "NEW", "title1", 2001);
         * pub3.storeWithUpdate();
         *
         * System.out.println("TEST DATA UPLOADED.");
         */
    }
}
