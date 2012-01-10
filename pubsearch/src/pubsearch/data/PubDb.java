package pubsearch.data;

import java.io.Serializable;
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
    private String submitParams;                // formatstring, %s:author
    private String submitParamsWithTitle;       // formatstring, %s:querystring, %s:title
    // result list
    private String pubPageLink;                 // regex
    private String pubPageLinkMod;              // formatstring, %s:link before mod
    private String nextPageLink;                // regex, group 1 kell
    // pub page
    private String bibtexLink;                  // regex
    private String bibtex;                      // regex
    private String authors;                     // regex
    private String title;                       // regex
    private String year;                        // regex
    private String refPubListPageLink;          // regex
    // ref pub list
    private String refPubListBlock;             // regex

    public PubDb() {
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBibtex() {
        return bibtex;
    }

    public void setBibtex(String bibtex) {
        this.bibtex = bibtex;
    }

    public String getBibtexLink() {
        return bibtexLink;
    }

    public void setBibtexLink(String bibtexLink) {
        this.bibtexLink = bibtexLink;
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

    public String getNextPageLink() {
        return nextPageLink;
    }

    public void setNextPageLink(String nextPageLink) {
        this.nextPageLink = nextPageLink;
    }

    public String getPubPageLink() {
        return pubPageLink;
    }

    public void setPubPageLink(String pubPageLink) {
        this.pubPageLink = pubPageLink;
    }

    public String getPubPageLinkMod() {
        return pubPageLinkMod;
    }

    public void setPubPageLinkMod(String pubPageLinkMod) {
        this.pubPageLinkMod = pubPageLinkMod;
    }

    public String getRefPubListBlock() {
        return refPubListBlock;
    }

    public void setRefPubListBlock(String refPubListBlock) {
        this.refPubListBlock = refPubListBlock;
    }

    public String getRefPubListPageLink() {
        return refPubListPageLink;
    }

    public void setRefPubListPageLink(String refPubListPageLink) {
        this.refPubListPageLink = refPubListPageLink;
    }

    public String getSubmitMethod() {
        return submitMethod;
    }

    public void setSubmitMethod(String submitMethod) {
        this.submitMethod = submitMethod;
    }

    public String getSubmitParams() {
        return submitParams;
    }

    public void setSubmitParams(String submitParams) {
        this.submitParams = submitParams;
    }

    public String getSubmitParamsWithTitle() {
        return submitParamsWithTitle;
    }

    public void setSubmitParamsWithTitle(String submitParamsWithTitle) {
        this.submitParamsWithTitle = submitParamsWithTitle;
    }

    public String getSubmitUrl() {
        return submitUrl;
    }

    public void setSubmitUrl(String submitUrl) {
        this.submitUrl = submitUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
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
}
