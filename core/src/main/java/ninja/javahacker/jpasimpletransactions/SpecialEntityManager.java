package ninja.javahacker.jpasimpletransactions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
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

    @NonNull
    private final ProviderAdapter adapter;

    @NonNull
    private final String persistenceUnitName;

    @NonNull
    private final EntityManagerFactory emf;

    @NonNull
    private Optional<SpecialEntityTransaction> trans;

    public SpecialEntityManager(
            @NonNull ProviderAdapter adapter,
            @NonNull String persistenceUnitName,
            @NonNull EntityManagerFactory emf)
    {
        this.persistenceUnitName = persistenceUnitName;
        this.adapter = adapter;
        this.trans = Optional.empty();
        this.emf = emf;
        recreateEntityManager();
    }

    private void recreateEntityManager() {
        if (this.wrapped != null) this.wrapped.close();
        this.wrapped = emf.createEntityManager();
    }

    @Override
    public void remove(Object obj) {
        if (obj != null && !isNew(obj)) wrapped.remove(obj);
    }

    @Override
    public <T extends Object> ExtendedTypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        return ExtendedTypedQuery.wrap(wrapped.createQuery(criteriaQuery));
    }

    @Override
    @SuppressFBWarnings(
            value = "SQL_INJECTION_JPA",
            justification = "False alarm, we're just delegating it untouched."
    )
    public <T extends Object> ExtendedTypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
        return ExtendedTypedQuery.wrap(wrapped.createQuery(qlString, resultClass));
    }

    @Override
    public <T extends Object> ExtendedTypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
        return ExtendedTypedQuery.wrap(wrapped.createNamedQuery(name, resultClass));
    }

    @Override
    public Connection getConnection() {
        return adapter.getConnection(wrapped);
    }

    @Override
    public EntityTransaction getTransaction() {
        var inner = wrapped.getTransaction(); // Relays exceptions.
        if (inner == null) throw new IllegalStateException(); // Should never happen with a sane wrapped EntityManager.

        // Return the cached transaction.
        if (!trans.isEmpty()) {
            var w = trans.get();
            if (w.wrapped == inner) return w;
        }

        // Create a new SpecialEntityTransaction and caches it.
        var t = new SpecialEntityTransaction(this, inner);
        trans = Optional.of(t);
        return t;
    }

    /**
     * Exists only to suppress lombok's delegation on a few methods.
     */
    private static interface DoNotDelegateEntityManager {
        public void remove(Object obj);

        public <T extends Object> TypedQuery<T> createQuery(CriteriaQuery<T> cq);

        public <T extends Object> TypedQuery<T> createQuery(String string, Class<T> type);

        public <T extends Object> TypedQuery<T> createNamedQuery(String string, Class<T> type);

        public EntityTransaction getTransaction();
    }

    /**
     * {@link EntityTransaction} implementation that tries to reconnect at the {@link #begin()} method.
     */
    private static class SpecialEntityTransaction implements EntityTransaction {

        @Delegate(types = EntityTransaction.class, excludes = DoNotDelegateEntityTransaction.class)
        private final EntityTransaction wrapped;

        private final SpecialEntityManager parent;

        public SpecialEntityTransaction(@NonNull SpecialEntityManager parent, @NonNull EntityTransaction wrapped) {
            this.parent = parent;
            this.wrapped = wrapped;
        }

        @Override
        public void begin() {
            try {
                wrapped.begin();
            } catch (RuntimeException e) {
                if (!parent.adapter.shouldTryToReconnect(e)) throw e;
                parent.recreateEntityManager();
                wrapped.begin();
                Database.getListener().renewedConnection(parent.persistenceUnitName);
            }
            Database.getListener().startedTransaction(parent.persistenceUnitName);
        }
    }

    /**
     * Exists only to suppress lombok's delegation on a few methods.
     */
    private static interface DoNotDelegateEntityTransaction {
        public void begin();
    }
}
