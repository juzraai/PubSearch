package pubsearch.crawl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import pubsearch.Config;

/**
 *
 * @author Zsolt
 */
public class Request {

    private String html;
    private int bytes;

    // TODO még kitalálni a POST-os megoldást is!!! összehangolni a tervezést a ResultListPage-el!
    public Request(String url) {
        try {
            URL u = new URL(url);
            String[] proxy = Config.getRandomProxy().split(":");
            Proxy p = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy[0], Integer.parseInt(proxy[1])));

            URLConnection uc = u.openConnection(p);
            uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");

            System.out.println("Connecting to '" + url + "' through proxy " + proxy[0] + ":" + proxy[1]);
            uc.connect();

            BufferedReader r = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = r.readLine()) != null) {
                bytes += line.length();
                sb.append(line.trim());
                sb.append(" ");
            }
            html = sb.toString();
        } catch (Throwable e) {
        }
    }

    public int getBytes() {
        return bytes;
    }
    
    public String getHtml() {
        return html;
    }

    
    
    
}
