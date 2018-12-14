package ninja.javahacker.jpasimpletransactions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import lombok.Getter;
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

    @Getter
    @Delegate(types = EntityManager.class, excludes = DoNotDelegateEntityManager.class)
    private EntityManager wrapped;

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
        if (this.wrapped != null) this.wrapped.close();
        this.wrapped = ExtendedEntityManager.unwrap(em);
    }

    @Override
    public void remove(Object obj) {
        if (obj != null && !isNew(obj)) wrapped.remove(obj);
    }

    @Override
    public <T extends Object> ExtendedTypedQuery<T> createQuery(CriteriaQuery<T> cq) {
        return ExtendedTypedQuery.wrap(wrapped.createQuery(cq));
    }

    @Override
    @SuppressFBWarnings(
            value = "SQL_INJECTION_JPA",
            justification = "False alarm, we're just delegating it untouched."
    )
    public <T extends Object> ExtendedTypedQuery<T> createQuery(String string, Class<T> type) {
        return ExtendedTypedQuery.wrap(wrapped.createQuery(string, type));
    }

    @Override
    public <T extends Object> ExtendedTypedQuery<T> createNamedQuery(String string, Class<T> type) {
        return ExtendedTypedQuery.wrap(wrapped.createNamedQuery(string, type));
    }

    @Override
    public Connection getConnection() {
        return adapter.getConnection(wrapped);
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
