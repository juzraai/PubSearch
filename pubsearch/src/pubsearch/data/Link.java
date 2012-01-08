/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsearch.data;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Egy publikációs adatbázisból egy publikációra mutató link.
 * @author Zsolt
 */
@Entity
public class Link extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private String url;
    private int dbID;
    private int pubID;

    public Link() {
    }

    public Link(String url, int dbID, int pubID) {
        this.url = url;
        this.dbID = dbID;
        this.pubID = pubID;
        store();
    }

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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Link)) {
            return false;
        }
        Link other = (Link) object;
        if ((this.url == null && other.url != null) || (this.url != null && !this.url.equals(other.url))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pubsearch.data.Link[ id=" + url + " ]";
    }
}
