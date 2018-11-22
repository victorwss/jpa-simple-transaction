package ninja.javahacker.jpasimpletransactions.eclipselink;

import java.sql.Connection;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import lombok.NonNull;
import ninja.javahacker.jpasimpletransactions.ProviderAdapter;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.PersistenceProvider;

/**
 * Implementation of {@link ProviderAdapter} for Eclipselink.
 * @author Victor Williams Stafusa da Silva
 */
public final class EclipselinkAdapter implements ProviderAdapter {

    private static final PersistenceProvider PROVIDER = new PersistenceProvider();

    public EclipselinkAdapter() {
    }

    @Override
    public boolean recognizes(@NonNull EntityManager em) {
        return em instanceof JpaEntityManager;
    }

    @Override
    public Connection getConnection(@NonNull EntityManager em) {
        if (!recognizes(em)) throw new UnsupportedOperationException();
        EntityTransaction et = em.getTransaction();
        boolean needTransaction = !et.isActive();
        try {
            if (needTransaction) em.getTransaction().begin();
            return em.unwrap(Connection.class);
        } finally {
            if (needTransaction) em.getTransaction().commit();
        }
    }

    @Override
    public PersistenceProvider getJpaProvider() {
        return PROVIDER;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof EclipselinkAdapter;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}