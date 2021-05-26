package ninja.javahacker.jpasimpletransactions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * Class responsible for managing the lifecycle of an {@link EntityManagerFactory}
 * and its {@link SpecialEntityManager}s and also automatically marking transaction
 * scopes.
 *
 * <p>Each instance of this class is responsible for managing a single instance of
 * a {@link EntityManagerFactory}. If your application should handle several
 * persistence units at once, you should create several instances of this class,
 * one for each for each persistence unit.</p>
 *
 * @author Victor Williams Stafusa da Silva
 */
public class Connector implements AutoCloseable {

    /**
     * The name of the persistence unit used by this {@code Connector}.
     * -- GETTER --
     * @return The name of the persistence unit used by this {@code Connector}.
     */
    @Getter
    private final String persistenceUnitName;

    /**
     * The {@link EntityManagerFactory} used for creating {@link EntityManager}s within this {@code Connector}.
     * -- GETTER --
     * @return The {@link EntityManagerFactory} used for creating {@link EntityManager}s within this {@code Connector}.
     */
    @Getter
    private final EntityManagerFactory entityManagerFactory;

    private final ThreadLocal<SpecialEntityManager> managers;

    /**
     * The persistence provider used by this {@code Connector}.
     * -- GETTER --
     * @return The persistence provider used by this {@code Connector}.
     */
    @Getter
    private final ProviderAdapter adapter;

    private Connector(
            @NonNull String persistenceUnitName,
            @NonNull EntityManagerFactory emf,
            @NonNull ProviderAdapter adapter)
    {
        this.persistenceUnitName = persistenceUnitName;
        this.entityManagerFactory = emf;
        this.managers = new ThreadLocal<>();
        this.adapter = adapter;
    }

    /**
     * Creates a connector which wraps a given {@link EntityManagerFactory} with a given persistence provider (wrapped by the
     * {@link ProviderAdapter} with the specified name of a persistence unit.
     * @param persistenceUnitName The name of the persistence unit.
     * @param emf The {@link EntityManagerFactory} responsible for creating {@link EntityManager}s.
     * @param adapter The persistence provider wrapped into a {@link ProviderAdapter}.
     * @return An instance of this class.
     * @throws IllegalArgumentException If any parameter is {@code null}.
     */
    public static Connector create(
            @NonNull String persistenceUnitName,
            @NonNull EntityManagerFactory emf,
            @NonNull ProviderAdapter adapter)
    {
        return new Connector(persistenceUnitName, emf, adapter);
        /*var con = new Connector(persistenceUnitName, emf, adapter);
        Database.addConnector(con, false);
        return con;*/
    }

    /**
     * Obtains the {@link EntityManager} (or more precisely, an {@link ExtendedEntityManager}) that is responsible for the persistence
     * within the active transaction in the current thread.
     * @return The {@link ExtendedEntityManager} responsible for the persistence  within the active transaction in the current thread.
     * @throws IllegalStateException If there is no active transaction.
     */
    public ExtendedEntityManager getEntityManager() {
        ExtendedEntityManager em = managers.get();
        if (em == null) throw new IllegalStateException("Can't get the EntityManager outside of a transaction.");
        return em;
    }

    private SpecialEntityManager createNewEntityManager() {
        var em = new SpecialEntityManager(adapter, persistenceUnitName, entityManagerFactory);
        managers.set(em);
        return em;
    }

    /**
     * Given an interface {@code iface} of type {@code <A>} and an implementation called {@code impl}, returns a new implementation
     * that wraps the given one by adding a transaction context on each of its methods. This transactional context is reentrant, so
     * nested calls of the methods don't create additional contexts. The persistence context of the transactions is provided by the
     * {@link #getEntityManager()} method.
     * @param <A> The type of the interface to be wrapped.
     * @param iface The actual class object representing the interface to be wrapped.
     * @param impl The implementation to be wrapped.
     * @return The wrapped implementation.
     * @throws IllegalArgumentException If any parameter is {@code null}.
     */
    public <A> A transact(@NonNull Class<A> iface, @NonNull A impl) {
        if (!iface.isInterface()) throw new IllegalArgumentException();
        InvocationHandler ih = (p, m, args) -> execute(() -> m.invoke(impl, args));
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        return iface.cast(Proxy.newProxyInstance(ccl, new Class<?>[] {iface}, ih));
    }

    /**
     * Given an interface {@code iface} of type {@code <A>} and an implementation called {@code impl}, returns a new implementation
     * that wraps the given one by adding a transaction context on each of its methods. This transactional context is reentrant, so
     * nested calls of the methods don't create additional contexts. The persistence context of the transactions is provided by the
     * {@link #getEntityManager()} method.
     * @param <A> The type of the interface to be wrapped.
     * @param type The generic type containing the {@code iface} class object representing the interface to be wrapped.
     * @param impl The implementation to be wrapped.
     * @return The wrapped implementation.
     * @throws IllegalArgumentException If any parameter is {@code null}.
     */
    public <A> A transact(@NonNull ReifiedGeneric<A> type, @NonNull A impl) {
        return transact(type.asClass(), impl);
    }

    /**
     * Used as a {@link Supplier} that declares that any {@link Throwable} might
     * be thrown without needing wrapping and unwrapping.
     * @param <E> The type of the supplied object.
     * @see #execute(XSupplier)
     */
    private static interface XSupplier<E> {
        public E get() throws InvocationTargetException, IllegalAccessException;

        public default E getOrRethrow() throws Throwable {
            try {
                return get();
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }
    }

    /**
     * Executes the given lambda inside a transaction context.
     * @param trans The lambda to execute inside the transaction context.
     * @throws IllegalArgumentException If {@code trans} is {@code null}.
     * @throws Throwable Whatever is thrown by the lambda. Forces a rollback in the transaction.
     */
    @SuppressFBWarnings(
            value = "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE",
            justification = "try-with-resources - It's either SpotBugs fault or javac fault, but definitely not our fault."
    )
    @SuppressWarnings({"PMD.CloseResource"})
    private <E> E execute(@NonNull XSupplier<E> trans) throws Throwable {
        SpecialEntityManager alreadyExists = managers.get();
        if (alreadyExists != null) return trans.get();

        boolean ok = false;
        try (SpecialEntityManager actual = createNewEntityManager()) {
            Database.getListener().connectorStarted(persistenceUnitName);
            managers.set(actual);
            EntityTransaction et = actual.getTransaction();
            try {
                et.begin();
                E result = trans.getOrRethrow();
                ok = true;
                return result;
            } finally {
                if (ok) {
                    et.commit();
                    Database.getListener().finishedWithCommit(persistenceUnitName);
                } else {
                    et.rollback();
                    Database.getListener().finishedWithRollback(persistenceUnitName);
                }
            }
        } finally {
            managers.remove();
            Database.getListener().connectorClosed(persistenceUnitName);
        }
    }

    /**
     * Closes the {@code Connector} and its subjacent {@link EntityManagerFactory}.
     */
    @Override
    public void close() {
        entityManagerFactory.close();
    }
}
