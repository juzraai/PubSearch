package pubsearch.crawl;

import pubsearch.Config;
import pubsearch.StringTools;

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

    /**
     * Elküldi a beállított kérést, majd letölti a válasz HTML oldalt és növeli
     * a statikus bájtszámlálót. 5-ször újrapróbálkozik, mindig más, proxy-val,
     * melyet véletlenszerűen választ a listából. Ha egy proxy-n keresztül nem
     * sikerült csatlakozni, azt törli a listából.
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
     * csatlakozni, azt törli a listából.
     * @param tries Újrapróbálkozások száma.
     * @return Sikerült-e HTML oldalt visszakapni.
     */
    public boolean submit(int tries) {
        boolean success;
        do {
            String proxy = Config.getRandomProxy();
            super.setProxy(proxy);

            success = super.submit();

            if (!success) {
                if (null != StringTools.findFirstMatch(error, "Circular", 0)) {
                    // ACM fix: ha nincs találat, akkor circular redirect-be megy,
                    // ez nem a proxy hibája, ezért ezesetben nem töröljük.
                    System.err.println("Circular redirect detected.");
                    return false;
                } else {
                    Config.delProxy(proxy);
                }
            }

            tries--;
        } while (!success && tries > 0);
        return success;
    }
}
