package hu.juranyi.zsolt.pubsearch.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.RollbackException;

/**
 * Imports PDatabase definitions from conf/*.pdb files, and uploads them to the
 * database.
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
 */
public class Importer {

    /**
     * Gets the file list for the pattern 'conf/*.pdb', then calls loadPDatabase
     * for all of them.
     */
    public static void loadPDatabases() {
        File confDir = new File("conf");
        String[] confFiles = confDir.list();
        if (null != confFiles) {
            for (String f : confFiles) {
                if (f.endsWith(".pdb")) {
                    loadPDatabase("conf" + File.separator + f);
                }
            }
        }
    }

    /**
     * Parses a .pdb file as a PDatabase object, then uploads it to the database.
     * @param fileName .pdb filename.
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
        } catch (IOException ex) {
            System.err.println("Cannot import PDB file: " + fileName);
        } finally {
            if (null != r) {
                try {
                    r.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // upload to database

        String name = fields.get("name");
        if (null != name) {
            Connection.getEntityManager().getTransaction().begin();
            PDatabase pdb = PDatabase.getReferenceFor(name);

            pdb.setBaseUrl(fields.get("baseurl"));
            pdb.setSubmitUrl(fields.get("submiturl"));
            pdb.setSubmitParamsFormat(fields.get("submitparamsformat"));
            pdb.setSubmitParamsWithTitleFormat(fields.get("submitparamswithtitleformat"));

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
            pdb.setNoResultsTextPattern(fields.get("noresultstextpattern"));
            pdb.setResultListPattern(fields.get("resultlistpattern"));
            pdb.setResultListItemPattern(fields.get("resultlistitempattern"));
            pdb.setPubPageLinkPattern(fields.get("pubpagelinkpattern"));
            pdb.setPubPageLinkModFormat(fields.get("pubpagelinkmodformat"));

            pdb.setBibtexLinkPattern(fields.get("bibtexlinkpattern"));
            pdb.setBibtexPattern(fields.get("bibtexpattern"));
            pdb.setAuthorsPattern(fields.get("authorspattern"));
            pdb.setTitlePattern(fields.get("titlepattern"));
            pdb.setYearPattern(fields.get("yearpattern"));
            pdb.setRefPubListPageLinkPattern(fields.get("refpublistpagelinkpattern"));

            pdb.setRefPubListPattern(fields.get("refpublistpattern"));
            pdb.setRefPubListItemPattern(fields.get("refpublistitempattern"));
            pdb.setRefPubAuthorsPattern(fields.get("refpubauthorspattern"));
            pdb.setRefPubTitlePattern(fields.get("refpubtitlepattern"));
            pdb.setRefPubYearPattern(fields.get("refpubyearpattern"));

            Connection.getEntityManager().persist(pdb);
            try {
                Connection.getEntityManager().getTransaction().commit();
                System.out.println("IMPORTED DATABASE: " + name);
            } catch (RollbackException e) {
                System.err.println("Exception on commit.");
            }
        }
    }
}
