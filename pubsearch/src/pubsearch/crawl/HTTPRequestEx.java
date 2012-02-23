package pubsearch.crawl;

import java.util.HashMap;
import java.util.Map;
import pubsearch.Config;

/**
 * Extends HTTPRequest to fit the programs requirements: retries download on error,
 * sets up random proxy for every try, and stores downloaded pages in a cache.
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
 */
public class HTTPRequestEx extends HTTPRequest {

    private static Map<String, String> cache = new HashMap<String, String>();

    /**
     * Sets up the HTTPRequestEx object.
     * @param url Request will be sent to this address.
     * @param queryString GET/POST parameters in querystring syntax.
     * @param method "GET" or "POST"
     */
    public HTTPRequestEx(String url, String queryString, String method) {
        super(url, queryString, method);
    }

    /**
     * Sets up the HTTPRequestEx object. Method will be GET.
     * @param url Request will be sent to this address.
     * @param queryString GET/POST parameters in querystring syntax.
     */
    public HTTPRequestEx(String url, String queryString) {
        super(url, queryString);
    }

    /**
     * Sets up the HTTPRequestEx object. Querystring will be empty and method will be GET.
     * @param url Request will be sent to this address.
     */
    public HTTPRequestEx(String url) {
        super(url);
    }

    /**
     * Calls submit(5).
     * @return True if succeded, false on error.
     */
    @Override
    public boolean submit() {
        return submit(5); // 5 retries
    }

    /**
     * Calls HTTPRequest's submit() but tries again if it fails. Downloaded pages
     * will be stored in the cache.
     * @param tries Count of tryings.
     * @return True if succeded, false on error.
     */
    public boolean submit(int tries) {
        String toCache = url + (!queryString.equals("") ? "?" + queryString : "");
        if (null != (html = getHTMLFromCache(toCache))) {
            //System.out.println("Used cache for " + url + "?" + queryString);
            return true;
        }

        boolean success = false;
        do {
            if (Thread.interrupted()) {
                break;
            }

            String proxy = Config.getRandomProxy();
            super.setProxy(proxy);

            success = super.submit();

            if (!success) {
                Config.delProxy(proxy);
            }

            tries--;
        } while (!success && tries > 0);

        if (success) {
            addToCache(toCache, html);
        } else {
            System.out.println("Download failed: " + toCache);
        }

        return success;
    }

    private static synchronized void addToCache(String url, String html) {
        cache.put(url, html);
    }

    private static synchronized String getHTMLFromCache(String url) {
        return cache.get(url);
    }
}
