package pubsearch.crawl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
    private String html;
    private long bytes;
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

        this.queryString = this.queryString.replaceAll(" ", "%20").replaceAll("\"", "%22");//.replaceAll("\\+", "%2b");
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
        int port;
        try {
            port = Integer.parseInt(p[1]);
        } catch (NumberFormatException e) {
            port = 8080;
        }
        setProxy(p[0], port);
    }

    public boolean submit() {
        boolean success = false;
        error = "";

        if (null != proxyIP) {
            client.getHostConfiguration().setProxy(proxyIP, proxyPort);
        }

        HttpMethodBase methodModel = buildMethod();
        try {
            int statusCode = client.executeMethod(methodModel);

            if (statusCode != HttpStatus.SC_OK) {
                throw new Exception("Method failed: " + methodModel.getStatusLine());
            }

            // TODO should detect if content-length header is present or not - if not, throw new Exception("Unknown content length.")

            InputStream instream = methodModel.getResponseBodyAsStream();
            StringBuilder sb = new StringBuilder();
            byte[] buffer = new byte[4096];
            int len;
            while ((len = instream.read(buffer)) > 0) {
                String s = new String(buffer, 0, len);
                sb.append(s);
            }
            html = sb.toString();
            bytes += html.length();

            if (null == StringTools.findFirstMatch(html, "<.*?>", 0)) { // a bit buggy detection :-)
                System.err.println("not a HTML");
                // <debug html output>
                try {
                    BufferedWriter w = new BufferedWriter(new FileWriter(System.currentTimeMillis() + ".html"));
                    w.write(html);
                    w.close();
                } catch (IOException e) {
                }
                // </debug html output>
                throw new Exception("Not a HTML file.");
            }

            success = true;
            /*
             * } catch (HttpException e) {
             * //System.err.println("Fatal protocol violation: " + e.getMessage());
             * } catch (IOException e) {
             * //System.err.println("Fatal transport error: " + e.getMessage() + " (" + url + ")");
             */
        } catch (Exception e) {
            error = e.getMessage();
            //System.err.println("HTTP Status failure");
        } finally {
            methodModel.releaseConnection();
            return success;
        }
    }

    public long getBytes() {
        return bytes;
    }

    public String getHtml() {
        return html;
    }

    private HttpMethodBase buildMethod() { // TODO TRY parse helyett setQueryString POSTMETHOD-nál is! :)
        HttpMethodBase m;
        if (method.equals("GET")) {
            String u = url;
            if (0 < queryString.length()) {
                if (url.contains("?")) {
                    u = url + "&" + queryString;
                } else {
                    u = url + "?" + queryString;
                }
            }
            m = new GetMethod(u);
        } else {
            m = new PostMethod(url);

            String[] params = queryString.split("&");
            for (String param : params) {
                String[] p = param.split("=", 2);
                if (2 == p.length) {
                    ((PostMethod) m).addParameter(p[0], p[1]);
                }
            }
        }

        m.getParams().setParameter(HttpMethodParams.USER_AGENT, "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
        m.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
        m.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 10 * 1000);
        return m;
    }
}
