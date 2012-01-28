/*
 * To change this template, choose StringTools | Templates
 * and open the template in the editor.
 */
package pubsearch.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;

/**
 * Egy publikáció alapvető adatai.
 *
 * @author Zsolt
 */
@Entity
public class Publication implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String authors;
    private String bibtex = "";
    private String title;
    private String url = "";
    private Integer year = 1;
    private PDatabase pdatabase;
    private Set<Publication> citedBy = new HashSet<Publication>();

    protected Publication() {
    }

    public Publication(String authors, String title, Integer year, PDatabase pdatabase) {
        this.authors = authors;
        this.title = title;
        this.year = year;
        this.pdatabase = pdatabase;
    }

    /**
     * Egy publikációt a szerzők, a cím és az évszám azonosít, valamint az adatbázis,
     * ahol a program megtalálta. Az URL azért nem, mert bizonyos adatbázisokban a
     * hivatkozó publikációkhoz nem rendelődik URL, így azok URL=null-lal tárolódnak.
     * A metódus megnézi, tároltuk-e már a megadott publikációt az adatbázisban. Ha
     * igen, akkor visszaad rá egy referenciát, ha nem, akkor újat hoz létre.
     * @param authors
     * @param title
     * @param year
     * @param pdb
     * @return Referencia a Publication objektumra.
     */
    public static Publication getReferenceFor(String authors, String title, int year, PDatabase pdb) {
        Query q = Connection.getEm().createQuery("SELECT p FROM Publication p WHERE p.authors=:au AND p.title=:ti AND p.year=" + year + " AND p.pdatabase.name=:pdbn");
        q.setParameter("au", authors);
        q.setParameter("ti", title);
        q.setParameter("pdbn", pdb.getName());
        List<Publication> pl = q.getResultList();//Connection.getEm().createQuery("SELECT p FROM Publication p WHERE p.authors=\"" + authors + "\" AND p.title=\"" + title + "\" AND p.year=" + year + " AND p.pdatabase.name=\"" + pdb.getName() + "\"").getResultList();
        if (pl.isEmpty()) {
            return new Publication(authors, title, year, pdb);
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
        filterAuthors = filterAuthors.replaceAll(" ", "% ");
        filterTitle = filterTitle.replaceAll(" ", "%");
        return Connection.getEm().createQuery("SELECT p FROM Publication p WHERE p.authors LIKE '%" + filterAuthors + "%' AND p.title LIKE '%" + filterTitle + "%'").getResultList();
    }

    public void addCitedBy(Publication p) {
        /*if (citedBy.indexOf(p) == -1) {
            citedBy.add(p);
        }*/
        citedBy.add(p);
    }

    @Transient
    public int getCitedByCount() {
        return citedBy.size();
    }

    @Transient
    public String getYearAsString() {
        if (-1 == year) {
            return "(N/A)";
        } else {
            return year.toString();
        }
    }

    /**
     * @return Az adatbázis neve, ahol megtalálta ezt a publikációt.
     */
    @Transient
    public String getDbName() {
        if (null == pdatabase) {
            return "(unknown)";
        }
        return pdatabase.getName();
    }

    @Column(nullable = false)
    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    @Column(length = 4096)
    public String getBibtex() {
        return bibtex;
    }

    public void setBibtex(String bibtex) {
        this.bibtex = bibtex;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Citing", joinColumns =
    @JoinColumn(name = "PubID"), inverseJoinColumns =
    @JoinColumn(name = "CitedByPubID"))
    public Set<Publication> getCitedBy() {
        return citedBy;
    }

    public void setCitedBy(Set<Publication> citedBy) {
        this.citedBy = citedBy;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    public PDatabase getPdatabase() {
        return pdatabase;
    }

    public void setPdatabase(PDatabase pdatabase) {
        this.pdatabase = pdatabase;
    }

    @Column(nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
