package pubsearch.data;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import pubsearch.Config;

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

    public static synchronized EntityManager getEm() {
        return em;
    }

    public static synchronized boolean getTransactionIsAliveSync() {
        return em.getTransaction().isActive();
    }

    public static synchronized EntityTransaction getTransactionSync() {
        return em.getTransaction();
    }

    public static int getLastError() {
        return lastError;
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
            t.printStackTrace();
            lastError = (t instanceof SQLException) ? SQL_ERROR : JPA_ERROR;
            return false;
        }
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
        java.sql.Connection c = DriverManager.getConnection("jdbc:mysql://" + Config.getJdbcUrl() + "/mysql", Config.getJdbcUser(), Config.getJdbcPass());
        Statement s = c.createStatement();
        s.execute("CREATE DATABASE IF NOT EXISTS pubsearch;");
        s.close();
        c.close();
        System.out.println("MYSQL CONNECTION OK.");

        /*
         * Csatalakoztatjuk a JPA-t.
         */
        Map props = new HashMap();
        props.put("javax.persistence.jdbc.url", "jdbc:mysql://" + Config.getJdbcUrl() + "/pubsearch");
        props.put("javax.persistence.jdbc.user", Config.getJdbcUser());
        props.put("javax.persistence.jdbc.password", Config.getJdbcPass());
        //props.put("eclipselink.logging.level", "OFF");
        emf = Persistence.createEntityManagerFactory("pubsearch", props);
        em = emf.createEntityManager();
        System.out.println("JPA CONNECTION BUILT.");

        Importer.loadPDatabases();

        System.out.println("INIT DONE.");
    }
}
