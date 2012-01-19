package pubsearch;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Zsolt
 */
public class Config {

    private static final String CONFIG_FILE = "pubsearch.cfg";
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
        Config.proxyList = proxyList;
    }

    public static void setProxyList(String[] proxyList) {
        Config.proxyList.clear();
        Collections.addAll(Config.proxyList, proxyList);
    }

    public static String getRandomProxy() {
        return proxyList.get((int) (Math.random() * proxyList.size()));
    }

    public static void load() {
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(CONFIG_FILE));
            setJdbcUrl(r.readLine());
            setJdbcUser(r.readLine());
            setJdbcPass(r.readLine());
            proxyList.clear();
            while (r.ready()) {
                proxyList.add(r.readLine());
            }
        } catch (IOException e) {
            System.out.println("ERROR WHILE LOADING CONFIG.");
        } finally {
            System.out.println("CONFIG LOADED.");
            if (r != null) {
                try {
                    r.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void save() {
        BufferedWriter w = null;
        try {
            w = new BufferedWriter(new FileWriter(CONFIG_FILE));
            w.write(jdbcUrl);
            w.newLine();
            w.write(jdbcUser);
            w.newLine();
            w.write(jdbcPass);
            w.newLine();
            for (String proxy : proxyList) {
                w.write(proxy);
                w.newLine();
            }
        } catch (IOException e) {
        } finally {
            //System.out.println("CONFIG SAVED.");
            if (w != null) {
                try {
                    w.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Törli a megadott proxy-t a listáról, és a változást rögzíti a konfig. fájlban is.
     * Ez a metódus érvénytelen proxy esetén (hibás kapcsolódás) hívódik meg.
     * @param proxy A törlendő proxy (IP:PORT).
     */
    public static void delProxy(String proxy) {
        if (proxyList.remove(proxy)) {
            System.out.println("Proxy " + proxy + " removed from the list.");
        }
        save();
    }
}
