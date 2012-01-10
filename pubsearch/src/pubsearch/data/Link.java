/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsearch.data;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * Egy publikációs adatbázisból egy publikációra mutató link.
 *
 * @author Zsolt
 */
@Entity
public class Link extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private String url;
    private int pubID;
    private int dbID;

    public Link() {
    }

    public Link(String url, int dbID, int pubID) {
        this.url = url;
        this.dbID = dbID;
        this.pubID = pubID;
    }

    @Id
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDbID() {
        return dbID;
    }

    public void setDbID(int dbID) {
        this.dbID = dbID;
    }

    @Transient
    public String getDbName() {
        // nem a legszebb megoldás, JPA nélkül ezt a külső lekérdezésbe is be lehetett volna tenni...
        return (String) Connection.getEm().createQuery("SELECT d.name FROM PubDb d WHERE d.id=" + dbID).getSingleResult();
    }

    @Id
    public int getPubID() {
        return pubID;
    }

    public void setPubID(int pubID) {
        this.pubID = pubID;
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
