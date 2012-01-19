package pubsearch.data;

/**
 * Az entity-k közös tulajdonságai. Jelen esetben egy tároló store() metódus.
 *
 * @author Zsolt
 */
public abstract class BaseEntity {

    public void store() {
        try {
            Connection.getEm().getTransaction().begin();
            Connection.getEm().persist(this);
            Connection.getEm().getTransaction().commit();
        } catch (Throwable t) {
        }
    }
}
