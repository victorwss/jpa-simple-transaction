package ninja.javahacker.jpasimpletransactions;

import java.sql.Connection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.LockTimeoutException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.PessimisticLockException;
import javax.persistence.TransactionRequiredException;
import javax.persistence.criteria.CriteriaQuery;
import lombok.NonNull;

/**
 * Extends the {@link EntityManager} interface adding several useful methods into it.
 * @author Victor Williams Stafusa da Silva
 */
public interface ExtendedEntityManager extends EntityManager, AutoCloseable, PersistenceUnitUtil {

    /**
     * Tells if the given object already have a defined identity or not.
     * @param entity Instance whose load state is a new entity.
     * @return {@code true} if the given object is new, {@code false} if it isn't.
     * @throws IllegalArgumentException If the argument is {@code null}.
     */
    public default boolean isNew(@NonNull Object entity) {
        return getIdentifier(entity) == null;
    }

    /**
     * {@inheritDoc}
     * @param entity {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException If the argument is {@code null}.
     */
    @Override
    public default Object getIdentifier(@NonNull Object entity) {
        return getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
    }

    /**
     * {@inheritDoc}
     * @param entity {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException If the argument is {@code null}.
     */
    @Override
    public default boolean isLoaded(@NonNull Object entity) {
        return getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(entity);
    }

    /**
     * {@inheritDoc}
     * @param entity {@inheritDoc}
     * @param attributeName {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException If either argument is {@code null}.
     */
    @Override
    public default boolean isLoaded(@NonNull Object entity, @NonNull String attributeName) {
        return getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(entity, attributeName);
    }

    /**
     * Save a given object in the database regardless the fact of it being a new entity or an existing one.
     * This will only insert it in the database if it is a new entity.
     * @param <T> The type of the entity to save.
     * @param entity The entity to save.
     * @return The saved instance.
     * @throws IllegalArgumentException If the argument is {@code null}.
     */
    public default <T> T save(@NonNull T entity) {
        if (!isNew(entity)) {
            T other = merge(entity);
            if (entity != other) refresh(entity);
        } else if (!contains(entity)) {
            persist(entity);
        } else {
            // Do nothing.
        }
        return entity;
    }

    /*public static ExtendedEntityManager wrap(@NonNull EntityManager em) {
        return em instanceof SpecialEntityManager
                ? (ExtendedEntityManager) em
                : new SpecialEntityManager(em, ProviderAdapter.findFor(em));
    }*/

    /**
     * Unwraps an {@link EntityManager} that has been decorated as an {@code ExtendedEntityManager}.
     * @param em The {@link EntityManager} to undecorate.
     * @return The undecorated {@link EntityManager} or {@code em} as is if not recognized as decorated.
     * @throws IllegalArgumentException If {@code em} is {@code null}.
     */
    @SuppressWarnings("checkstyle:javadocmethod")
    public static EntityManager unwrap(@NonNull EntityManager em) {
        EntityManager r = em instanceof SpecialEntityManager ? ((SpecialEntityManager) em).getWrapped() : em;
        if (r instanceof SpecialEntityManager) throw new AssertionError();
        return r;
    }

    /**
     * Find by primary key. Search for an entity of the specified class and primary key.
     * If the entity instance is contained in the persistence context, it is returned from there wrapped into an {@link Optional}.
     * @param <T> The type of the entity class.
     * @param entityClass The entity class.
     * @param primaryKey The primary key.
     * @return An {@link Optional} containing the found entity instance or an empty one if the entity does not exist.
     */
    public default <T> Optional<T> findOptional(Class<T> entityClass, Object primaryKey) {
        return Optional.ofNullable(find(entityClass, primaryKey));
    }

    /**
     * Find by primary key and lock. Search for an entity of the specified class and primary key and lock it with respect
     * to the specified lock type. If the entity instance is contained in the persistence context, it is returned from there,
     * and the effect of this method is the same as if the lock method had been called on the entity.
     * <p>If the entity is found within the persistence context and the lock mode type is pessimistic and the entity has a
     * version attribute, the persistence provider must perform optimistic version checks when obtaining the database lock.
     * If these checks fail, the {@link OptimisticLockException} will be thrown.</p>
     * <p>The lock mode type is pessimistic and the entity instance is found but cannot be locked:</p>
     * <ul>
     * <li>The PessimisticLockException will be thrown if the database locking failure causes transaction-level rollback.</li>
     * <li>The LockTimeoutException will be thrown if the database locking failure causes only statement-level rollback.</li>
     * </ul>
     * @param <T> The type of the entity class.
     * @param entityClass The entity class.
     * @param primaryKey The primary key.
     * @param lockMode The lock mode.
     * @return An {@link Optional} containing the found entity instance or an empty one if the entity does not exist.
     * @throws IllegalArgumentException If the first argument does not denote an entity type or the second argument is not a valid type for
     *     that entity's primary key or is {@code null}.
     * @throws TransactionRequiredException If there is no transaction and a lock mode other than NONE is specified or if invoked on an
     *     entity manager which has not been joined to the current transaction and a lock mode other than NONE is specified.
     * @throws OptimisticLockException If the optimistic version check fails.
     * @throws PessimisticLockException If pessimistic locking fails and the transaction is rolled back.
     * @throws LockTimeoutException If pessimistic locking fails and only the statement is rolled back.
     * @throws PersistenceException If an unsupported lock call is made.
     */
    public default <T> Optional<T> findOptional(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
        return Optional.ofNullable(find(entityClass, primaryKey, lockMode));
    }

