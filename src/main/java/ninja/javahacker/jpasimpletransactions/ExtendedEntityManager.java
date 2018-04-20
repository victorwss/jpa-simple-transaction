package ninja.javahacker.jpasimpletransactions;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Stream;
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

    public default <T> Optional<T> findOptional(Class<T> type, Object id) {
        return Optional.ofNullable(find(type, id));
    }

    public default <T> Optional<T> findOptional(Class<T> type, Object id, LockModeType lmt) {
        return Optional.ofNullable(find(type, id, lmt));
    }

    public default <T> Optional<T> findOptional(Class<T> type, Object id, Map<String, Object> map) {
        return Optional.ofNullable(find(type, id, map));
    }

    public default <T> Optional<T> findOptional(Class<T> type, Object id, LockModeType lmt, Map<String, Object> map) {
        return Optional.ofNullable(find(type, id, lmt, map));
    }

    public default <T> ExtendedTypedQuery<T> createQuery(@NonNull Class<T> type, @NonNull OrderBy... orders) {
        return this.createQuery(type, Collections.emptyMap(), orders);
    }

    @Override
    public <T> ExtendedTypedQuery<T> createQuery(CriteriaQuery<T> cq);

    @Override
    public <T> ExtendedTypedQuery<T> createQuery(String string, Class<T> type);

    public default <T> ExtendedTypedQuery<T> createQuery(
            @NonNull Class<T> type,
            @NonNull Map<String, Object> map,
            @NonNull OrderBy... orders)
    {
        StringBuilder jpql = new StringBuilder("SELECT c FROM ").append(type.getName()).append(" c");
        if (!map.isEmpty()) {
            jpql.append(" WHERE ");
            StringJoiner sj = new StringJoiner(" AND ");
            map.keySet().stream().map(k -> "c." + k + " = :" + k).forEach(sj::add);
            jpql.append(sj.toString());
        }
        if (orders.length > 0) {
            jpql.append(" ORDER BY ");
            StringJoiner sj = new StringJoiner(", ");
            Stream.of(orders).map(k -> "c." + k.getField() + (k.isDesc() ? " DESC" : "")).forEach(sj::add);
            jpql.append(sj.toString());
        }
        ExtendedTypedQuery<T> query = this.createQuery(jpql.toString(), type);
        map.forEach(query::setParameter);
        return query;
    }

    @Override
    public <T> ExtendedTypedQuery<T> createNamedQuery(String string, Class<T> type);

    public static OrderBy orderBy(@NonNull String field) {
        return new OrderBy(field, false);
    }

    public static OrderBy orderByDesc(@NonNull String field) {
        return new OrderBy(field, true);
    }
}
