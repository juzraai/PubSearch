package pubsearch.data;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Egy publikációs adatbázis jellemzői (feldolgozáshoz).
 *
 * @author Zsolt
 */
@Entity
public class PDatabase implements Serializable {

    private static final long serialVersionUID = 1L;
    // basic data
    @Id
    private String name;
    @Column(nullable = false, unique = true)
    private String baseUrl;
    // form submit
    private String submitUrl = "";
    private String submitMethod;                // "GET" / "POST"
    @Column(nullable = false)
    private String submitParamsFormat;          // formatstring, %s:author
    private String submitParamsWithTitleFormat = "%s"; // formatstring, %s:querystring, %s:title
    // result list
    private String pubPageLinkPattern;          // regex
    private String pubPageLinkModFormat = "%s"; // formatstring, %s:link before mod
    private String startField = "start";
    private Byte firstIndex = 0;                 // 0 / 1
    private Byte resultsPerPage = 10;
    // pub page
    private String bibtexLinkPattern;           // regex
    private String bibtexPattern;               // regex
    private String authorsPattern;              // regex
    private String titlePattern;                // regex
    private String yearPattern;                 // regex
    private String refPubListPageLinkPattern;   // regex
    // ref pub list
    private String refPubListBlockPattern;      // regex
    private String refPubBlockPattern;          // regex
    private String refPubAuthorsPattern;        // regex
    private String refPubTitlePattern;          // regex
    private String refPubYearPattern;           // regex

    protected PDatabase() {
    }

    private PDatabase(String name) {
        this.name = name;
    }

    /**
     * Az adatbázisokat a nevük azonosítja, ez a metódus megkeresi, hogy egy adott
     * nevű adatbázis van-e már tárolva az adatbázisban, vagy nincs. Ha igen, lekéri
     * az adatbázisból az objektumot, ha nem, akkor újat hoz létre a megadott névvel.
     * @param name Adatbázist azonosító név.
     * @return Referencia a PDatabase objektumra.
     */
    public static PDatabase getReferenceFor(String name) {
        PDatabase pdb = Connection.getEm().find(PDatabase.class, name);
        if (null == pdb) {
            return new PDatabase(name);
        } else {
            return pdb;
        }
    }

    /**
     * @return Az összes PDatabase objektum, ami az adatbázisban van.
     */
    public static List<PDatabase> getAll() {
        return Connection.getEm().createQuery("SELECT p FROM PDatabase p").getResultList();
    }

    public String getAuthorsPattern() {
        return authorsPattern;
    }

    public void setAuthorsPattern(String authorsPattern) {
        this.authorsPattern = authorsPattern;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBibtexPattern() {
        return bibtexPattern;
    }

    public void setBibtexPattern(String bibtexPattern) {
        this.bibtexPattern = bibtexPattern;
    }

    public String getBibtexLinkPattern() {
        return bibtexLinkPattern;
    }

    public void setBibtexLinkPattern(String bibtexLinkPattern) {
        this.bibtexLinkPattern = bibtexLinkPattern;
    }

    public Byte getFirstIndex() {
        return firstIndex;
    }

    public void setFirstIndex(Byte firstIndex) {
        if (null != firstIndex) {
            this.firstIndex = firstIndex;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPubPageLinkModFormat() {
        return pubPageLinkModFormat;
    }

    public void setPubPageLinkModFormat(String pubPageLinkModFormat) {
        if (null != pubPageLinkModFormat) {
            this.pubPageLinkModFormat = pubPageLinkModFormat;
        }
    }

    public String getPubPageLinkPattern() {
        return pubPageLinkPattern;
    }

    public void setPubPageLinkPattern(String pubPageLinkPattern) {
        this.pubPageLinkPattern = pubPageLinkPattern;
    }

    public String getRefPubAuthorsPattern() {
        return refPubAuthorsPattern;
    }

    public void setRefPubAuthorsPattern(String refPubAuthorsPattern) {
        this.refPubAuthorsPattern = refPubAuthorsPattern;
    }

    public String getRefPubBlockPattern() {
        return refPubBlockPattern;
    }

    public void setRefPubBlockPattern(String refPubBlockPattern) {
        this.refPubBlockPattern = refPubBlockPattern;
    }

    public String getRefPubListBlockPattern() {
        return refPubListBlockPattern;
    }

    public void setRefPubListBlockPattern(String refPubListBlockPattern) {
        this.refPubListBlockPattern = refPubListBlockPattern;
    }

    public String getRefPubListPageLinkPattern() {
        return refPubListPageLinkPattern;
    }

    public void setRefPubListPageLinkPattern(String refPubListPageLinkPattern) {
        this.refPubListPageLinkPattern = refPubListPageLinkPattern;
    }

    public String getRefPubTitlePattern() {
        return refPubTitlePattern;
    }

    public void setRefPubTitlePattern(String refPubTitlePattern) {
        this.refPubTitlePattern = refPubTitlePattern;
    }

    public String getRefPubYearPattern() {
        return refPubYearPattern;
    }

    public void setRefPubYearPattern(String refPubYearPattern) {
        this.refPubYearPattern = refPubYearPattern;
    }

    public Byte getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(Byte resultsPerPage) {
        if (null != resultsPerPage) {
            this.resultsPerPage = resultsPerPage;
        }
    }

    public String getStartField() {
        return startField;
    }

    public void setStartField(String startField) {
        if (null != startField) {
            this.startField = startField;
        }
    }

    public String getSubmitMethod() {
        return submitMethod;
    }

    public void setSubmitMethod(String submitMethod) {
        this.submitMethod = submitMethod;
    }

    public String getSubmitParamsFormat() {
        return submitParamsFormat;
    }

    public void setSubmitParamsFormat(String submitParamsFormat) {
        this.submitParamsFormat = submitParamsFormat;
    }

    public String getSubmitParamsWithTitleFormat() {
        return submitParamsWithTitleFormat;
    }

    public void setSubmitParamsWithTitleFormat(String submitParamsWithTitleFormat) {
        if (null != submitParamsWithTitleFormat) {
            this.submitParamsWithTitleFormat = submitParamsWithTitleFormat;
        }
    }

    public String getSubmitUrl() {
        return submitUrl;
    }

    public void setSubmitUrl(String submitUrl) {
        if (null != submitUrl) {
            this.submitUrl = submitUrl;
        }
    }

    public String getTitlePattern() {
        return titlePattern;
    }

    public void setTitlePattern(String titlePattern) {
        this.titlePattern = titlePattern;
    }

    public String getYearPattern() {
        return yearPattern;
    }

    public void setYearPattern(String yearPattern) {
        this.yearPattern = yearPattern;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (name != null ? name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PDatabase)) {
            return false;
        }
        PDatabase other = (PDatabase) object;
        if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pubsearch.data.PDatabase[ name=" + name + " ]";
    }
}
