package pubsearch.crawl;

import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 * Executes a HTTP request and downloads the response.
 * Can be used to download a page or post a GET/POST form.
 * Uses Apache HTTPClient 3.0.1
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
 */
public class HTTPRequest {

    // in
    protected String url;
    protected String queryString;
    private String method;
    private String proxyIP;
    private int proxyPort;
    //inside
    private static HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
    // out
    private static long bytes;
    protected String html;
    protected String error;

    /**
     * Sets up the HTTPRequest object.
     * @param url Request will be sent to this address.
     * @param queryString GET/POST parameters in querystring syntax.
     * @param method "GET" or "POST"
     */
    public HTTPRequest(String url, String queryString, String method) {
        this.url = (null != url) ? url : "";
        this.queryString = (null != queryString) ? queryString : "";
        this.method = (null != method && method.toUpperCase().equals("POST")) ? "POST" : "GET";

        if (this.url.contains("?")) {
            String[] urlParts = this.url.split("\\?");
            this.url = urlParts[0];
            this.queryString = urlParts[1] + "&" + this.queryString;
        }
        this.queryString = this.queryString.replaceAll(" ", "%20").replaceAll("\"", "%22");
    }

    /**
     * Sets up the HTTPRequest object. Method will be GET.
     * @param url Request will be sent to this address.
     * @param queryString GET/POST parameters in querystring syntax.
     */
    public HTTPRequest(String url, String queryString) {
        this(url, queryString, null);
    }

    /**
     * Sets up the HTTPRequest object. Querystring will be empty and method will be GET.
     * @param url Request will be sent to this address.
     */
    public HTTPRequest(String url) {
        this(url, null, null);
    }

    /**
     * Sets up the proxy used by the client.
     * @param proxyIP Proxy IP.
     * @param proxyPort Proxy port.
     */
    public void setProxy(String proxyIP, int proxyPort) {
        this.proxyIP = proxyIP;
        this.proxyPort = proxyPort;
    }

    /**
     * Sets up the proxy used by the client.
     * @param proxyIPPORT Proxy IP and port, format: "IP:PORT", for example: "127.0.0.1:8080".
     * If port is not included, sets to 8080 by default.
     */
    public void setProxy(String proxyIPPORT) {
        if (null == proxyIPPORT) {
            proxyIP = null;
        } else {
            String[] p = proxyIPPORT.split(":", 2);
            if (2 != p.length) {
                return;
            }
            int port = 8080;
            try {
                port = Integer.parseInt(p[1]);
            } finally {
                setProxy(p[0], port);
            }
        }
    }

    /**
     * Sends the request then downloads HTML response page. Size will be added to
     * the static byte counter. If the process fails, stores the error message in
     * 'error' field.
     * @return True if succeded, false on error.
     */
    public boolean submit() {
        boolean success = false;
        error = "";

        if (null != proxyIP) {
            client.getHostConfiguration().setProxy(proxyIP, proxyPort);
        }
        client.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);


        HttpMethodBase methodModel = buildMethod();

        int timeout = 20 * 1000;
        client.getParams().setParameter("http.socket.timeout", timeout);
        client.getParams().setParameter("http.connection.timeout", timeout);

        try {
            if (client.executeMethod(methodModel) != HttpStatus.SC_OK) {
                throw new Exception("Method failed: " + methodModel.getStatusLine());
            }

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
            addBytes(htmlBytes.length);

            success = true;

        } catch (Exception e) {
            error = e.getMessage();
        } finally {
            methodModel.releaseConnection();
            return success;
        }
    }

    /**
     * Increases the static byte counter by the given value.
     * @param b Value to be added.
     */
    private static synchronized void addBytes(long b) {
        bytes += b;
    }

    public static synchronized long getBytes() {
        return bytes;
    }

    /**
     * Sets the static byte counter to 0.
     */
    public static void zeroBytes() {
        bytes = 0;
    }

    public String getHtml() {
        return html;
    }

    /**
     * Sets up the request model.
     * @return The request model.
     */
    private HttpMethodBase buildMethod() {
        HttpMethodBase m = (method.equals("POST")) ? new PostMethod(url) : new GetMethod(url);
        m.setQueryString(queryString);
        m.getParams().setParameter(HttpMethodParams.USER_AGENT, "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
        m.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
        m.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 10 * 1000);
        m.setFollowRedirects(false);
        return m;
    }
}
