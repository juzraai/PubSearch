package pubsearch.data;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import pubsearch.config.ConfigModel;

/**
 * Az adatbáziskapcsolatot innen érik el az osztályok. Jelen esetben a JPA használatához szükséges EntityManager objektumot.
 *
 * @author Zsolt
 */
public class Connection {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static int lastError = 0;
    public static final int SQL_ERROR = 1;
    public static final int JPA_ERROR = 2;

    public static EntityManager getEm() {
        return em;
    }

    public static int getLastError() {
        return lastError;
    }

    /**
     * Létrehozza az adatbázist (ha még nem létezik), valamint a kapcsolatot
     * a JPA-val (ami pedig csatlakozik az adatbázishoz).
     */
    private static void init() throws SQLException {
        /*
         * Az SQLException-t azért kívül kezeljük le, mert a JPA kivételeit
         * csak kívül tudjuk lekezelni, és így együtt kezeljük le mindkettőt.
         */

        /*
         * Létrehozzuk az adatbázist, ha még nincs (egyúttal teszteljük a konfigot)
         */
        java.sql.Connection c = DriverManager.getConnection("jdbc:mysql://" + ConfigModel.getJdbcUrl() + "/mysql", ConfigModel.getJdbcUser(), ConfigModel.getJdbcPass());
        Statement s = c.createStatement();
        s.execute("CREATE DATABASE IF NOT EXISTS pubsearch;");
        //s.execute("DROP TABLE PubDb;");
        s.close();
        c.close();
        System.out.println("MYSQL CONNECTION OK.");

        /*
         * Csatalakoztatjuk a JPA-t.
         */
        Map props = new HashMap();
        props.put("javax.persistence.jdbc.url", "jdbc:mysql://" + ConfigModel.getJdbcUrl() + "/pubsearch");
        props.put("javax.persistence.jdbc.user", ConfigModel.getJdbcUser());
        props.put("javax.persistence.jdbc.password", ConfigModel.getJdbcPass());
        //props.put(PersistenceUnitProperties.)
        emf = Persistence.createEntityManagerFactory("pubsearch", props);
        em = emf.createEntityManager();
        System.out.println("JPA CONNECTION BUILT.");

        /*
         * Feltöltjük a publikációs adatbázisok adatait. Ezt a CRAWL package-ben kéne már!!!
         */
        // TODO táblázat Excel -> CSV, beolvasót írni rá, ami felépít egy List<PubDb>-t, és egy ciklussal berántja JPA-ba
        /*List<PubDb> pubdbs = new ArrayList<PubDb>();
        //(id, name, baseUrl, submitUrl, submitMethod, submitParametersFormat, pubPageLinkPattern, pubPageLinkModFormat, nextPageLinkPattern, authorsPattern, titlePattern, yearPattern, bibtexLinkPattern, bibtexPattern)
        pubdbs.add(new PubDb(1L, "CiteSeerX", "http://citeseerx.ist.psu.edu/", "search", "GET", "q=title%3A%s+AND+author%3A%s&sort=cite&t=doc", "href=\"/(viewdoc/summary(.*?))\"", null, "href=(.*?)>Next", null, "<p>(@(.*?))</p>", null, null, null));
        //pubdbs.add(new PubDb(2L, "ACM", "http://dl.acm.org/", "results.cfm"))

        for (PubDb pubdb : pubdbs) {
            try {
                pubdb.store();
                System.out.println("PubDB added: " + pubdb.getName());
            } catch (Throwable t) {
            }
        }*/
        
        /*Publication p = new Publication(null, "sz1", "c1", 1);
        p.store();
        new Link("url1", 0, p.getId().intValue()).store();
        System.out.println("TEST DATA STORED.");*/

        System.out.println("INIT DONE.");
    }

    /**
     * Megkísérli az inicializálást. Hiba esetén a lastError mező értékében tájékoztat,
     * hogy SQL vagy JPA kivételről volt-e szó.
     * @return sikerült-e
     */
    public static boolean tryInit() {
        try {
            init();
            return true;
        } catch (Throwable t) {
            lastError = (t instanceof SQLException) ? SQL_ERROR : JPA_ERROR;
            return false;
        }
    }
}
