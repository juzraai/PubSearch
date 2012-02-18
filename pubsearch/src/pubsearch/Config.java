package pubsearch;

import java.io.*;
import java.util.*;

/**
 * This class handles the configuration.
 * Contains static methods for getting/setting, loading/saving MySQL connection
 * parameters and the proxy list.
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
 */
public class Config {

    private static final String CONF_DIR = "conf";
    private static final String MYSQL_FILE = CONF_DIR + "/mysql.cfg";
    private static final String PROXY_FILE = CONF_DIR + "/proxy.lst";
    private static String jdbcUrl = "localhost:3306";
    private static String jdbcUser = "root";
    private static String jdbcPass = "";
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

    /**
     * Sets the proxy list. Deletes duplications.
     * @param proxyList The new proxy list.
     */
    public static void setProxyList(List<String> proxyList) {
        Config.proxyList = new LinkedList<String>(new HashSet<String>(proxyList));
    }

    /**
     * Sets the proxy list. Deletes duplications.
     * @param proxyList The new proxy list.
     */
    public static void setProxyList(String[] proxies) {
        List<String> pl = new ArrayList<String>();
        Collections.addAll(pl, proxies);
        setProxyList(pl);
    }

    /**
     * Returns a random proxy from the proxy list.
     * If the list is empty, tries to download a new one.
     * @return A random choosed proxy string or null if download failed.
     */
    public static synchronized String getRandomProxy() {
        if (proxyList.isEmpty()) {
            setProxyList(GetProxyList.getProxyList());
        }
        if (proxyList.isEmpty()) {
            return proxyList.get((int) (Math.random() * proxyList.size()));
        } else {
            return null; // TODO TEST !
        }
    }

    /**
     * Removes a proxy from the list.
     * Proxy list file won't be updated.
     * @param proxy Proxy to remove.
     */
    public static synchronized void delProxy(String proxy) {
        if (proxyList.remove(proxy)) {
            //System.out.println("Proxy removed: " + proxy);
        }
    }

    /**
     * Loads MySQL connection parameters from the config file.
     */
    public static void loadMySQLConfig() {
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(MYSQL_FILE));
            setJdbcUrl(r.readLine());
            setJdbcUser(r.readLine());
            setJdbcPass(r.readLine());
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
     * Saves MySQL connection parameters to the config file.
     * If configuration directory doesn't exist, it will be created.
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
            if (null != w) {
                try {
                    w.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Loads proxy list from the proxy list file.
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
     * Saves the proxy list to the proxy list file.
     * If configuration directory doesn't exist, it will be created.
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
            if (null != w) {
                try {
                    w.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
