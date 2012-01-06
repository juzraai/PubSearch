package pubsearch.data;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Az adatbáziskapcsolatot innen érik el az osztályok. Jelen esetben a JPA használatához szükséges EntityManager objektumot.
 *
 * @author Zsolt
 */
public class Connection {

    private static EntityManagerFactory emf = null;
    public static EntityManager em = null;
    public static String dburl = "localhost:3306";
    public static String username = "root";
    public static String password = "root";

    /**
     * Létrehozza az adatbázist (ha még nem létezik), valamint a kapcsolatot
     * a JPA-val (ami pedig csatlakozik az adatbázishoz).
     */
    public static void init() throws SQLException {
        /*
         * Az SQLException-t azért kívül kezeljük le, mert a JPA kivételeit
         * csak kívül tudjuk lekezelni, és így együtt kezeljük le mindkettőt.
         */

        /*
         * Létrehozzuk az adatbázist, ha még nincs (egyúttal teszteljük a konfigot)
         */
        java.sql.Connection c = DriverManager.getConnection("jdbc:mysql://" + dburl + "/mysql", username, password);
        Statement s = c.createStatement();
        s.execute("CREATE DATABASE IF NOT EXISTS pubsearch;");
        s.close();
        c.close();

        /*
         * Csatalakoztatjuk a JPA-t.
         */
        Map props = new HashMap();
        props.put("javax.persistence.jdbc.url", "jdbc:mysql://" + dburl + "/pubsearch");
        props.put("javax.persistence.jdbc.user", username);
        props.put("javax.persistence.jdbc.password", password);
        emf = Persistence.createEntityManagerFactory("pubsearch", props);
        em = emf.createEntityManager();

        System.out.println("[pubsearch.data.Connection] Init done.");
    }
}
