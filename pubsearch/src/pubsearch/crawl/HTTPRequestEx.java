package pubsearch.crawl;

import pubsearch.Config;

/**
 * HTTPRequest osztály kiegészítése a program követelményeihez.
 * Beállítja a proxy-t, és az újrapróbálkozásokat is egy véletlenszerű proxy-n
 * keresztül végzi.
 *
 * @author Zsolt
 */
public class HTTPRequestEx extends HTTPRequest {

    public HTTPRequestEx(String url, String queryString, String method) {
        super(url, queryString, method);
    }

    public HTTPRequestEx(String url, String queryString) {
        super(url, queryString);
    }

    public HTTPRequestEx(String url) {
        super(url);
    }

    public boolean submit(int tries) {
        boolean success;
        do {
            String proxy = Config.getRandomProxy();
            super.setProxy(proxy);

            success = super.submit();

            if (!success) {
                Config.delProxy(proxy);
            }

            tries--;
        } while (!success && tries > 0);
        return success;
    }
}
