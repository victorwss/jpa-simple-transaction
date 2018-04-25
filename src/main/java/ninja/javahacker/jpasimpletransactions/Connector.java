package ninja.javahacker.jpasimpletransactions;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class Connector implements AutoCloseable {

    @Getter
    private final String persistenceUnitName;

    private final EntityManagerFactory emf;

    private final ThreadLocal<ExtendedEntityManager> managers;

    public Connector(@NonNull String persistenceUnitName, @NonNull EntityManagerFactory emf) {
        this.persistenceUnitName = persistenceUnitName;
        this.emf = emf;
        this.managers = new ThreadLocal<>();
    }

    public static Connector withPersistenceXml(@NonNull String persistenceUnitName) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName);
        return new Connector(persistenceUnitName, emf);
    }

    public static Connector withPersistenceXml(@NonNull String persistenceUnitName, @NonNull Map<String, String> properties) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName, properties);
        return new Connector(persistenceUnitName, emf);
    }

    public static Connector withPersistenceXml(@NonNull String persistenceUnitName, @NonNull PersistenceProperties properties) {
        return withPersistenceXml(persistenceUnitName, properties.build());
    }

    public static Connector withoutXml(
            @NonNull String persistenceUnitName,
            @NonNull Collection<Class<?>> classes,
            @NonNull Map<String, String> properties)
    {
        SimplePersistenceUnitInfo sui = new SimplePersistenceUnitInfo(persistenceUnitName, classes, properties);
        EntityManagerFactory emf = SimplePersistenceUnitInfo
                .makeProvider()
                .createContainerEntityManagerFactory(sui, properties);
        return new Connector(persistenceUnitName, emf);
    }

    public static Connector withoutXml(
            @NonNull String persistenceUnitName,
            @NonNull Collection<Class<?>> classes,
            @NonNull PersistenceProperties properties)
    {
        return Connector.withoutXml(persistenceUnitName, classes, properties.build());
    }

    public ExtendedEntityManager getEntityManager() {
        ExtendedEntityManager em = managers.get();
        if (em == null) throw new IllegalStateException();
        return em;
    }

    private ExtendedEntityManager createNewEntityManager() {
        ExtendedEntityManager em = ExtendedEntityManager.wrap(emf.createEntityManager());
        managers.set(em);
        Database.getListener().connectorStarted(persistenceUnitName);
        return em;
    }

    public <A> A transact(@NonNull Class<A> type, @NonNull A operation) {
        return transact(ReifiedGeneric.forClass(type), operation);
    }

    @SuppressWarnings("unchecked")
    public <A> A transact(@NonNull ReifiedGeneric<A> type, @NonNull A operation) {
        Class<?> k = type.raw();
        if (!k.isInterface()) throw new IllegalArgumentException();
        return (A) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[] {k},
                (p, m, args) -> execute(() -> m.invoke(operation, args)));
    }

    private static interface XSupplier<E> {
        public E get() throws Throwable;
    }

    private <E> E execute(@NonNull XSupplier<E> trans) throws Throwable {
        ExtendedEntityManager alreadyExists = managers.get();
        ExtendedEntityManager actual = alreadyExists == null ? createNewEntityManager() : alreadyExists;
        boolean ok = false;
        try {
            if (alreadyExists == null) {
                managers.set(actual);
                try {
                    actual.getTransaction().begin();
                } catch (RuntimeException e) {
                    if (!shouldTryToReconnect(e)) throw e;
                    Database.getListener().renewedConnection(persistenceUnitName);
                    actual.close();
                    actual = createNewEntityManager();
                    managers.set(actual);
                    actual.getTransaction().begin();
                }
                Database.getListener().startedTransaction(persistenceUnitName);
            }
            E result = trans.get();
            ok = true;
            return result;
        } finally {
            if (alreadyExists == null) {
                try {
                    if (ok) {
                        actual.getTransaction().commit();
                        Database.getListener().finishedWithCommit(persistenceUnitName);
                    } else {
                        actual.getTransaction().rollback();
                        Database.getListener().finishedWithRollback(persistenceUnitName);
                    }
                } finally {
                    actual.clear();
                    actual.close();
                    managers.remove();
                    Database.getListener().connectorClosed(persistenceUnitName);
                }
            }
        }
    }

    private boolean shouldTryToReconnect(RuntimeException e) {
        return e.getClass().getName().equals("org.hibernate.exception.JDBCConnectionException")
                && e.getMessage().equals("Unable to acquire JDBC Connection");
    }

    @Override
    public void close() {
        emf.close();
    }
}
