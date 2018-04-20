package ninja.javahacker.jpasimpletransactions;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import lombok.NonNull;
import lombok.experimental.Delegate;
import lombok.experimental.PackagePrivate;

/**
 * @author Victor Williams Stafusa da Silva
 */
@SuppressWarnings("rawtypes")
@PackagePrivate
class SpecialEntityManager implements ExtendedEntityManager {
    @Delegate(types = EntityManager.class, excludes = DoNotDelegateEntityManager.class)
    private final EntityManager delegate;

    public SpecialEntityManager(@NonNull EntityManager em) {
        this.delegate = em;
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
    public <T extends Object> ExtendedTypedQuery<T> createQuery(String string, Class<T> type) {
        return ExtendedTypedQuery.wrap(delegate.createQuery(string, type));
    }

    @Override
    public <T extends Object> ExtendedTypedQuery<T> createNamedQuery(String string, Class<T> type) {
        return ExtendedTypedQuery.wrap(delegate.createNamedQuery(string, type));
    }

    private static interface DoNotDelegateEntityManager {
        public void remove(Object obj);

        public <T extends Object> TypedQuery<T> createQuery(CriteriaQuery<T> cq);

        public <T extends Object> TypedQuery<T> createQuery(String string, Class<T> type);

        public <T extends Object> TypedQuery<T> createNamedQuery(String string, Class<T> type);
    }
}
