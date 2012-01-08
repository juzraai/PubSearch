package pubsearch.data;

import javax.persistence.Transient;

/**
 *
 * @author Zsolt
 */
public abstract class BaseEntity {

    @Transient
    protected boolean stored = false;

    public void store() {
        if (!stored) {
            Connection.getEm().getTransaction().begin();
            Connection.getEm().persist(this);
            Connection.getEm().getTransaction().commit();
        }
    }
}
