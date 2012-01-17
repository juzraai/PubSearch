package pubsearch.crawl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import sun.misc.IOUtils;

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
    // out
    private String html;

    public HTTPRequest(String url) {
        this(url, null, null);
    }

    public HTTPRequest(String url, String queryString) {
        this(url, queryString, null);
    }

    public HTTPRequest(String url, String queryString, String method) {
        //System.out.println("URL = " + url + "?" + queryString);
        this.url = url;
        this.queryString = queryString;
        this.method = (null != method && method.toUpperCase().equals("POST")) ? "POST" : "GET";
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
        String[] p = proxyIPPORT.split(":");
        int port;
        try {
            port = Integer.parseInt(p[1]);
        } catch (NumberFormatException e) {
            port = 8080;
        }
        setProxy(p[0], port);
    }

    public void submit() {
        HttpClient client = new HttpClient();
        if (null != proxyIP) {
            client.getHostConfiguration().setProxy(proxyIP, proxyPort);
        }

        HttpMethodBase methodModel = buildMethod();
        try {
            int statusCode = client.executeMethod(methodModel);

            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + methodModel.getStatusLine());
            }

            //byte[] responseBody = methodModel.getResponseBody();              
            html = methodModel.getResponseBodyAsString();
            //html = html.replace('\n', ' '); // egysorossá tesszük, így működnek a regex.-eink
            //html = html.replace('\t', ' ');
            System.out.println("HTTPREQ: " + html.length());

        } catch (HttpException e) {
            System.err.println("Fatal protocol violation: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Fatal transport error: " + e.getMessage() + " (" + url + ")");
        } finally {
            methodModel.releaseConnection();
        }
    }

    public String getHtml() {
        return html;
    }

    private HttpMethodBase buildMethod() {
        HttpMethodBase m;
        if (method.equals("GET")) {
            m = new GetMethod(url + "?" + queryString);
        } else {
            m = new PostMethod(url);

            String[] params = queryString.split("&");
            for (String param : params) {
                String[] p = param.split("=");
                ((PostMethod) m).addParameter(p[0], p[1]);
            }
        }
        m.getParams().setParameter(HttpMethodParams.USER_AGENT, "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
        m.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
        return m;
    }

    public String getUrl() {
        return url;
    }
}
