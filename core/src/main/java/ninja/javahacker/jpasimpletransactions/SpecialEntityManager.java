package ninja.javahacker.jpasimpletransactions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import lombok.NonNull;
import lombok.experimental.Delegate;
import lombok.experimental.PackagePrivate;

/**
 * Implementation of the {@link ExtendedEntityManager} interface that
 * delegates to some other {@link EntityManager}.
 * @author Victor Williams Stafusa da Silva
 */
@SuppressWarnings("rawtypes")
@PackagePrivate
final class SpecialEntityManager implements ExtendedEntityManager {

    @Delegate(types = EntityManager.class, excludes = DoNotDelegateEntityManager.class)
    private EntityManager delegate;

    private final ProviderAdapter adapter;

    public SpecialEntityManager(
            @NonNull EntityManager em,
            @NonNull ProviderAdapter adapter)
    {
        this.adapter = adapter;
        replace(em);
    }

    @PackagePrivate
    void replace(@NonNull EntityManager em) {
        if (this.delegate != null) this.delegate.close();
        this.delegate = getRoot(em);
    }

    private static EntityManager getRoot(EntityManager em) {
        EntityManager r = em instanceof SpecialEntityManager ? ((SpecialEntityManager) em).delegate : em;
        if (r instanceof SpecialEntityManager) throw new AssertionError();
        return r;
    }

    @Override
    public void remove(Object obj) {
        if (obj != null && !isNew(obj)) delegate.remove(obj);
    }

    @Override
    public <T extends Object> ExtendedTypedQuery<T> createQuery(CriteriaQuery<T> cq) {
        return ExtendedTypedQuery.wrap(delegate.createQuery(cq));
    }

    @Override
    @SuppressFBWarnings(
            value = "SQL_INJECTION_JPA",
            justification = "False alarm, we're just delegating it untouched."
    )
    public <T extends Object> ExtendedTypedQuery<T> createQuery(String string, Class<T> type) {
        return ExtendedTypedQuery.wrap(delegate.createQuery(string, type));
    }

    @Override
    public <T extends Object> ExtendedTypedQuery<T> createNamedQuery(String string, Class<T> type) {
        return ExtendedTypedQuery.wrap(delegate.createNamedQuery(string, type));
    }

    @Override
    public Connection getConnection() {
        return adapter.getConnection(delegate);
    }

    public ProviderAdapter getProviderAdapter() {
        return adapter;
    }

    /**
     * Exists only to suppress lombok's delegation on a few methods.
     */
    private static interface DoNotDelegateEntityManager {
        public void remove(Object obj);

        public <T extends Object> TypedQuery<T> createQuery(CriteriaQuery<T> cq);

        public <T extends Object> TypedQuery<T> createQuery(String string, Class<T> type);

        public <T extends Object> TypedQuery<T> createNamedQuery(String string, Class<T> type);
    }
}
