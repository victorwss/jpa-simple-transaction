package ninja.javahacker.jpasimpletransactions;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.Supplier;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.xjfunctions.XConsumer;
import ninja.javahacker.xjfunctions.XFunction;
import ninja.javahacker.xjfunctions.XLongFunction;
import ninja.javahacker.xjfunctions.XRunnable;
import ninja.javahacker.xjfunctions.XSupplier;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class Connector implements AutoCloseable {

    @Getter
    private final String persistenceUnitName;

    private final EntityManagerFactory emf;

    private final ThreadLocal<ExtendedEntityManager> managers;

    /*package*/ Connector(@NonNull String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
        this.emf = Persistence.createEntityManagerFactory(persistenceUnitName);
        this.managers = new ThreadLocal<>();
    }

    public ExtendedEntityManager getEntityManager() {
        ExtendedEntityManager em = managers.get();
        if (em == null) throw new IllegalStateException();
        return em;
    }

    private ExtendedEntityManager createNewEntityManager() {
        ExtendedEntityManager em = ExtendedEntityManager.wrap(emf.createEntityManager());
        managers.set(em);
        return em;
    }

    public <A, B> Function<A, B> transactFunction(@NonNull Function<A, B> trans) {
        return transactXFunction(XFunction.wrap(trans)).unchecked();
    }

    public <A, B> XFunction<A, B> transactXFunction(@NonNull XFunction<A, B> trans) {
        return in -> execute(() -> trans.apply(in));
    }

    public <B> LongFunction<B> transactLongFunction(@NonNull LongFunction<B> trans) {
        return transactXLongFunction(XLongFunction.wrap(trans)).unchecked();
    }

    public <B> XLongFunction<B> transactXLongFunction(@NonNull XLongFunction<B> trans) {
        return in -> execute(() -> trans.apply(in));
    }

    public <E> Consumer<E> transactConsumer(@NonNull Consumer<E> trans) {
        return transactXConsumer(XConsumer.wrap(trans)).unchecked();
    }

    public <E> XConsumer<E> transactXConsumer(@NonNull XConsumer<E> trans) {
        return in -> {
            execute(() -> {
                trans.accept(in);
                return this;
            });
        };
    }

    public Runnable transactRunnable(@NonNull Runnable trans) {
        return transactXRunnable(XRunnable.wrap(trans)).unchecked();
    }

    public XRunnable transactXRunnable(@NonNull XRunnable trans) {
        return () -> {
            execute(() -> {
                trans.run();
                return this;
            });
        };
    }

    public <E> Supplier<E> transactSupplier(@NonNull Supplier<E> trans) {
        return transactXSupplier(XSupplier.wrap(trans)).unchecked();
    }

    public <E> XSupplier<E> transactXSupplier(@NonNull XSupplier<E> trans) {
        return () -> execute(trans);
    }

    public <E> E execute(@NonNull XSupplier<E> trans) throws Throwable {
        ExtendedEntityManager jaExistente = managers.get();
        ExtendedEntityManager atual = jaExistente == null ? createNewEntityManager() : jaExistente;
        boolean ok = false;
        try {
            if (jaExistente == null) {
                managers.set(atual);
                try {
                    atual.getTransaction().begin();
                } catch (RuntimeException e) {
                    if (!e.getClass().getName().equals("org.hibernate.exception.JDBCConnectionException")
                            || !e.getMessage().equals("Unable to acquire JDBC Connection"))
                    {
                        throw e;
                    }
                    Database.getListener().renewedConnection(this);
                    atual.close();
                    atual = createNewEntityManager();
                    managers.set(atual);
                    atual.getTransaction().begin();
                }
                Database.getListener().startedTransaction(this);
            }
            E resultado = trans.get();
            ok = true;
            return resultado;
        } finally {
            if (jaExistente == null) {
                try {
                    if (ok) {
                        atual.getTransaction().commit();
                        Database.getListener().finishedWithCommit(this);
                    } else {
                        atual.getTransaction().rollback();
                        Database.getListener().finishedWithRollback(this);
                    }
                } finally {
                    atual.clear();
                    atual.close();
                    managers.remove();
                }
            }
        }
    }

    @Override
    public void close() {
        emf.close();
    }
}
