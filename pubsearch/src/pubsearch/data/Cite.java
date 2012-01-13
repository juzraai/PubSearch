package pubsearch.data;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Egy idézett-idéző pár megvalósítása.
 *
 * @author Zsolt
 */
@Entity
public class Cite extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private Long pubID;
    @Id
    private Long citedByPubID;

    public Cite() {
    }

    public Cite(Long pubID, Long citedByPubID) {
        this.pubID = pubID;
        this.citedByPubID = citedByPubID;
    }

    public Long getCitedByPubID() {
        return citedByPubID;
    }

    public void setCitedByPubID(Long citedByPubID) {
        this.citedByPubID = citedByPubID;
    }

    public Long getPubID() {
        return pubID;
    }

    public void setPubID(Long pubID) {
        this.pubID = pubID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pubID != null ? pubID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Cite)) {
            return false;
        }
        Cite other = (Cite) object;
        if (this.pubID == null || this.citedByPubID == null || other.pubID == null || other.citedByPubID == null) {
            return false;
        }
        return (this.pubID.equals(other.pubID) && this.citedByPubID.equals(other.citedByPubID));
    }

    @Override
    public String toString() {
        return "pubsearch.data.Cite[ pubID=" + pubID + ", citedByPubID=" + citedByPubID + " ]";
    }
}
