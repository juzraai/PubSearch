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
public class Publication implements Serializable {

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
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Citing", joinColumns =
    @JoinColumn(name = "PubID"), inverseJoinColumns =
    @JoinColumn(name = "CitedByPubID"))
    private List<Publication> citedBy = new ArrayList<Publication>();

    protected Publication() {
    }

    private Publication(String title, Integer year) {
        this.title = title;
        this.year = year;
    }

    /**
     * Mivel 2 publikációt akkor tekintünk egyezőnek, ha a cím és az évszám azonos,
     * feltöltés előtt meg kell nézni, hogy tároltunk-e már róla információkat. Ha
     * igen, a program lekéri az adatbázisból a teljes objektumot, ha nem, akkor
     * egy újat gyárt a megadott adatokkal.
     * @param title A publikációt azonosító cím.
     * @param year A publikációt azonosító év.
     * @return Referencia a Publication objektumára.
     */
    public static Publication getReferenceFor(String title, int year) { // TODO WHERE LOWER(p.title)=LOWER( +title+ )
        List<Publication> pl = Connection.getEm().createQuery("SELECT p FROM Publication p WHERE p.title=\"" + title + "\" AND p.year=" + year).getResultList();
        if (pl.isEmpty()) {
            return new Publication(title, year);
        } else {
            return pl.get(0);
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
        return Connection.getEm().createQuery("SELECT p FROM Publication p WHERE p.authors LIKE '%" + filterAuthors + "%' AND p.title LIKE '%" + filterTitle + "%'").getResultList();
    }

    public void addCitedBy(Publication p) {
        if (citedBy.indexOf(p) == -1) {
            citedBy.add(p);
        }
    }

    public void addLink(Link l) {
        if (links.indexOf(l) == -1) {
            links.add(l);
        }
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getBibtex() {
        return bibtex;
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
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
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
