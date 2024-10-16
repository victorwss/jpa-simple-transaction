package ninja.javahacker.test.jpasimpletransactions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ConnectorTest {

    @ParameterizedTest(name = "{displayName} - {0}")
    @MethodSource("ninja.javahacker.test.jpasimpletransactions.JpaConfiguration#all")
    public void testPersistenceUnit(String t, JpaConfiguration config) throws Exception {
        var con = config.connect();
        Assertions.assertEquals("test-1", con.getPersistenceUnitName());
    }

    @ParameterizedTest(name = "{displayName} - {0}")
    @MethodSource("ninja.javahacker.test.jpasimpletransactions.JpaConfiguration#all")
    public void testEntityManagerOnTransaction(String t, JpaConfiguration config) throws Exception {
        var con = config.connect();
        con.transact(Runnable.class, () -> Assertions.assertAll(
                () -> Assertions.assertNotNull(con.getEntityManager()),
                () -> Assertions.assertTrue(config.getAdapter().recognizes(con.getEntityManager())),
                () -> Assertions.assertSame(con.getEntityManager(), config.getAdapter().ensureRecognition(con.getEntityManager())),
                () -> Assertions.assertTrue(con.getEntityManager().getTransaction().isActive())
        )).run();
    }

    @ParameterizedTest(name = "{displayName} - {0}")
    @MethodSource("ninja.javahacker.test.jpasimpletransactions.JpaConfiguration#all")
    public void testConnectionOnTransaction(String t, JpaConfiguration config) throws Exception {
        var con = config.connect();
        con.transact(Runnable.class,
                () -> {
                    var c = con.getEntityManager();
                    Assertions.assertEquals(config.getAdapter().getConnection(c), c.getConnection());
                }
        ).run();
    }

    @ParameterizedTest(name = "{displayName} - {0}")
    @MethodSource("ninja.javahacker.test.jpasimpletransactions.JpaConfiguration#all")
    public void testEntityManagerOutOfTransaction(String t, JpaConfiguration config) throws Exception {
        var con = config.connect();
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
