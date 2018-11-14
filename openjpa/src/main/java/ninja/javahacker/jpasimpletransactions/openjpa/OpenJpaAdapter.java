package ninja.javahacker.jpasimpletransactions.openjpa;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
@SuppressFBWarnings("IMC_IMMATURE_CLASS_NO_TOSTRING")
public class OpenJpaAdapter implements ProviderAdapter {

    public static final OpenJpaAdapter INSTANCE = new OpenJpaAdapter();

    private final PersistenceProviderImpl provider;

    private OpenJpaAdapter() {
        this.provider = new PersistenceProviderImpl();
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
        return provider;
    }
}