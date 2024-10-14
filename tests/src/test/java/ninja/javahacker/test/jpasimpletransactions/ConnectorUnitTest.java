package ninja.javahacker.test.jpasimpletransactions;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ninja.javahacker.jpasimpletransactions.Connector;
import ninja.javahacker.jpasimpletransactions.ProviderAdapter;
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
        var emf = Mocker.mock(EntityManagerFactory.class).getTarget();
        var pa = Mocker.mock(ProviderAdapter.class).getTarget();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Connector.create(null, emf, pa),
                nullMessage("persistenceUnitName")
        );
    }

    @Test
    public void testBadConnectorCreate2() throws Exception {
        var pa = Mocker.mock(ProviderAdapter.class).getTarget();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Connector.create(null, null, pa),
                nullMessage("persistenceUnitName")
        );
    }

    @Test
    public void testBadConnectorCreate3() throws Exception {
        var pa = Mocker.mock(ProviderAdapter.class).getTarget();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Connector.create("foo", null, pa),
                nullMessage("emf")
        );
    }

    @Test
    public void testBadConnectorCreate4() throws Exception {
        var emf = Mocker.mock(EntityManagerFactory.class).getTarget();
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Connector.create("foo", emf, null),
                nullMessage("adapter")
        );
    }

    @Test
    public void testBadConnectorCreate5() throws Exception {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Connector.create("foo", null, null),
                nullMessage("emf")
        );
    }

    @Test
    public void testUnitName() throws Exception {
        var emf = Mocker.mock(EntityManagerFactory.class).getTarget();
        var pa = Mocker.mock(ProviderAdapter.class).getTarget();
        var con = Connector.create("mumble", emf, pa);
        Assertions.assertEquals(con.getPersistenceUnitName(), "mumble");
    }

    @Test
    public void testClose() throws Exception {
        AtomicInteger a = new AtomicInteger(0);
        var emfc = Mocker.mock(EntityManagerFactory.class);
        var pa = Mocker.mock(ProviderAdapter.class).getTarget();
        emfc.rule("X").procedure(EntityManagerFactory::close).executes(call -> a.incrementAndGet());
        emfc.enable("X");
        Connector.create("mumble", emfc.getTarget(), pa).close();
        Assertions.assertEquals(1, a.get());
    }

    @Test
    public void testBadGetEntityManager() throws Exception {
        var emf = Mocker.mock(EntityManagerFactory.class).getTarget();
        var pa = Mocker.mock(ProviderAdapter.class).getTarget();
        Assertions.assertThrows(
                IllegalStateException.class,
                () -> Connector.create("foo", emf, pa).getEntityManager(),
                "Can't get the EntityManager outside of a transaction."
        );
    }

    public static interface Work<E> {
        public E work();
    }

    public static interface SimpleWork extends Work<String> {
    }

    private static class CustomException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    private static class CommitTest<E> {
        Mocker<EntityManagerFactory> mockEmf = Mocker.mock(EntityManagerFactory.class);
        EntityManagerFactory emf = mockEmf.getTarget();
        Mocker<EntityManager> mockEm = Mocker.mock(EntityManager.class);
        EntityManager em = mockEm.getTarget();
        Mocker<EntityTransaction> mockEt = Mocker.mock(EntityTransaction.class);
        EntityTransaction et = mockEt.getTarget();
        Mocker<ProviderAdapter> mockPa = Mocker.mock(ProviderAdapter.class);
        ProviderAdapter pa = mockPa.getTarget();
        E out;
        Work<E> r;
        AtomicInteger x = new AtomicInteger(0);

        public CommitTest(E out) {
            this.out = out;
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
            r = () -> {
                Assertions.assertEquals(1, x.getAndIncrement());
                mockEt.enable("COMMIT");
                return out;
            };
            mockEt.rule("COMMIT").procedure(e -> e.commit()).executes(call -> {
                mockEt.disable("COMMIT");
                mockEm.enable("CLOSE");
            });
            mockEm.rule("CLOSE").procedure(e -> e.close()).executes(call -> {
                mockEm.disable("CLOSE");
                Assertions.assertEquals(2, x.getAndIncrement());
            });
            mockEmf.enable("CREATE");
        }
    }

    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    private static class RollbackTest {
        Mocker<EntityManagerFactory> mockEmf = Mocker.mock(EntityManagerFactory.class);
        EntityManagerFactory emf = mockEmf.getTarget();
        Mocker<EntityManager> mockEm = Mocker.mock(EntityManager.class);
        EntityManager em = mockEm.getTarget();
        Mocker<EntityTransaction> mockEt = Mocker.mock(EntityTransaction.class);
        EntityTransaction et = mockEt.getTarget();
        Mocker<ProviderAdapter> mockPa = Mocker.mock(ProviderAdapter.class);
        ProviderAdapter pa = mockPa.getTarget();
        Work<Object> r;
        AtomicInteger x = new AtomicInteger(0);

        public RollbackTest() {
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
            r = () -> {
                Assertions.assertEquals(1, x.getAndIncrement());
                mockEt.enable("ROLLBACK");
                throw new CustomException();
            };
            mockEt.rule("ROLLBACK").procedure(e -> e.rollback()).executes(call -> {
                mockEt.disable("ROLLBACK");
                mockEm.enable("CLOSE");
            });
            mockEm.rule("CLOSE").procedure(e -> e.close()).executes(call -> {
                mockEm.disable("CLOSE");
                Assertions.assertEquals(2, x.getAndIncrement());
            });
            mockEmf.enable("CREATE");
        }
    }

    @Test
    public void testTransactCommit1() throws Exception {
        var fixture = new CommitTest<>("blarg");
        var con = Connector.create("mumble", fixture.emf, fixture.pa);
        Assertions.assertSame(fixture.out, con.transact(Work.class, fixture.r).work());
        Assertions.assertEquals(3, fixture.x.get());
    }

    @Test
    public void testTransactRollback() throws Exception {
        var fixture = new RollbackTest();
        var con = Connector.create("mumble", fixture.emf, fixture.pa);
        Assertions.assertThrows(CustomException.class, () -> con.transact(Work.class, fixture.r).work());
        Assertions.assertEquals(3, fixture.x.get());
    }
}