    /**
     * Find by primary key, using the specified properties. Search for an entity of the specified class and primary key.
     * If the entity instance is contained in the persistence context, it is returned from there wrapped into an {@link Optional}.
     * If a vendor-specific property or hint is not recognized, it is silently ignored.
     * @param <T> The type of the entity class.
     * @param entityClass The entity class.
     * @param primaryKey The primary key.
     * @param properties Standard and vendor-specific properties and hints.
     * @return An {@link Optional} containing the found entity instance or an empty one if the entity does not exist.
     */
    public default <T> Optional<T> findOptional(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
        return Optional.ofNullable(find(entityClass, primaryKey, properties));
    }

    /**
     * Find by primary key and lock. Search for an entity of the specified class and primary key and lock it with respect
     * to the specified lock type. If the entity instance is contained in the persistence context, it is returned from there,
     * and the effect of this method is the same as if the lock method had been called on the entity.
     * <p>If the entity is found within the persistence context and the lock mode type is pessimistic and the entity has a
     * version attribute, the persistence provider must perform optimistic version checks when obtaining the database lock.
     * If these checks fail, the {@link OptimisticLockException} will be thrown.</p>
     * <p>The lock mode type is pessimistic and the entity instance is found but cannot be locked:</p>
     * <ul>
     * <li>The {@link PessimisticLockException} will be thrown if the database locking failure causes transaction-level rollback.</li>
     * <li>The {@link LockTimeoutException} will be thrown if the database locking failure causes only statement-level rollback.</li>
     * </ul>
     * <p>If a vendor-specific property or hint is not recognized, it is silently ignored.</p>
     * <p>Portable applications should not rely on the standard timeout hint.
     * Depending on the database in use and the locking mechanisms used by the provider, the hint may or may not be observed.</p>
     * @param <T> The type of the entity class.
     * @param entityClass The entity class.
     * @param primaryKey The primary key.
     * @param lockMode The lock mode.
     * @param properties Standard and vendor-specific properties and hints.
     * @return An {@link Optional} containing the found entity instance or an empty one if the entity does not exist.
     * @throws IllegalArgumentException If the first argument does not denote an entity type or the second argument is not a valid type for
     *     that entity's primary key or is {@code null}.
     * @throws TransactionRequiredException If there is no transaction and a lock mode other than NONE is specified or if invoked on an
     *     entity manager which has not been joined to the current transaction and a lock mode other than NONE is specified.
     * @throws OptimisticLockException If the optimistic version check fails.
     * @throws PessimisticLockException If pessimistic locking fails and the transaction is rolled back.
     * @throws LockTimeoutException If pessimistic locking fails and only the statement is rolled back.
     * @throws PersistenceException If an unsupported lock call is made.
     */
    public default <T> Optional<T> findOptional(
            Class<T> entityClass,
            Object primaryKey,
            LockModeType lockMode,
            Map<String, Object> properties)
    {
        return Optional.ofNullable(find(entityClass, primaryKey, lockMode, properties));
    }

    /**
     * {@inheritDoc}
     * @param qlString {@inheritDoc}
     * @param resultClass {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public <T> ExtendedTypedQuery<T> createNamedQuery(String qlString, Class<T> resultClass);

    /**
     * Create a query selecting all the entities typed as {@code resultClass} ordered by the {@code orders} criterions.
     * @param <T> The type of the entity to be queried.
     * @param resultClass The entity type of the result.
     * @param orders Ordering criteria for the results.
     * @return {@code this}.
     */
    public default <T> ExtendedTypedQuery<T> createQuery(@NonNull Class<T> resultClass, @NonNull By... orders) {
        return this.createQuery(resultClass, Collections.emptyMap(), orders);
    }

    /**
     * {@inheritDoc}
     * @param cq {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public <T> ExtendedTypedQuery<T> createQuery(CriteriaQuery<T> cq);

    /**
     * {@inheritDoc}
     * @param qlString {@inheritDoc}
     * @param resultClass {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public <T> ExtendedTypedQuery<T> createQuery(String qlString, Class<T> resultClass);

    /**
     * Create a query selecting all the entities typed as {@code resultClass}, where their fields match the ones
     * given in the {@code where} map and ordered by the {@code orders} criterions.
     * @param <T> The type of the entity to be queried.
     * @param resultClass The entity type of the result.
     * @param where A map relating fields to their expected values.
     * @param orders Ordering criteria for the results.
     * @return {@code this}.
     */
    public default <T> ExtendedTypedQuery<T> createQuery(
            @NonNull Class<T> resultClass,
            @NonNull Map<String, Object> where,
            @NonNull By... orders)
    {
        StringBuilder jpql = new StringBuilder("SELECT c FROM ").append(resultClass.getName()).append(" c");
        if (!where.isEmpty()) {
            jpql.append(" WHERE ");
            StringJoiner sj = new StringJoiner(" AND ");
            where.keySet().stream().map(k -> "c." + k + " = :" + k).forEach(sj::add);
            jpql.append(sj);
        }
        if (orders.length > 0) {
            jpql.append(" ORDER BY ");
            StringJoiner sj = new StringJoiner(", ");
            Stream.of(orders).map(k -> "c." + k.getField() + (k.isDescending() ? " DESC" : "")).forEach(sj::add);
            jpql.append(sj);
        }
        ExtendedTypedQuery<T> query = this.createQuery(jpql.toString(), resultClass);
        where.forEach(query::setParameter);
        return query;
    }

    /**
     * Obtains the {@link Connection} used by this {@code EntityManager}.
     * @return The {@link Connection} used by this {@code EntityManager}.
     */
    public Connection getConnection();
}
