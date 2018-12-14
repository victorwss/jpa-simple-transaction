package ninja.javahacker.test.jpasimpletransactions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ConnectorTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("ninja.javahacker.test.jpasimpletransactions.JpaConfiguration#all")
    public void testPersistenceUnit(String t, JpaConfiguration config) throws Exception {
        var con = config.getProperties().connect();
        Assertions.assertEquals(con.getPersistenceUnitName(), "test-1");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ninja.javahacker.test.jpasimpletransactions.JpaConfiguration#all")
    public void testEntityManagerOnTransaction(String t, JpaConfiguration config) throws Exception {
        var con = config.getProperties().connect();
        con.transact(Runnable.class, () -> Assertions.assertAll(
                () -> Assertions.assertNotNull(con.getEntityManager()),
                () -> Assertions.assertTrue(config.getAdapter().recognizes(con.getEntityManager())),
                () -> Assertions.assertSame(con.getEntityManager(), config.getAdapter().ensureRecognition(con.getEntityManager())),
                () -> Assertions.assertTrue(con.getEntityManager().getTransaction().isActive())
        )).run();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ninja.javahacker.test.jpasimpletransactions.JpaConfiguration#all")
    public void testConnectionOnTransaction(String t, JpaConfiguration config) throws Exception {
        var con = config.getProperties().connect();
        con.transact(Runnable.class,
                () -> Assertions.assertEquals(config.getAdapter().getConnection(con.getEntityManager()), con.getEntityManager().getConnection())
        ).run();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ninja.javahacker.test.jpasimpletransactions.JpaConfiguration#all")
    public void testEntityManagerOutOfTransaction(String t, JpaConfiguration config) throws Exception {
        var con = config.getProperties().connect();
        Assertions.assertAll(
                () -> Assertions.assertThrows(IllegalStateException.class, con::getEntityManager),
                () -> con.transact(Runnable.class, () -> Assertions.assertAll(
                        () -> Assertions.assertNotNull(con.getEntityManager()),
                        () -> Assertions.assertTrue(con.getEntityManager().getTransaction().isActive())
                )).run(),
                () -> Assertions.assertThrows(IllegalStateException.class, con::getEntityManager)
        );
    }
}
