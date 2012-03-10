package hu.juranyi.zsolt.pubsearch.data;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Entity for storing crawling informations for a publication database. It contains
 * parameters, field names, URLs, and mostly regex patterns.
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
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
    @Column(nullable = false)
    private String submitParamsFormat;          // formatstring, %s:author
    private String submitParamsWithTitleFormat = "%s"; // formatstring, %s:querystring, %s:title
    // result list
    private String startField = "start";
    private Byte firstIndex = 0;                // 0 / 1
    private Byte resultsPerPage = 10;
    private String noResultsTextPattern;
    private String resultListPattern;
    private String resultListItemPattern;
    private String pubPageLinkPattern;
    private String pubPageLinkModFormat = "%s"; // formatstring, %s:link before mod
    // pub page
    private String bibtexLinkPattern;
    private String bibtexPattern;
    private String authorsPattern;
    private String titlePattern;
    private String yearPattern;
    private String refPubListPageLinkPattern;
    // ref pub list
    private String refPubListPattern;
    private String refPubListItemPattern;
    private String refPubAuthorsPattern;
    private String refPubTitlePattern;
    private String refPubYearPattern;

    /**
     * Needed by JPA.
     */
    protected PDatabase() {
    }

    private PDatabase(String name) {
        this.name = name;
    }

    /**
     * A PDatabase object is identified by its name, this method finds or creates
     * the PDatabase object having the given name.
     * @param name Name field of the needed PDatabase object.
     * @return Reference for the PDatabase object having the given name.
     */
    public static PDatabase getReferenceFor(String name) {
        PDatabase pdb = Connection.getEntityManager().find(PDatabase.class, name);
        if (null == pdb) {
            return new PDatabase(name);
        } else {
            return pdb;
        }
    }

    /**
     * @return All PDatabase object from the database.
     */
    public static List<PDatabase> getAll() {
        return Connection.getEntityManager().createQuery("SELECT p FROM PDatabase p").getResultList();
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

    public String getNoResultsTextPattern() {
        return noResultsTextPattern;
    }

    public void setNoResultsTextPattern(String noResultsTextPattern) {
        this.noResultsTextPattern = noResultsTextPattern;
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

    public String getRefPubListItemPattern() {
        return refPubListItemPattern;
    }

    public void setRefPubListItemPattern(String refPubListItemPattern) {
        this.refPubListItemPattern = refPubListItemPattern;
    }

    public String getRefPubListPattern() {
        return refPubListPattern;
    }

    public void setRefPubListPattern(String refPubListPattern) {
        this.refPubListPattern = refPubListPattern;
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

    public String getResultListItemPattern() {
        return resultListItemPattern;
    }

    public void setResultListItemPattern(String resultListItemPattern) {
        this.resultListItemPattern = resultListItemPattern;
    }

    public String getResultListPattern() {
        return resultListPattern;
    }

    public void setResultListPattern(String resultListPattern) {
        this.resultListPattern = resultListPattern;
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
