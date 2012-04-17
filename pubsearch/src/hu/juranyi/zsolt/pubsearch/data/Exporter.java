package hu.juranyi.zsolt.pubsearch.data;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * Tool for exporting citation from a Publication object. First it maps the
 * 'formats' directory for *.vm files and stores them in a global list. GUI
 * can query the formatList to show in a combobox.
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
 */
public class Exporter {

    private static final List<String> vmFiles = new ArrayList<String>();
    private static final List<String> formatList = new ArrayList<String>();
    private final VelocityContext context = new VelocityContext();

    static {
        // get *.vm files list

        File confDir = new File("formats");
        String[] confFiles = confDir.list();
        if (null != confFiles) {
            for (String f : confFiles) {
                if (f.endsWith(".vm")) {
                    vmFiles.add("formats" + File.separator + f);
                }
            }
        }

        // fill list (filenames without extensions)
        for (String t : vmFiles) {
            t = t.substring("formats".length() + 1);
            t = t.substring(0, t.length() - 3);
            formatList.add(t);
        }
    }

    /**
     * Builds up the citation context (a VelocityContext object) from the publication
     * data.
     * @param p Publication to be exported.
     */
    public Exporter(Publication p) {
        context.put("authors", p.getAuthors());
        context.put("title", p.getTitle());
        if (-1 < p.getYear()) {
            context.put("year", p.getYear());
        }
        if (null != p.getUrl()) {
            context.put("url", p.getUrl());
        }
    }

    /**
     * Merges the specified format template with the context.
     * @param formatIndex The index of the format (the template) in the global list.
     * @return The exported citation.
     */
    public String export(int formatIndex) {
        String export = "";
        StringWriter sw = new StringWriter();
        try {
            Template template = Velocity.getTemplate(vmFiles.get(formatIndex));
            template.merge(context, sw);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        } finally {
            export = sw.toString();
        }
        return export;
    }

    public static List<String> getFormatList() {
        return formatList;
    }
}
