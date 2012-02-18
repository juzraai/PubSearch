package pubsearch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import pubsearch.crawl.HTTPRequest;

/**
 * Tool for downloading a proxy list.
 * It has main() method, so it can be runned as a standalone program.
 * It downloads the following pages, and extracts the IPs and ports:
 * <ul><li>http://www.ip-adress.com/proxy_list/?k=type</li>
 * <li>http://www.xroxy.com/proxylist.php?port=&type=&ssl=&country=HU&latency=1000&reliability=9000</li></ul>
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
 */
public class GetProxyList {

    /**
     * Calls getProxyList() method then prints out the list to the console.
     *
     * @param args Command line parameters - has no effect.
     */
    public static void main(String[] args) {
        List<String> proxies = getProxyList();
        for (String p : proxies) {
            System.out.println(p);
        }
    }

    private GetProxyList() {
    }

    /**
     * Grabs 2 webpage providing a mostly reliable proxy list, then extracts
     * IP:PORT strings and returns them in a list.
     * @return The grabbed IP:PORT list.
     */
    public static List<String> getProxyList() {
        List<String> pl = new ArrayList<String>();
        pl.addAll(getFromXROXYdotCOM());
        pl.addAll(getFromIPADRESSdotCOM());
        System.out.println("Proxy lists downloaded.");
        return pl;
    }

    private static List<String> getFromIPADRESSdotCOM() {
        String url = "http://www.ip-adress.com/proxy_list/?k=type";
        HTTPRequest r = new HTTPRequest(url);
        boolean success = false;
        int retry = 1;
        while (!success && retry < 3) {
            success = r.submit();
            retry++;
        }
        if (r.submit()) {
            String html = r.getHtml();
            List<String> plist = StringTools.findAllMatch(html, "(([0-9]{1,3}\\.){3}[0-9]{1,3}:[0-9]{2,4})", 1);
            return new LinkedList<String>(new HashSet<String>(plist));
        }
        return new LinkedList<String>();
    }

    private static List<String> getFromXROXYdotCOM() {
        String url = "http://www.xroxy.com/proxylist.php?port=&type=&ssl=&country=HU&latency=1000&reliability=9000#table";
        HTTPRequest r = new HTTPRequest(url);
        boolean success = false;
        int retry = 1;
        while (!success && retry < 3) {
            success = r.submit();
            retry++;
        }
        if (r.submit()) {
            String html = r.getHtml();
            List<String> plist = StringTools.findAllMatch(html, "(<tr class=.?row.*?</tr>)", 1);
            for (int i = 0; i < plist.size(); i++) {
                String s = plist.get(i);
                String ip = StringTools.findFirstMatch(s, "<td>.*?details.*?((?:[0-9]{1,3}\\.){3}[0-9]{1,3}).*?</td>", 1);
                String port = StringTools.findFirstMatch(s, "<td>.*?port.*?([0-9]{2,4}).*?</td>", 1);
                s = ip + ":" + port;
                plist.set(i, s);
            }
            return new LinkedList<String>(new HashSet<String>(plist));
        }
        return new LinkedList<String>();
    }
}
