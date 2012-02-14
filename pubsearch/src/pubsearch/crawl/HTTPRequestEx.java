package pubsearch.crawl;

import java.util.HashMap;
import java.util.Map;
import pubsearch.Config;
import pubsearch.StringTools;

/**
 * HTTPRequest osztály kiegészítése a program követelményeihez.
 * Beállítja a proxy-t, és az újrapróbálkozásokat is egy véletlenszerű proxy-n
 * keresztül végzi. Emellett egy cache-ben tárolja az (URL,HTML) párokat, hogy
 * egy oldalt csak egyszer töltsön le.
 *
 * @author Zsolt
 */
public class HTTPRequestEx extends HTTPRequest {

    private static Map<String, String> cache = new HashMap<String, String>();

    public HTTPRequestEx(String url, String queryString, String method) {
        super(url, queryString, method);
    }

    public HTTPRequestEx(String url, String queryString) {
        super(url, queryString);
    }

    public HTTPRequestEx(String url) {
        super(url);
    }

    /**
     * Meghívja a submit(5)-öt.
     * @return Sikerült-e HTML oldalt visszakapni.
     */
    @Override
    public boolean submit() {
        return submit(5); // 5 retries
    }

    /**
     * Elküldi a beállított kérést, majd letölti a válasz HTML oldalt és növeli
     * a statikus bájtszámlálót. Újrapróbálkozik, mindig más, proxy-val, melyet
     * véletlenszerűen választ a listából. Ha egy proxy-n keresztül nem sikerült
     * csatlakozni, azt törli a listából. Ezen felül a már letöltött oldalakat
     * elmenti, így egy későbbi kérésnél már a memóriából olvassa ki ismételt
     * letöltés helyett.
     * @param tries Újrapróbálkozások száma.
     * @return Sikerült-e HTML oldalt visszakapni.
     */
    public boolean submit(int tries) {
        String toCache = url + (!queryString.equals("") ? "?" + queryString : "");
        if (null != (html = getHTMLFromCache(toCache))) {
            //System.out.println("Used cache for " + url + "?" + queryString);
            return true;
        }

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

        if (success) {
            addToCache(toCache, html);
        } else {
            System.out.println("Download failed: " + toCache);
        }

        return success;
    }

    private static synchronized String getHTMLFromCache(String url) {
        return cache.get(url);
    }

    private static synchronized void addToCache(String url, String html) {
        cache.put(url, html);
    }
}
