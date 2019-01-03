package ninja.javahacker.test.jpasimpletransactions;

import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import ninja.javahacker.jpasimpletransactions.Connector;
import ninja.javahacker.jpasimpletransactions.hibernate.HibernateAdapter;
import ninja.javahacker.mocker.Mocker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ConnectorUnitTest {

    private static String nullMessage(String paramName) {
        return paramName + " is marked as non null but it is null.";
    }

    @Test
    public void testBadConnectorCreate1() throws Exception {
        var emfc = Mocker.mock(EntityManagerFactory.class);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Connector.create(null, emfc.getTarget(), HibernateAdapter.CANONICAL),
                nullMessage("persistenceUnitName"));
    }

    @Test
    public void testBadConnectorCreate2() throws Exception {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Connector.create(null, null, HibernateAdapter.CANONICAL),
                nullMessage("persistenceUnitName"));
    }

    @Test
    public void testBadConnectorCreate3() throws Exception {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Connector.create("foo", null, HibernateAdapter.CANONICAL),
                nullMessage("emf"));
    }

    @Test
    public void testBadConnectorCreate4() throws Exception {
        var emfc = Mocker.mock(EntityManagerFactory.class);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Connector.create("foo", emfc.getTarget(), null),
                nullMessage("adapter"));
    }

    @Test
    public void testBadConnectorCreate5() throws Exception {
        var emfc = Mocker.mock(EntityManagerFactory.class);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Connector.create("foo", null, null),
                nullMessage("emf"));
    }

    @Test
    public void testUnitName() throws Exception {
        var emfc = Mocker.mock(EntityManagerFactory.class);
        var con = Connector.create("mumble", emfc.getTarget(), HibernateAdapter.CANONICAL);
        Assertions.assertEquals(con.getPersistenceUnitName(), "mumble");
    }

    @Test
    public void testClose() throws Exception {
        AtomicInteger a = new AtomicInteger(0);
        var emfc = Mocker.mock(EntityManagerFactory.class);
        emfc.rule().procedure(EntityManagerFactory::close).executes(call -> a.incrementAndGet());
        Connector.create("mumble", emfc.getTarget(), HibernateAdapter.CANONICAL).close();
        Assertions.assertEquals(1, a.get());
    }

    @Test
    public void testBadGetEntityManager() throws Exception {
        var emfc = Mocker.mock(EntityManagerFactory.class);
        Assertions.assertThrows(
                IllegalStateException.class,
                () -> Connector.create("foo", emfc.getTarget(), HibernateAdapter.CANONICAL).getEntityManager(),
                "Can't get the EntityManager outside of a transaction.");
    }

    @Test
    public void testTransact() throws Exception {
        AtomicInteger x = new AtomicInteger(0);
        var mockEmf = Mocker.mock(EntityManagerFactory.class);
        var emf = mockEmf.getTarget();
        var mockEm = Mocker.mock(EntityManager.class);
        var em = mockEm.getTarget();
        var mockEt = Mocker.mock(EntityTransaction.class);
        var et = mockEt.getTarget();

        mockEmf.rule("CREATE").function(e -> e.createEntityManager()).executes(call -> {
            mockEmf.disable("CREATE");
            mockEm.enable("TRANS");
            return em;
        });
        mockEm.rule("TRANS").function(e -> e.getTransaction()).executes(call -> {
            mockEm.disable("TRANS");
            mockEt.enable("BEGIN");
            return et;
        });
        mockEt.rule("BEGIN").procedure(e -> e.begin()).executes(call -> {
            mockEt.disable("BEGIN");
            Assertions.assertEquals(0, x.getAndIncrement());
        });
        Runnable r = () -> {
            Assertions.assertEquals(1, x.getAndIncrement());
            mockEm.enable("TRANS2");
        };
        mockEm.rule("TRANS2").function(e -> e.getTransaction()).executes(call -> {
            mockEm.disable("TRANS2");
            mockEt.enable("COMMIT");
            return et;
        });
        mockEt.rule("COMMIT").procedure(e -> e.commit()).executes(call -> {
            mockEt.disable("COMMIT");
            mockEm.enable("CLOSE");
        });
        mockEm.rule("CLOSE").procedure(e -> e.close()).executes(call -> {
            mockEm.disable("CLOSE");
            Assertions.assertEquals(2, x.getAndIncrement());
        });

        var con = Connector.create("mumble", emf, HibernateAdapter.CANONICAL);
        con.transact(Runnable.class, r).run();
        Assertions.assertEquals(3, x.get());
    }
}
