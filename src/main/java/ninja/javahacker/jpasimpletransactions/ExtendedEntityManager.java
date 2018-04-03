package ninja.javahacker.jpasimpletransactions;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.criteria.CriteriaQuery;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
public interface ExtendedEntityManager extends EntityManager {

    public default boolean isNew(@NonNull Object obj) {
        return getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(obj) == null;
    }

    public default <T> T save(@NonNull T obj) {
        if (!isNew(obj)) {
            T obj2 = merge(obj);
            if (obj != obj2) refresh(obj);
        } else if (!contains(obj)) {
            persist(obj);
        }
        return obj;
    }

    public static ExtendedEntityManager wrap(@NonNull EntityManager em) {
        return em instanceof ExtendedEntityManager ? (ExtendedEntityManager) em : new SpecialEntityManager(em);
    }

    public default <E> List<E> listAll(@NonNull Class<E> type) {
        return this.createQuery("SELECT c FROM " + type.getName(), type).getResultList();
    }

    public default <E> Optional<E> findOptional(Class<E> type, Object id) {
        return Optional.ofNullable(find(type, id));
    }

    public default <E> Optional<E> findOptional(Class<E> type, Object id, LockModeType lmt) {
        return Optional.ofNullable(find(type, id, lmt));
    }

    public default <E> Optional<E> findOptional(Class<E> type, Object id, Map<String, Object> map) {
        return Optional.ofNullable(find(type, id, map));
    }

    public default <E> Optional<E> findOptional(Class<E> type, Object id, LockModeType lmt, Map<String, Object> map) {
        return Optional.ofNullable(find(type, id, lmt, map));
    }

    @Override
    public <T extends Object> ExtendedTypedQuery<T> createQuery(CriteriaQuery<T> cq);

    @Override
    public <T extends Object> ExtendedTypedQuery<T> createQuery(String string, Class<T> type);

    @Override
    public <T extends Object> ExtendedTypedQuery<T> createNamedQuery(String string, Class<T> type);
}
