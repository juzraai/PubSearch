package pubsearch.crawl;

import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import pubsearch.StringTools;

/**
 * HTTP kérést végrehajtó osztály. Használható egy oldal letöltésére, GET/POST
 * form elküldésére. Az Apache HTTPClient 3.0.1-re épül.
 *
 * @author Zsolt
 */
public class HTTPRequest {

    // in
    private String url;
    private String queryString;
    private String method;
    private String proxyIP;
    private int proxyPort;
    //inside
    private static HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
    // out
    private static long bytes;
    private String html;
    protected String error;

    public HTTPRequest(String url) {
        this(url, null, null);
    }

    public HTTPRequest(String url, String queryString) {
        this(url, queryString, null);
    }

    public HTTPRequest(String url, String queryString, String method) {
        this.url = (null != url) ? url : "";
        this.queryString = (null != queryString) ? queryString : "";
        this.method = (null != method && method.toUpperCase().equals("POST")) ? "POST" : "GET";
        this.queryString = this.queryString.replaceAll(" ", "%20").replaceAll("\"", "%22");

        if (this.url.contains("?")) {
            String[] urlParts = this.url.split("\\?");
            this.url = urlParts[0];
            this.queryString = urlParts[1] + "&" + this.queryString;
        }
    }

    /**
     * Beállítja a proxy-t a kéréshez.
     * @param proxyIP A proxyszerver IP címe.
     * @param proxyPort A proxyszerver portja.
     */
    public void setProxy(String proxyIP, int proxyPort) {
        this.proxyIP = proxyIP;
        this.proxyPort = proxyPort;
    }

    /**
     * Beállítja a proxy-t a kéréshez, egy IP:PORT formátúmú string-ből.
     * Ha a port nincs benne a szövegben, alapértelmezésként 8080-ra állítja.
     * @param proxyIPPORT IP:PORT formátumú string, pl. "127.0.0.1:8080"
     */
    public void setProxy(String proxyIPPORT) {
        String[] p = proxyIPPORT.split(":", 2);
        if (2 != p.length) {
            return;
        }
        int port = 8080;
        try {
            port = Integer.parseInt(p[1]);
        } catch (NumberFormatException e) {
        }
        setProxy(p[0], port);
    }

    /**
     * Elküldi a beállított kérést, majd letölti a válasz HTML oldalt. A méretét
     * hozzáadja a statikus bájtszámlálóhoz. Ha a művelet nem volt sikeres, a
     * hibaüzenetet eltárolja.
     * @return Sikerült-e HTML oldalt visszakapni.
     */
    public boolean submit() {
        boolean success = false;
        error = "";

        if (null != proxyIP) {
            client.getHostConfiguration().setProxy(proxyIP, proxyPort);
        }

        HttpMethodBase methodModel = buildMethod();
        try {
            if (client.executeMethod(methodModel) != HttpStatus.SC_OK) {
                throw new Exception("Method failed: " + methodModel.getStatusLine());
            }

            // TODO should detect if content-length header is present or not - if not, throw new Exception("Unknown content length.")
            InputStream instream = methodModel.getResponseBodyAsStream();
            byte[] buffer = new byte[4096];
            byte[] htmlBytes = new byte[0];
            int len;
            while ((len = instream.read(buffer)) > 0) {
                byte[] b = new byte[htmlBytes.length + len];
                System.arraycopy(htmlBytes, 0, b, 0, htmlBytes.length);
                System.arraycopy(buffer, 0, b, htmlBytes.length, len);
                htmlBytes = b;
            }
            html = new String(htmlBytes, Charset.forName("UTF-8"));
            bytes += htmlBytes.length;

            if (null == StringTools.findFirstMatch(html, "<.*?>", 0)) { // a bit buggy detection :-)
                System.err.println("not a HTML");
                throw new Exception("Not a HTML file.");
            }

            success = true;
        } catch (Exception e) {
            error = e.getMessage();
        } finally {
            methodModel.releaseConnection();
            return success;
        }
    }

    /**
     * Szinkronizáltan növeli a statikus bájtszámláló értékét.
     * @param b Hozzáadandó érték.
     */
    private static synchronized void addBytes(long b) {
        bytes += b;
    }

    /**
     * @return A statikus bájtszámláló értéke.
     */
    public static long getBytes() {
        return bytes;
    }

    /**
     * Lenullázza a statikus bájtszámlálót.
     */
    public static void zeroBytes() {
        bytes = 0;
    }

    /**
     * @return A letöltött válasz HTML oldal.
     */
    public String getHtml() {
        return html;
    }

    /**
     * A konstruktorban kapott paraméterek alapján felépíti a kérés modelljét.
     * @return A kérés modellje.
     */
    private HttpMethodBase buildMethod() { //TODO try setQueryString with POST ! :)
        HttpMethodBase m = (method.equals("POST")) ? new PostMethod(url) : new GetMethod(url);
        m.setQueryString(queryString);
        m.getParams().setParameter(HttpMethodParams.USER_AGENT, "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
        m.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
        m.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 10 * 1000);
        return m;
    }
}
