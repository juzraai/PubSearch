package pubsearch.data;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Egy publikációs adatbázis jellemzői (feldolgozáshoz).
 *
 * @author Zsolt
 */
@Entity
public class PubDb extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    // basic data
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String baseUrl;
    // form submit
    private String submitUrl;
    private String submitMethod;                // "GET" / "POST"
    private String submitParamsFormat;          // formatstring, %s:author
    private String submitParamsWithTitleFormat; // formatstring, %s:querystring, %s:title
    // result list
    private String pubPageLinkPattern;          // regex
    private String pubPageLinkModFormat;        // formatstring, %s:link before mod
    private String nextPageLinkPattern;         // regex
    // pub page
    private String bibtexLinkPattern;           // regex
    private String bibtePatternx;               // regex
    private String authorsPattern;              // regex
    private String titlePattern;                // regex
    private String yearPattern;                 // regex, group 2 kell !!! (többinél 1)
    private String refPubListPageLinkPattern;   // regex
    // ref pub list
    private String refPubListBlockPattern;      // regex
    private String refPubAuthorsPattern;        // regex
    private String refPubTitlePattern;          // regex
    private String refPubYearPattern;           // regex

    public PubDb() {
    }

    public static List<PubDb> getAll() {
        return Connection.getEm().createQuery("SELECT p FROM PubDb p").getResultList();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PubDb)) {
            return false;
        }
        PubDb other = (PubDb) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pubsearch.data.PubDb[ id=" + id + " ]";
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

    public String getBibtePatternx() {
        return bibtePatternx;
    }

    public void setBibtePatternx(String bibtePatternx) {
        this.bibtePatternx = bibtePatternx;
    }

    public String getBibtexLinkPattern() {
        return bibtexLinkPattern;
    }

    public void setBibtexLinkPattern(String bibtexLinkPattern) {
        this.bibtexLinkPattern = bibtexLinkPattern;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNextPageLinkPattern() {
        return nextPageLinkPattern;
    }

    public void setNextPageLinkPattern(String nextPageLinkPattern) {
        this.nextPageLinkPattern = nextPageLinkPattern;
    }

    public String getPubPageLinkModFormat() {
        return pubPageLinkModFormat;
    }

    public void setPubPageLinkModFormat(String pubPageLinkModFormat) {
        this.pubPageLinkModFormat = pubPageLinkModFormat;
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
        this.submitParamsWithTitleFormat = submitParamsWithTitleFormat;
    }

    public String getSubmitUrl() {
        return submitUrl;
    }

    public void setSubmitUrl(String submitUrl) {
        this.submitUrl = submitUrl;
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
}
