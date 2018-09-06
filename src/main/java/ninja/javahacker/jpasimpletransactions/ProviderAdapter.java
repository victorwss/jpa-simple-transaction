package ninja.javahacker.jpasimpletransactions;

import java.sql.Connection;
import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
public interface ProviderAdapter {
    public boolean recognizes(@NonNull EntityManager em);
    public Connection getConnection(@NonNull EntityManager em);
    public Class<? extends PersistenceProvider> getJpaProvider();
    public boolean shouldTryToReconnect(@NonNull RuntimeException e);

    public static ProviderAdapter findFor(@NonNull EntityManager em) {
        return HibernateAdapter.INSTANCE;
    }
}
