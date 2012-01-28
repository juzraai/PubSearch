package pubsearch;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import pubsearch.crawl.HTTPRequest;

/**
 * Tool proxy lista beszerzéséhez.
 *
 * @author Zsolt
 */
public class GetProxyList {

    public static void main(String[] args) {
        List<String> proxies = getProxyList();
        for (String p : proxies) {
            System.out.println(p);
        }
    }

    public static List<String> getProxyList() {
        System.out.println("Downloading proxy list...");
        String url = "http://www.ip-adress.com/proxy_list/?k=time&d=desc";
        HTTPRequest r = new HTTPRequest(url);
        if (r.submit()) {
            String html = r.getHtml();
            List<String> plist = StringTools.findAllMatch(html, "(([0-9]{1,3}\\.){3}[0-9]{1,3}:[0-9]{2,4})", 1);
            return new LinkedList<String>(new HashSet<String>(plist));
        }
        return new LinkedList<String>();
    }
}
