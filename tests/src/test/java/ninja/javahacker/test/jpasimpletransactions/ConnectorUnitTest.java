package ninja.javahacker.test.jpasimpletransactions;

import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import ninja.javahacker.jpasimpletransactions.Connector;
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
                () -> Connector.create(null, emfc.getTarget()),
                nullMessage("persistenceUnitName"));
    }

    @Test
    public void testBadConnectorCreate2() throws Exception {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Connector.create(null, null),
                nullMessage("persistenceUnitName"));
    }

    @Test
    public void testBadConnectorCreate3() throws Exception {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Connector.create("foo", null),
                nullMessage("emf"));
    }

    @Test
    public void testUnitName() throws Exception {
        var emfc = Mocker.mock(EntityManagerFactory.class);
        var con = Connector.create("mumble", emfc.getTarget());
        Assertions.assertEquals(con.getPersistenceUnitName(), "mumble");
    }

    @Test
    public void testClose() throws Exception {
        AtomicInteger a = new AtomicInteger(0);
        var emfc = Mocker.mock(EntityManagerFactory.class);
        emfc.rule().when(EntityManagerFactory::close).then().go(() -> a.incrementAndGet()).delete().returnNothing();
        Connector.create("mumble", emfc.getTarget()).close();
        Assertions.assertEquals(1, a.get());
    }

    @Test
    public void testBadGetEntityManager() throws Exception {
        var emfc = Mocker.mock(EntityManagerFactory.class);
        Assertions.assertThrows(
                IllegalStateException.class,
                () -> Connector.create("foo", emfc.getTarget()).getEntityManager(),
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

        /*Mocker.SimplestAction h = () -> Assertions.assertEquals(2, x.getAndIncrement());
        Mocker.SimplestAction g = () -> mockEmf.rule().when(e -> e.close()).then().go(h).delete().returnThe(em);
        Mocker.SimplestAction f = () -> mockEt.rule().when(e -> e.commit()).then().go(g).delete().returnNothing();
        Runnable r = () -> {
            Assertions.assertEquals(1, x.getAndIncrement());
            mockEm.rule().when(e -> e.getTransaction()).then().go(f).delete().returnThe(et);
        };
        Mocker.SimplestAction d = () -> Assertions.assertEquals(0, x.getAndIncrement());
        Mocker.SimplestAction c = () -> mockEt.rule().when(e -> e.begin()).then().go(d).delete().returnNothing();
        Mocker.SimplestAction b = () -> mockEm.rule().when(e -> e.getTransaction()).then().go(c).delete().returnThe(et);*/
        mockEmf.rule().when(e -> e.createEntityManager()).then().returnNothing()/*.go(b).delete().returnThe(em)*/;

        var con = Connector.create("mumble", emf);
        con.transact(Runnable.class, () -> {}).run();
        Assertions.assertEquals(3, x.get());
    }
}
