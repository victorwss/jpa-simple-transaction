package ninja.javahacker.test.jpasimpletransactions;

import java.util.List;
import java.util.function.IntSupplier;
import ninja.javahacker.jpasimpletransactions.Connector;
import ninja.javahacker.jpasimpletransactions.properties.PersistenceProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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

    @DisplayName("testSimpleOperations Y {1}")
    @ParameterizedTest(name = "testSimpleOperations X {1}")
    @MethodSource("ninja.javahacker.test.jpasimpletransactions.JPAConfiguration#properties")
    public void testSimpleOperations(String t, PersistenceProperties prop) throws Exception {
        var c = Connector.withoutXml(List.of(Fruit.class), prop);
        var id = c.transact(IntSupplier.class, () -> insertFruit(c)).getAsInt();
        c.transact(Runnable.class, () -> selectFruit(c, id)).run();
    }
}
