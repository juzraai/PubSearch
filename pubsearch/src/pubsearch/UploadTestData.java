package pubsearch;

import pubsearch.data.Connection;
import pubsearch.data.Link;
import pubsearch.data.PDatabase;
import pubsearch.data.Publication;

/**
 * Segédprogram, tesztadatok feltöltésére.
 * @author Zsolt
 */
public class UploadTestData {
    
    public static void main(String[] args) {
        Config.load();
        Connection.tryInit();
        
        PDatabase db1 = new PDatabase();
        /*db1.setName("CiteSeerX");
        db1.setBaseUrl("http://citeseerx.ist.psu.edu/");
        db1.setSubmitUrl("search");
        db1.setSubmitParamsFormat("sort=cite&t=doc&q=author:%s");
        db1.setSubmitParamsWithTitleFormat("");
        db1.setPubPageLinkPattern("href=\"/(viewdoc/summary.*?)\"");
        db1.setNextPageLinkPattern("<div id=\"pager\">.*?<a href=\"/(.*?)\">Next.*?</a>");*/
        db1.setName("ACM");
        db1.setBaseUrl("http://dl.acm.org/");
        db1.setSubmitUrl("results.cfm");
        db1.setSubmitParamsFormat("short=1&nquery=(Author:%s)");
        db1.setSubmitParamsWithTitleFormat("");
        db1.setPubPageLinkPattern("href=\\\"(citation.cfm\\?.*?)\"");
        db1.setNextPageLinkPattern("<td.*?Result.*?page:.*<a href=\"(.*?start.*?)\">.*?next</a>");
        
        

        Publication pub1 = new Publication("bibtex1", "authors1", "title1", 2001);
        Publication pub2 = new Publication("bibtex2", "authors2", "title2", 2002);

        Link link1 = new Link("link2pub1");


        link1.setPublication(pub1);
        link1.setPdatabase(db1);
        pub1.getLinks().add(link1);
        db1.getLinks().add(link1);

        pub1.getCitedBy().add(pub2);
        pub2.getCites().add(pub1);

        link1.store();
        db1.store();
        pub1.store();
        pub2.store();
        
        System.out.println("TEST DATA UPLOADED.");
    }
    
}
