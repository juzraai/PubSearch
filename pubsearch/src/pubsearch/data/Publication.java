/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsearch.data;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.*;

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
    private int year;

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

    public List<Publication> getCites() {
        return Connection.getEm().createQuery("SELECT p FROM Publication p WHERE p.id IN (SELECT c.citedByPubID FROM Cite c WHERE c.pubID=" + id + ")").getResultList();
    }

    public List<Link> getLinks() {
        return Connection.getEm().createQuery("SELECT l FROM Link l WHERE l.pubID=" + id).getResultList();
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getBibtex() {
        // TODO if (bibtex==null) GENERÁL!!!
        return bibtex;
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
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
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
