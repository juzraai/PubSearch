package pubsearch.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;

/**
 * Entity for storing basic information of a publication.
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
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

    /**
     * Needed by JPA.
     */
    protected Publication() {
    }

    public Publication(String authors, String title, Integer year, PDatabase pdatabase) {
        this.authors = authors;
        this.title = title;
        this.year = year;
        this.pdatabase = pdatabase;
    }

    /**
     * Stores a publication in the database.
     * @param p Publication object to store.
     */
    public static synchronized void store(Publication p) {
        Connection.getEntityManager().getTransaction().begin();
        Connection.getEntityManager().persist(p);
        try {
            Connection.getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Exception on commit.");
        }
    }

    /**
     * A Publication object is identified by four fields: authors, title, year and pdb
     * (logically, not in the database!), this method finds or creates the Publication
     * object having the given parameters.
     * @param authors List of authors as a String.
     * @param title Title of the publication.
     * @param year Release year of the publication.
     * @param pdb PDatabase object, which tells which publication database this publication was found in.
     * @return Reference for the Publication object having the given parameters.
     */
    public static synchronized Publication getReferenceFor(String authors, String title, int year, PDatabase pdb) {
        Query q = Connection.getEntityManager().createQuery("SELECT p FROM Publication p WHERE p.authors=:au AND p.title=:ti AND p.year=" + year + " AND p.pdatabase.name=:pdbn");
        q.setParameter("au", authors);
        q.setParameter("ti", title);
        q.setParameter("pdbn", pdb.getName());
        List<Publication> pl = q.getResultList();
        if (pl.isEmpty()) {
            Publication p = new Publication(authors, title, year, pdb);
            store(p); // eltároljuk, hogy kapjunk ID-t
            return p;
        } else {
            return pl.get(0);
        }
    }

    /**
     * Selects the matching publications from the database.
     * @param filterAuthors Filter for the 'authors' field. Will be surrounded with % and spaces will be replaced with % too.
     * @param filterTitle Filter for the 'title' field. Will be surrounded with % and spaces will be replaced with % too.
     * @return The query result: list of the matching publications.
     */
    public static List<Publication> searchResults(String filterAuthors, String filterTitle) {
        filterAuthors = filterAuthors.replaceAll(" ", "% ");
        filterTitle = filterTitle.replaceAll(" ", "%");
        return Connection.getEntityManager().createQuery("SELECT p FROM Publication p WHERE p.authors LIKE '%" + filterAuthors + "%' AND p.title LIKE '%" + filterTitle + "%'").getResultList();
    }

    public void addCitedBy(Publication p) {
        citedBy.add(p);
    }

    @Transient
    public int getCitedByCount() {
        return citedBy.size();
    }

    /**
     * @return The release year of the publication as String, or "(N/A)" if year is -1.
     */
    @Transient
    public String getYearAsString() {
        if (-1 == year) {
            return "(N/A)";
        } else {
            return year.toString();
        }
    }

    /**
     * @return 'Name' field of the stored PDatabase object if it's not null, otherwise "(unknown)".
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
