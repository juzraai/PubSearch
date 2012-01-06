/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsearch.data;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

/**
 * Egy publikáció alapvető adatai.
 * @author Zsolt
 */
@Entity
public class Publication implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String authors;
    private String title;
    private int year;
    @Transient
    private boolean stored = false;

    public Publication() {
    }

    public Publication(String authors, String title, int year) {
        this.authors = authors;
        this.title = title;
        this.year = year;

        if (!stored) {
            Connection.em.getTransaction().begin();
            Connection.em.persist(this);
            Connection.em.getTransaction().commit();
            stored = true;
        }
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
        Query q = Connection.em.createQuery("SELECT p FROM Publication p WHERE p.authors LIKE '%" + filterAuthors + "%' AND p.title LIKE '%" + filterTitle + "%'");
        return q.getResultList();
    }

        public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
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
