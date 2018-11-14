package ninja.javahacker.jpasimpletransactions.eclipselink;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
@SuppressFBWarnings("IMC_IMMATURE_CLASS_NO_TOSTRING")
public class EclipselinkAdapter implements ProviderAdapter {

    public static final EclipselinkAdapter INSTANCE = new EclipselinkAdapter();

    private final PersistenceProvider provider;

    private EclipselinkAdapter() {
        this.provider = new PersistenceProvider();
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
        return provider;
    }
}