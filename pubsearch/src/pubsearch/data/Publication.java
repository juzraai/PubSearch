/*
 * To change this template, choose StringTools | Templates
 * and open the template in the editor.
 */
package pubsearch.data;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import pubsearch.StringTools;

/**
 * Egy publikáció alapvető adatai.
 *
 * @author Zsolt
 */
@Entity
public class Publication extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String bibtex;
    private String authors;
    private String title;
    private Integer year;

    public Publication() {
    }

    public Publication(String bibtex, String authors, String title, int year) {
        this.bibtex = bibtex;
        this.authors = authors;
        this.title = title;
        this.year = year;
    }

    /**
     * Lekérdezi az adatbázisból azokat a publikációkat, amelyek megfelelnek a szerző és cím szűrési feltételeknek.
     * @param filterAuthors A szerzők szűrése. (szavakat külön veszi, de a sorrend marad)
     * @param filterTitle A cím szűráse. (szavakat külön veszi, de a sorrend marad)
     * @return A megfelelő publikációk listája.
     */
    public static List<Publication> searchResults(String filterAuthors, String filterTitle) {
        filterAuthors = filterAuthors.replace(' ', '%');
        filterTitle = filterTitle.replace(' ', '%');
        return Connection.getEm().createQuery("SELECT p FROM Publication p WHERE p.authors LIKE '%" + filterAuthors + "%' AND p.title LIKE '%" + filterTitle + "%'").getResultList();
    }

    /**
     * Lekérdezi az adatbázisból azokat a publikációkat, amelyek erre a publikációra hivatkoznak (idéznek belőle).
     * @return  A megfelelő publikációk listja.
     */
    public List<Publication> getCites() {
        return Connection.getEm().createQuery("SELECT p FROM Publication p WHERE p.id IN (SELECT c.citedByPubID FROM Cite c WHERE c.pubID=" + id + ")").getResultList();
    }

    /**
     * Lekérdezi az adatbázisból a publikációhoz tartozó linkeket.
     * @return A megfelelő linkek listája.
     */
    public List<Link> getLinks() {
        return Connection.getEm().createQuery("SELECT l FROM Link l WHERE l.pubID=" + id).getResultList();
    }

    public String getAuthors() {
        if (authors == null && bibtex != null) {
            return StringTools.findFirstMatch(bibtex, "author = {(.*?)}");
        }
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getBibtex() {
        // TODO if (bibtex==null) GENERÁL!!!
        return (null != bibtex) ? bibtex : "N/A";
    }

    public void setBibtex(String bibtex) {
        this.bibtex = bibtex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        if (title == null && bibtex != null) {
            return StringTools.findFirstMatch(bibtex, "[^(book)]title = {(.*?)}");
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        if (year == null && bibtex != null) {
            return Integer.parseInt(StringTools.findFirstMatch(bibtex, "year = {(.*?)}"));
        }
        return year;
    }

    public void setYear(Integer year) {
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
        if (!(object instanceof Publication)) {
            return false;
        }
        Publication other = (Publication) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pubsearch.data.Publication[ id=" + id + " ]";
    }
}
