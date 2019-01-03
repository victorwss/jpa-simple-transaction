package ninja.javahacker.jpasimpletransactions.openjpa;

import java.sql.Connection;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import lombok.NonNull;
import ninja.javahacker.jpasimpletransactions.ExtendedEntityManager;
import ninja.javahacker.jpasimpletransactions.ProviderAdapter;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAEntityManagerFactory;
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
    public boolean recognizes(@NonNull EntityManagerFactory emf) {
        return emf instanceof OpenJPAEntityManagerFactory;
    }

    @Override
    public boolean recognizes(@NonNull EntityManager em) {
        return ExtendedEntityManager.unwrap(em) instanceof OpenJPAEntityManager;
    }

    @Override
    public Connection getConnection(@NonNull EntityManager em) {
        OpenJPAEntityManager oem = (OpenJPAEntityManager) ExtendedEntityManager.unwrap(ensureRecognition(em));
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