package ninja.javahacker.jpasimpletransactions;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Optional;
import java.util.function.Function;
import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class HibernateAdapter implements ProviderAdapter {

    public static final HibernateAdapter INSTANCE = new HibernateAdapter();

    private static final Optional<Class<? extends PersistenceProvider>> PROVIDER_CLASS = getProviderClass();

    private static final Optional<Class<? extends EntityManager>> SESSION_CLASS = sessionClass();

    private static final Function<EntityManager, Connection> IMPL = work();

    private static Optional<Class<? extends EntityManager>> sessionClass() {
        try {
            return Optional.of(Class.forName("org.hibernate.Session").asSubclass(EntityManager.class));
        } catch (ClassNotFoundException x) {
            return Optional.empty();
        }
    }

    private static Function<EntityManager, Connection> work() {
        Optional<Function<EntityManager, Connection>> temp = SESSION_CLASS.map(sessionClass -> {
            try {
                Class<?> returningWorkClass = Class.forName("org.hibernate.jdbc.ReturningWork");
                Method doReturningWork = sessionClass.getMethod("doReturningWork", returningWorkClass);
                InvocationHandler ih = (p, m, a) -> a[0];
                Object returningWork = Proxy.newProxyInstance(
                        Thread.currentThread().getContextClassLoader(),
                        new Class<?>[] { returningWorkClass },
                        ih);
                return em -> work(em, doReturningWork, returningWork);
            } catch (ClassNotFoundException | NoSuchMethodException x) {
                throw new AssertionError(x);
            }
        });
        return temp.orElse(em -> {
            throw new UnsupportedOperationException();
        });
    }

    private static Optional<Class<? extends PersistenceProvider>> getProviderClass() {
        try {
            return Optional.of(Class.forName("org.hibernate.jpa.HibernatePersistenceProvider")
                    .asSubclass(PersistenceProvider.class));
        } catch (ClassNotFoundException x) {
            return Optional.empty();
        }
    }

    private static Connection work(
            EntityManager em,
            Method doReturningWork,
            Object returningWork)
    {
        try {
            return (Connection) doReturningWork.invoke(em.unwrap(SESSION_CLASS.get()), returningWork);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError(e);
        }
    }

    private HibernateAdapter() {
    }

    @Override
    public boolean recognizes(@NonNull EntityManager em) {
        return SESSION_CLASS.orElseThrow(UnsupportedOperationException::new).isInstance(em);
    }

    @Override
    public Connection getConnection(@NonNull EntityManager em) {
        return IMPL.apply(em);
    }

    @Override
    public Class<? extends PersistenceProvider> getJpaProvider() {
        return PROVIDER_CLASS.orElseThrow(UnsupportedOperationException::new);
    }

    @Override
    public boolean shouldTryToReconnect(@NonNull RuntimeException e) {
        return "org.hibernate.exception.JDBCConnectionException".equals(e.getClass().getName())
                && "Unable to acquire JDBC Connection".equals(e.getMessage());
    }
}