package pubsearch.data;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.RollbackException;

/**
 *
 * @author Zsolt
 */
public class Importer {

    /**
     * Betölti a 'conf' mappában található *.pdb fájlokból a publikációs adatbázisok
     * adatait.
     */
    public static void loadPDatabases() {
        File confDir = new File("conf");
        String[] confFiles = confDir.list();
        if (null != confFiles) {
            for (String f : confFiles) {
                if (f.endsWith(".pdb")) {
                    loadPDatabase("conf/" + f);
                }
            }
        }
    }

    /**
     * Betölti egy PDatabase adatait az adatbázisba, a megadott fájlból.
     * @param fileName Fájlnév, ajánlott: *.pdb
     */
    private static void loadPDatabase(String fileName) {

        // read file

        Map<String, String> fields = new HashMap<String, String>();
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(fileName));
            while (r.ready()) {
                String[] lp = r.readLine().split("\t", 2);
                if (2 == lp.length) {
                    fields.put(lp[0].toLowerCase(), lp[1].trim());
                }
            }
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } finally {
            if (null != r) {
                try {
                    r.close();
                } catch (IOException e) {
                }
            }
        }

        // upload to database

        String name = fields.get("name");
        if (null != name) {
            Connection.getEm().getTransaction().begin();
            PDatabase pdb = PDatabase.getReferenceFor(name);

            pdb.setBaseUrl(fields.get("baseurl"));
            pdb.setSubmitUrl(fields.get("submiturl"));
            pdb.setSubmitMethod(fields.get("submitmethod"));
            pdb.setSubmitParamsFormat(fields.get("submitparamsformat"));
            pdb.setSubmitParamsWithTitleFormat(fields.get("submitparamswithtitleformat"));
            pdb.setPubPageLinkPattern(fields.get("pubpagelinkpattern"));
            pdb.setPubPageLinkModFormat(fields.get("pubpagelinkmodformat"));

            String s = fields.get("resultsperpage");
            if (null != s) {
                try {
                    pdb.setResultsPerPage(Byte.valueOf(s));
                } catch (NumberFormatException e) {
                }
            }

            s = fields.get("firstindex");
            if (null != s) {
                try {
                    pdb.setFirstIndex(Byte.valueOf(s));
                } catch (NumberFormatException e) {
                }
            }

            pdb.setStartField(fields.get("startfield"));
            pdb.setBibtexLinkPattern(fields.get("bibtexlinkpattern"));
            pdb.setBibtexPattern(fields.get("bibtexpattern"));
            pdb.setAuthorsPattern(fields.get("authorspattern"));
            pdb.setTitlePattern(fields.get("titlepattern"));
            pdb.setYearPattern(fields.get("yearpattern"));
            pdb.setRefPubListPageLinkPattern(fields.get("refpublistpagelinkpattern"));
            pdb.setRefPubListBlockPattern(fields.get("refpublistblockpattern"));
            pdb.setRefPubBlockPattern(fields.get("refpubblockpattern"));
            pdb.setRefPubAuthorsPattern(fields.get("refpubauthorspattern"));
            pdb.setRefPubTitlePattern(fields.get("refpubtitlepattern"));
            pdb.setRefPubYearPattern(fields.get("refpubyearpattern"));

            Connection.getEm().persist(pdb);
            try {
                Connection.getEm().getTransaction().commit();
                System.out.println("IMPORTED DATABASE: " + name);
            } catch (RollbackException rbe) {
            }
        }
    }
}
