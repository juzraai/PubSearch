/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsearch.data;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author Zsolt
 */
@Entity
public class PubDb implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @Transient
    private boolean stored = false;

    public PubDb()
    {
        
    }
    public PubDb(String name) {
        this.name = name;
        
        if (!stored) {
            Connection.em.getTransaction().begin();
            Connection.em.persist(this);
            Connection.em.getTransaction().commit();
            stored = true;
        }
    }
    
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(object instanceof PubDb)) {
            return false;
        }
        PubDb other = (PubDb) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pubsearch.data.PubDb[ id=" + id + " ]";
    }
    
}
