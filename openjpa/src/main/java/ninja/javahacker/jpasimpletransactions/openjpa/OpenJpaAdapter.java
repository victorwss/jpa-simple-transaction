package ninja.javahacker.jpasimpletransactions.openjpa;

import java.sql.Connection;
import javax.persistence.EntityManager;
import lombok.NonNull;
import ninja.javahacker.jpasimpletransactions.ProviderAdapter;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.PersistenceProviderImpl;

/**
 * Implementation of {@link ProviderAdapter} for Open JPA.
 * @author Victor Williams Stafusa da Silva
 */
public final class OpenJpaAdapter implements ProviderAdapter {

    private static final PersistenceProviderImpl PROVIDER = new PersistenceProviderImpl();

    public static final OpenJpaAdapter CANONICAL = new OpenJpaAdapter();

    public OpenJpaAdapter() {
    }

    @Override
    public boolean recognizes(@NonNull EntityManager em) {
        return em instanceof OpenJPAEntityManager;
    }

    @Override
    public Connection getConnection(@NonNull EntityManager em) {
        if (!recognizes(em)) throw new UnsupportedOperationException();
        OpenJPAEntityManager oem = (OpenJPAEntityManager) em;
        return (Connection) oem.getConnection();
    }

    @Override
    public PersistenceProviderImpl getJpaProvider() {
        return PROVIDER;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof OpenJpaAdapter;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}