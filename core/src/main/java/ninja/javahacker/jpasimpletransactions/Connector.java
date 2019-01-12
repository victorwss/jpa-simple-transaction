package ninja.javahacker.jpasimpletransactions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * Class responsible for managing the lifecycle of an {@link EntityManagerFactory}
 * and its {@link SpecialEntityManager}s and also automatically demarking transaction
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

    @Getter
    private final String persistenceUnitName;

    @Getter
    private final EntityManagerFactory entityManagerFactory;

    private final ThreadLocal<SpecialEntityManager> managers;

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

    public <A> A transact(@NonNull Class<A> type, @NonNull A operation) {
        if (!type.isInterface()) throw new IllegalArgumentException();
        InvocationHandler ih = (p, m, args) -> execute(() -> m.invoke(operation, args));
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        return type.cast(Proxy.newProxyInstance(ccl, new Class<?>[] {type}, ih));
    }

    public <A> A transact(@NonNull ReifiedGeneric<A> type, @NonNull A operation) {
        return transact(type.raw(), operation);
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

    @SuppressFBWarnings(
            value = "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE",
            justification = "try-with-resources - It's either SpotBugs fault or javac fault, but definitely not our fault."
    )
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

    @Override
    public void close() {
        entityManagerFactory.close();
    }
}
