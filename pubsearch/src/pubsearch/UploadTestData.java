package pubsearch;

import pubsearch.data.Connection;
import pubsearch.data.Importer;
import pubsearch.data.PDatabase;
import pubsearch.data.Publication;

/**
 * Segédprogram, tesztadatok feltöltésére.
 *
 * @author Zsolt
 */
public class UploadTestData {

    public static void main(String[] args) {

        Config.loadMySQLConfig();
        Connection.tryInit();
        Importer.loadPDatabases();
        if (true) {
            return;
        }

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

        /*
         * PDatabase db3 = new PDatabase();
         * //db3.setId(3L);
         * db3.setName("liinwww.ira.uka.de");
         * db3.setBaseUrl("http://liinwww.ira.uka.de/");
         * db3.setSubmitUrl("csbib");
         * db3.setSubmitParamsFormat("maxnum=100&query=+au:\"%s\"");
         * db3.setPubPageLinkPattern("<td class=\"biblinks\".*?\"/(.*?bibshow.*?)\"");
         * db3.setFirstIndex(1);
         * db3.setResultsPerPage(100);
         * Connection.getEm().getTransactionSync().begin();
         * PDatabase p = Connection.getEm().find(PDatabase.class, db3.getName());
         * if (null == p) {
         * System.out.println("persist");
         * Connection.getEm().persist(db3);
         * }
         * else {
         *
         * p.setBaseUrl("-NEW-0");
         *
         * }
         * Connection.getEm().getTransactionSync().commit();
         */

        ///System.out.println(db3.getId());
        //db3.store();
        /*
         * System.out.println(db3.getId());
         * List<PDatabase> pl = Connection.getEm().createQuery("SELECT p FROM PDatabase p WHERE p.name=\""+db3.getName()+"\"").getResultList();
         * if (!pl.isEmpty()) {
         * System.out.println("rewrite");
         * PDatabase p = pl.get(0);
         *
         * Connection.getEm().detach(p);
         * Connection.getEm().merge(db3);
         * }
         */


        // DATA
        Connection.getEm().getTransaction().begin();
        /*
         * Publication pub1 = Publication.getReferenceFor("title1", 2001);
         * pub1.setAuthors("authors1");
         *
         * Publication pub2 = Publication.getReferenceFor("title2", 2002);
         * pub2.setAuthors("authors2");
         *
         *
         * pub2.addCitedBy(pub1);
         *
         *
         * //Connection.getEm().persist(pub1); // felesleges
         * Connection.getEm().persist(pub2);
         */

        /*
         * PDatabase pdb1 = PDatabase.getReferenceFor("CiteSeerX");
         * pdb1.setBaseUrl("hello");
         * pdb1.setSubmitParamsFormat("k");
         * Connection.getEm().persist(pdb1);
         */

        PDatabase db1 = PDatabase.getReferenceFor("db1");
        db1.setBaseUrl("bu1");
        db1.setSubmitParamsFormat("none");

        Publication p = Publication.getReferenceFor("authors", "title", 2000, db1);
        Publication p2 = Publication.getReferenceFor("a\nb", "title2", -1, db1);

        p.setBibtex("@INPROCEEDINGS{Porras97emerald:event,    author = {Phillip A. Porras and Peter G. Neumann},    title = {EMERALD: Event monitoring enabling responses to anomalous live disturbances},    booktitle = {In Proceedings of the 20th National Information Systems Security Conference},    year = {1997},    pages = {353--365}}");
        p.addCitedBy(p2);

        Connection.getEm().persist(db1);
        Connection.getEm().persist(p);
        Connection.getEm().persist(p2);
        Connection.getEm().getTransaction().commit();



        /*
         * Publication pub1 = new Publication("bibtex1", "authors1", "title1", 2001);
         * Publication pub2 = new Publication("bibtex2", "authors2", "title2", 2002);
         *
         * //Link link1 = new Link("link2pub1", pub1, db1);
         * Link link1 = new Link();
         * link1.setUrl("link2pub2");
         * link1.setPublication(pub1);
         * pub1.getLinks().add(link1);
         * link1.store();
         *
         * //pub1.getLinks().add(link1);
         * //db1.getLinks().add(link1);
         *
         * pub1.getCitedBy().add(pub2);
         * //pub2.getCites().add(pub1);
         *
         * Publication pub3 = new Publication("bibtex3", "a3", "t3", 2003);
         * //pub3.getCitedBy().add(pub2);
         * //pub2.getCites().add(pub3);
         *
         * //pub1.getCitedBy().add(pub3);
         *
         * //link1.store();
         * //db1.store();
         * Publication.store(pub1);
         * //Publication.store(pub2);
         * //Publication.store(pub3);
         *
         */

        /*
         * Publication pub3 = new Publication("bibtex3", "NEW", "title1", 2001);
         * pub3.storeWithUpdate();
         *
         * System.out.println("TEST DATA UPLOADED.");
         */
    }
}
