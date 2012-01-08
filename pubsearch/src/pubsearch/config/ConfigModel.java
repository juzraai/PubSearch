package pubsearch.config;

/**
 *
 * @author Zsolt
 */
public class ConfigModel {
    
    private static final String CONFIG_FILE = "pubsearch.cfg";
    private static String jdbcUrl = "localhost:3306";
    private static String jdbcUser = "root";
    private static String jdbcPass = "root";
    private static String[] proxyList;

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
        // TODO config beolvasása
    }
    
    public static void save() {
        // TODO config mentése
    }
    
}
