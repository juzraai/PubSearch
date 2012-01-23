package pubsearch;

import java.io.*;
import java.util.*;

/**
 *
 * @author Zsolt
 */
public class Config {

    private static final String CONF_DIR = "conf";
    private static final String MYSQL_FILE = CONF_DIR + "/mysql.cfg";
    private static final String PROXY_FILE = CONF_DIR + "/proxy.lst";
    private static String jdbcUrl = "localhost:3306";
    private static String jdbcUser = "root";
    private static String jdbcPass = "root";
    private static List<String> proxyList = new ArrayList<String>();

    public static String getJdbcPass() {
        return jdbcPass;
    }

    public static void setJdbcPass(String jdbcPass) {
        Config.jdbcPass = jdbcPass;
    }

    public static String getJdbcUrl() {
        return jdbcUrl;
    }

    public static void setJdbcUrl(String jdbcUrl) {
        Config.jdbcUrl = jdbcUrl;
    }

    public static String getJdbcUser() {
        return jdbcUser;
    }

    public static void setJdbcUser(String jdbcUser) {
        Config.jdbcUser = jdbcUser;
    }

    public static List<String> getProxyList() {
        return proxyList;
    }

    public static void setProxyList(List<String> proxyList) {
        Config.proxyList = new LinkedList<String>(new HashSet<String>(proxyList));
        // a Set-tel kiszűrjük a duplikátokat.
    }

    public static void setProxyList(String[] proxies) {
        List<String> proxyList = new ArrayList<String>();
        Collections.addAll(proxyList, proxies);
        setProxyList(proxyList); // meghívjuk a List-es verziót, az kiszűri a duplikátokat
    }

    public static String getRandomProxy() {
        return proxyList.get((int) (Math.random() * proxyList.size()));
    }


    /**
     * Törli a megadott proxy-t a listáról, és a változást rögzíti a konfig. fájlban is.
     * Ez a metódus érvénytelen proxy esetén (hibás kapcsolódás) hívódik meg.
     * @param proxy A törlendő proxy (IP:PORT).
     */
    public static void delProxy(String proxy) {
        if (proxyList.remove(proxy)) {
            System.err.println("Proxy " + proxy + " removed from the list.");
        }
        saveProxyList();
    }

    /**
     * Betölti a MySQL kapcsolódási beállításokat.
     */
    public static void loadMySQLConfig() {
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(MYSQL_FILE));
            setJdbcUrl(r.readLine());
            setJdbcUser(r.readLine());
            setJdbcPass(r.readLine());
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } finally {
            if (null != r) {
                try {
                    r.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Kimenti a MySQL kapcsolódási beállításokat.
     */
    public static void saveMySQLConfig() {
        new File(CONF_DIR).mkdir();

        BufferedWriter w = null;
        try {
            w = new BufferedWriter(new FileWriter(MYSQL_FILE));
            w.write(getJdbcUrl());
            w.newLine();
            w.write(getJdbcUser());
            w.newLine();
            w.write(getJdbcPass());
            w.newLine();
        } catch (IOException e) {
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Betölti a proxy listát.
     */
    public static void loadProxyList() {
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(PROXY_FILE));
            List<String> proxies = new ArrayList<String>();
            while (r.ready()) {
                proxies.add(r.readLine());
            }
            setProxyList(proxies);
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } finally {
            if (null != r) {
                try {
                    r.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Kimenti a proxy listát.
     */
    public static void saveProxyList() {
        new File(CONF_DIR).mkdir();

        BufferedWriter w = null;
        try {
            w = new BufferedWriter(new FileWriter(PROXY_FILE));
            for (String p : getProxyList()) {
                w.write(p);
                w.newLine();
            }
        } catch (IOException e) {
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
