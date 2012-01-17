/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsearch.data;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Egy publikációs adatbázisból egy publikációra mutató link.
 *
 * @author Zsolt
 */
@Entity
public class Link extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private String url;
    private Publication publication;
    private PDatabase pDatabase;

    public Link() {
    }

    public Link(String url) {
        this.url = url;
    }
    
    @Transient
    public String getDbName() {
        return pDatabase.getName();
    }

    @Id
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    public PDatabase getPdatabase() {
        return pDatabase;
    }

    public void setPdatabase(PDatabase pdatabase) {
        this.pDatabase = pdatabase;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (url != null ? url.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Link)) {
            return false;
        }
        Link other = (Link) object;
        if (this.url == null || other.url == null) {
            return false;
        }
        return (this.url.equalsIgnoreCase(other.url));
    }

    @Override
    public String toString() {
        return "pubsearch.data.Link[ id=" + url + " ]";
    }
}
