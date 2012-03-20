package hu.juranyi.zsolt.pubsearch.data;

import hu.juranyi.zsolt.pubsearch.Config;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * This class provides access to the MySQL database through JPA for the crawler.
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
 */
public class Connection {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static int lastError = 0;
    public static final int SQL_ERROR = 1;
    public static final int JPA_ERROR = 2;

    public static synchronized EntityManager getEntityManager() {
        return em;
    }

    /**
     * Tells the type of the last occurred error
     * @return 0 if there was no error, SQL_ERROR if there was an SQLException,
     * JPA_ERROR otherwise.
     */
    public static int getLastError() {
        return lastError;
    }

    /**
     * Tries to initialize connection. Calls init(), then stores the error code.
     * @return False on error, true on success.
     */
    public static boolean tryInit() {
        try {
            lastError = 0;
            init();
            return true;
        } catch (Exception e) {
            lastError = (e instanceof SQLException) ? SQL_ERROR : JPA_ERROR;
            return false;
        }
    }

    /**
     * Creates database in MySQL, builds up JPA connection, then imports PDatabase
     * definitions.
     */
    private static void init() throws SQLException {
        /*
         * We catch SQLException in the caller, because JPA exceptions cannot be
         * handled here. So both of them will be caught in the caller.
         */

        /*
         * Create database
         */
        java.sql.Connection c = DriverManager.getConnection("jdbc:mysql://" + Config.getJdbcUrl() + "/mysql", Config.getJdbcUser(), Config.getJdbcPass());
        Statement s = c.createStatement();
        s.execute("CREATE DATABASE IF NOT EXISTS pubsearch;");
        s.close();
        c.close();
        System.out.println("MYSQL CONNECTION OK.");

        /*
         * Connect JPA
         */
        Map props = new HashMap();
        props.put("javax.persistence.jdbc.url", "jdbc:mysql://" + Config.getJdbcUrl() + "/pubsearch");
        props.put("javax.persistence.jdbc.user", Config.getJdbcUser());
        props.put("javax.persistence.jdbc.password", Config.getJdbcPass());
        props.put("eclipselink.logging.level", "OFF");
        emf = Persistence.createEntityManagerFactory("pubsearch", props);
        em = emf.createEntityManager();
        System.out.println("JPA CONNECTION BUILT.");

        /*
         * Import PDatabase definitions
         */
        Importer.loadPDatabases();

        System.out.println("INIT DONE.");
    }
}
