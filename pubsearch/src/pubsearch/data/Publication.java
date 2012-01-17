/*
 * To change this template, choose StringTools | Templates
 * and open the template in the editor.
 */
package pubsearch.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "publication")
    private List<Link> links = new ArrayList<Link>();
    @ManyToMany(cascade= CascadeType.ALL)
    @JoinTable(name = "Citing", joinColumns =
    @JoinColumn(name = "PubID"), inverseJoinColumns =
    @JoinColumn(name = "CitedByPubID"))
    private List<Publication> citedBy = new ArrayList<Publication>();
    @ManyToMany(mappedBy = "citedBy", cascade= CascadeType.ALL)
    private List<Publication> cites = new ArrayList<Publication>();

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
    /*public List<Publication> getCites() {
        return Connection.getEm().createQuery("SELECT p FROM Publication p WHERE p.id IN (SELECT c.citedByPubID FROM Cite c WHERE c.pubID=" + id + ")").getResultList();
    }*/
    public String getAuthors() {
        if (authors == null && bibtex != null) {
            return StringTools.findFirstMatch(bibtex, "author = {(.*?)}", 1); // TODO javítani a regex-et!
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

    public List<Publication> getCitedBy() {
        return citedBy;
    }

    public void setCitedBy(List<Publication> citedBy) {
        this.citedBy = citedBy;
    }

    public List<Publication> getCites() {
        return cites;
    }

    public void setCites(List<Publication> cites) {
        this.cites = cites;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public String getTitle() {
        if (title == null && bibtex != null) {
            return StringTools.findFirstMatch(bibtex, "[^(book)]title = {(.*?)}", 1); // TODO javítani
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        if (year == null && bibtex != null) {
            return Integer.parseInt(StringTools.findFirstMatch(bibtex, "year = {(.*?)}", 1)); // TODO javítani
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
