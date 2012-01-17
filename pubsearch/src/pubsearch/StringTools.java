package pubsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Segédfüggvények. Javarészt String függvények: keresés, formázás.
 *
 * @author Zsolt
 */
public class StringTools {

    /**
     * Megtisztít egy szöveget a HTML kódoktól, a többszörös szóközöket szimplára
     * cseréli, és a szöveg végéről levágja a whitespace-eket.
     *
     * @param s A bemeneti szöveg.
     * @return A megtisztított szöveg.
     */
    public static String clean(String s) {
        return s.trim().replaceAll("<.*?>", " ").replaceAll("  ", " ");
    }

    /**
     * Megkeresi a megadott reguláris kifejezésre illeszkedő szövegrészt,
     * és visszatér az 1-es számú csoporttal.
     * @param in Amiben keresni kell.
     * @param pattern A minta, reguláris kifejezés, mely tartalmaz legalább 1 csoportot.
     * @return Az illeszkedő szövegrészletből az 1-es csoport, vagy null, ha nincs találat.
     */
    /**
     * Megkeresi az illeszkedő szövegrészt és visszaaindja annak egy csoportját.
     * @param in Amiben keresni kell.
     * @param pattern A minta (reguláris kifejezés), amit keresni kell.
     * @param group A reguláris kifejezésben szereplő csoport száma, amire szükség van.
     * @return Az illesztett szöveg megadott csoportja.
     */
    public static String findFirstMatch(String in, String pattern, int group) {
        if (null == in || null == pattern || 0 == in.length() || 0 == pattern.length() || group < 0) {
            return null;
        }
        Pattern p = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher m = p.matcher(in);
        return (m.find()) ? m.group(group) : null;
    }

    /**
     * Megkeresi a szövegben egy adott minta összes előfordulását, és visszatér
     * azok megadott csoportjainak listájával.
     * @param in Amiben keresni kell.
     * @param pattern A minta (reguláris kifejezés), amit keresni kell.
     * @param group A reguláris kifejezésben szereplő csoport száma, amire szükség van.
     * @return Az illeszkedő szövegrészletek megfelelő csoportjai, listába szervezve.
     */
    public static List<String> findAllMatch(String in, String pattern, int group) {
        if (null == in || null == pattern || 0 == in.length() || 0 == pattern.length() || group < 0) {
            return null;
        }
        Pattern p = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher m = p.matcher(in);
        ArrayList<String> a = new ArrayList<String>();
        while (m.find()) {
            a.add(m.group(group));
        }
        return a;
    }

    /**
     * Egy bájtokban megadott adatmennyiséget konvertál szöveggé, úgy hogy közben
     * a mértékegységet is váltja B-tól GB-ig. Két tizedesjegyre kerekít.
     * @param bytes A formázandó adatmennyiség.
     * @return A megformázott adatmennyiség.
     */
    public static String formatDataSize(long bytes) {
        String[] units = new String[]{"B",
            "KB",
            "MB",
            "GB"};

        double b = bytes;
        int i = 0;
        while (b > 900 && i < units.length) {
            b /= 1024;
            i++;
        }

        String bs;
        if (b % 1 == 0) {
            // egész számról van szó, egészként írjuk ki (így nem lesz .0 a végén)
            bs = Integer.toString((int) b);
        } else {
            // tört számunk van, 2 tizedesre kerekítünk, majd levágjuk a fölös 0-kat a végéről
            bs = String.format("%.2f", b);
            while (bs.endsWith("0")) {
                bs = bs.substring(0, bs.length() - 1);
            }
        }
        return bs + " " + units[i];
    }

    /**
     * Megformáz egy nanoszekundumban megadott időt.
     *
     * @param time
     * @return Az idő szövegként: MM:SS.MLSC,MCSC
     */
    public static String formatNanoTime(long time, boolean showMicro, boolean showNano) {
        long nanosec = time % 1000;
        long microsec = (time / 1000) % 1000;
        long millisec = (time / 1000 / 1000) % 1000;
        long sec = (time / 1000 / 1000 / 1000) % 60;
        long min = (time / 1000 / 1000 / 1000 / 60);

        String r = lpad(Long.toString(min), 2, '0') + ":"
                + lpad(Long.toString(sec), 2, '0') + "."
                + lpad(Long.toString(millisec), 3, '0');
        if (showMicro) {
            r += "," + lpad(Long.toString(microsec), 3, '0');
        }
        if (showNano) {
            r += "'" + lpad(Long.toString(nanosec), 3, '0');
        }
        return r;
    }

    /**
     * Balról kiegészít egy szöveget a megadott hosszúságra, a megadott karakterrel.
     * Ha a szöveg hosszabb, akkor nem módosít rajta.
     * @param s A kiindulási szöveg.
     * @param n A célhossz.
     * @param c A kitöltő karakter.
     * @return A kiegészített szöveg.
     */
    public static String lpad(String s, int n, char c) {
        while (s.length() < n) {
            s = c + s;
        }
        return s;
    }
}
