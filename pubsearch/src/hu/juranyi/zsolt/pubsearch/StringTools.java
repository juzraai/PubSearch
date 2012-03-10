package hu.juranyi.zsolt.pubsearch;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tools for advanced string handling.
 * It contains regular expression related methods, cleaners and formatters.
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
 */
public class StringTools {

    /**
     * Cuts out HTML tags, reduces repeating spaces, and trims the input string.
     *
     * @param s Input text.
     * @return Cleaned text.
     */
    public static String clean(String s) {
        return (null != s) ? s.replaceAll("<.*?>", " ").replaceAll("&nbsp;", " ").replaceAll(" +", " ").trim() : null;
    }

    /**
     * Finds the first matching part of a pattern in a string and returns the
     * specified group.
     * @param in Input text.
     * @param pattern Pattern.
     * @param group Needed group.
     * @return Specified group of the matching part or null if there's no match or
     * if one of the first two parameters is null or empty, or group is negative.
     */
    public static String findFirstMatch(String in, String pattern, int group) {
        if (null == in || null == pattern || 0 == in.length() || 0 == pattern.length() || group < 0) {
            return null;
        }
        Pattern p = Pattern.compile(pattern, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(in);
        return (m.find()) ? m.group(group) : null;
    }

    /**
     * Finds all matching part of a pattern in a string, then returns their one
     * group in a list.
     * @param in Input text.
     * @param pattern Pattern.
     * @param group Needed group.
     * @return List of the matching parts specified group or null if one of the
     * first two parameters is null or empty, or group is negative.
     */
    public static List<String> findAllMatch(String in, String pattern, int group) {
        if (null == in || null == pattern || 0 == in.length() || 0 == pattern.length() || group < 0) {
            return null;
        }
        Pattern p = Pattern.compile(pattern, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(in);
        ArrayList<String> a = new ArrayList<String>();
        while (m.find()) {
            a.add(m.group(group));
        }
        return a;
    }

    /**
     * Transforms a data size in bytes to a readable text which includes the unit.
     * Uses 1024 for division, divides when value is above 900 and rounds to 2 digits.
     * <br />For example:
     * <ul><li>300 will be "300 B"</li>
     * <li>1024 will be "1 KB"</li>
     * <li>1250 will be "1,22 KB"</li>
     * <li>1535450808 will be "1,43 GB"<li></ul>
     * @param bytes Number to format.
     * @return Formatted number.
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
        bs = String.format("%.2f", b);
        while (bs.endsWith("0")) {
            bs = bs.substring(0, bs.length() - 1);
        }
        if (bs.endsWith(Character.toString(new DecimalFormat().getDecimalFormatSymbols().getDecimalSeparator()))) {
            bs = bs.substring(0, bs.length() - 1);
        }
        return bs + " " + units[i];
    }

    /**
     * Transforms a time in nanoseconsds to a readable text.
     * <br />Output format: MM:SS.MS,mS'nS
     * <br />1 second = 1000 millisecond = 1000*1000 microsecond = 1000*1000*1000 nanosecond
     * @param time Time to format.
     * @param showMicro Include microseconds in the text?
     * @param showNano Include nanoseconds in the text?
     * @return
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
        if (showMicro && showNano) {
            r += "'" + lpad(Long.toString(nanosec), 3, '0');
        }
        return r;
    }

    /**
     * Pads a text from left to the specified length with the specified character.
     * If input length > needed length, it returns the input text.
     * @param s Input text.
     * @param n Needed length.
     * @param c Fill character.
     * @return Padded text.
     */
    public static String lpad(String s, int n, char c) {
        while (s.length() < n) {
            s = c + s;
        }
        return s;
    }

    /**
     * Pads a text from right to the specified length with the specified character.
     * If input length > needed length, it returns the input text.
     * @param s Input text.
     * @param n Needed length.
     * @param c Fill character.
     * @return Padded text.
     */
    public static String rpad(String s, int n, char c) {
        while (s.length() < n) {
            s += c;
        }
        return s;
    }
}
