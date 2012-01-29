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

        Publication p = Publication.getReferenceFor("a", "t", 0, PDatabase.getReferenceFor("ACM"));
        System.out.println(p.getId());
        Publication p2 = Publication.getReferenceFor("a", "t", 0, PDatabase.getReferenceFor("ACM"));
        System.out.println(p2.getId());
        /*Connection.getEm().getTransaction().begin();
        Connection.getEm().getTransaction().commit();*/
    }
}
