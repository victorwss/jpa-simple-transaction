package ninja.javahacker.test.jpasimpletransactions;

import java.util.List;
import java.util.function.IntSupplier;
import ninja.javahacker.jpasimpletransactions.Connector;
import ninja.javahacker.jpasimpletransactions.hibernate.HibernatePersistenceProperties;
import org.hibernate.dialect.HSQLDialect;
import org.hsqldb.jdbc.JDBCDriver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ExtendedEntityManagerTest {

    private Connector hibernateConnect() throws ClassNotFoundException {
        new JDBCDriver();
        HibernatePersistenceProperties p = new HibernatePersistenceProperties("test-1")
                .withDialect(HSQLDialect.class)
                .withDriver(JDBCDriver.class)
                .withUrl("jdbc:hsqldb:mem:test1")
                .withUser("sa")
                .withPassword("");
        return Connector.withoutXml(List.of(Fruit.class), p);
    }

    private int insertFruit(Connector con) {
        var em = con.getEntityManager();
        var f = new Fruit("peach", "yellow");
        em.save(f);
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

    @Test
    public void testSimpleOperations() throws Exception {
        var c = hibernateConnect();
        var id = c.transact(IntSupplier.class, () -> insertFruit(c)).getAsInt();
        c.transact(Runnable.class, () -> selectFruit(c, id)).run();
    }
}
