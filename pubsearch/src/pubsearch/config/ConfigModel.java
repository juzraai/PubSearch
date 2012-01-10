package pubsearch.config;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Zsolt
 */
public class ConfigModel {

    private static final String CONFIG_FILE = "pubsearch.cfg";
    private static String jdbcUrl = "localhost:3306";
    private static String jdbcUser = "root";
    private static String jdbcPass = "root";
    private static String[] proxyList = new String[0];

    public static String getJdbcPass() {
        return jdbcPass;
    }

    public static void setJdbcPass(String jdbcPass) {
        ConfigModel.jdbcPass = jdbcPass;
    }

    public static String getJdbcUrl() {
        return jdbcUrl;
    }

    public static void setJdbcUrl(String jdbcUrl) {
        ConfigModel.jdbcUrl = jdbcUrl;
    }

    public static String getJdbcUser() {
        return jdbcUser;
    }

    public static void setJdbcUser(String jdbcUser) {
        ConfigModel.jdbcUser = jdbcUser;
    }

    public static String[] getProxyList() {
        return proxyList;
    }

    public static void setProxyList(String[] proxyList) {
        ConfigModel.proxyList = proxyList;
    }

    public static void load() {
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(CONFIG_FILE));
            setJdbcUrl(r.readLine());
            setJdbcUser(r.readLine());
            setJdbcPass(r.readLine());
            List<String> readProxyList = new ArrayList<String>();
            while (r.ready()) {
                readProxyList.add(r.readLine());
            }
            proxyList = new String[readProxyList.size()];
            proxyList = readProxyList.toArray(proxyList);
        } catch (IOException e) {
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
            System.out.println("CONFIG SAVED.");
            if (w != null) {
                try {
                    w.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
