package ninja.javahacker.test.jpasimpletransactions;

import java.util.function.IntSupplier;
import ninja.javahacker.jpasimpletransactions.Connector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ExtendedEntityManagerTest {

    private int insertFruit(Connector con) {
        var em = con.getEntityManager();
        var f = new Fruit("peach", "yellow");
        em.save(f);
        em.flush();
        Assertions.assertNotNull(f.getId());
        return f.getId();
    }

    private void selectFruit(Connector con, int id) {
        var em = con.getEntityManager();
        var of = em.findOptional(Fruit.class, id);
        var f = of.orElseThrow(AssertionError::new);
        Assertions.assertAll(
                () -> Assertions.assertEquals("peach", f.getName()),
                () -> Assertions.assertEquals("yellow", f.getColor()),
                () -> Assertions.assertEquals(id, f.getId().intValue())
        );
    }

    @ParameterizedTest(name = "{displayName} - {0}")
    @MethodSource("ninja.javahacker.test.jpasimpletransactions.JpaConfiguration#all")
    public void testSimpleOperations(String t, JpaConfiguration config) throws Exception {
        var c = config.connect();
        var id = c.transact(IntSupplier.class, () -> insertFruit(c)).getAsInt();
        c.transact(Runnable.class, () -> selectFruit(c, id)).run();
    }
}
