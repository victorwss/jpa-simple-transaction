package ninja.javahacker.test.jpasimpletransactions;

import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import ninja.javahacker.jpasimpletransactions.Connector;
import ninja.javahacker.jpasimpletransactions.ProviderAdapter;
import ninja.javahacker.jpasimpletransactions.eclipselink.EclipselinkAdapter;
import ninja.javahacker.jpasimpletransactions.eclipselink.EclipselinkPersistenceProperties;
import ninja.javahacker.jpasimpletransactions.hibernate.HibernateAdapter;
import ninja.javahacker.jpasimpletransactions.hibernate.HibernatePersistenceProperties;
import ninja.javahacker.jpasimpletransactions.openjpa.OpenJpaAdapter;
import ninja.javahacker.jpasimpletransactions.openjpa.OpenJpaPersistenceProperties;
import org.hibernate.dialect.HSQLDialect;
import org.hsqldb.jdbc.JDBCDriver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ConnectorTest {

    private <C extends ProviderAdapter> Optional<C> load(Class<C> c) {
        return ServiceLoader
                .load(ProviderAdapter.class)
                .stream()
                .filter(p -> p.type() == c)
                .findAny()
                .map(Provider::get)
                .map(c::cast);
    }

    private static interface GetEm {
        public EntityManager getIt();
    }

    private void testConnect(Connector con) throws Exception {
        Assertions.assertAll(
                () -> Assertions.assertEquals(con.getPersistenceUnitName(), "test-1"),
                () -> Assertions.assertThrows(IllegalStateException.class, () -> con.getEntityManager()),
                () -> Assertions.assertNotNull(con.transact(GetEm.class, con::getEntityManager))
        );
    }

    private Connector hibernateConnect() throws ClassNotFoundException {
        var p = new HibernatePersistenceProperties("test-1")
                .withDialect(HSQLDialect.class)
                .withDriver(JDBCDriver.class);
        return Connector.withoutXml(List.of(Fruit.class), p);
    }

    @Test
    public void testSimpleConnectHibernate() throws Exception {
        testConnect(hibernateConnect());
    }

    @Test
    public void testHibernateAdapter() throws Exception {
        Assertions.assertTrue(load(HibernateAdapter.class).isPresent());
    }

    private Connector eclipselinkConnect() throws ClassNotFoundException {
        var p = new EclipselinkPersistenceProperties("test-1");
        return Connector.withoutXml(List.of(Fruit.class), p);
    }

    @Test
    public void testSimpleConnectEclipselink() throws Exception {
        testConnect(eclipselinkConnect());
    }

    @Test
    public void testEclipselinkAdapter() throws Exception {
        Assertions.assertTrue(load(EclipselinkAdapter.class).isPresent());
    }

    private Connector openJpaConnect() throws ClassNotFoundException {
        var p = new OpenJpaPersistenceProperties("test-1");
        return Connector.withoutXml(List.of(Fruit.class), p);
    }

    @Test
    public void testSimpleConnectOpenJpa() throws Exception {
        testConnect(openJpaConnect());
    }

    @Test
    public void testOpenJpaAdapter() throws Exception {
        Assertions.assertTrue(load(OpenJpaAdapter.class).isPresent());
    }

    @Test
    public void testAdapterList() throws Exception {
        var a = ServiceLoader
                .load(ProviderAdapter.class)
                .stream()
                .map(Provider::get)
                .collect(Collectors.toList());

        System.out.println(a);
        Assertions.assertAll(
                () -> Assertions.assertEquals(3, a.size()),
                () -> Assertions.assertTrue(a.contains(new EclipselinkAdapter())),
                () -> Assertions.assertTrue(a.contains(new HibernateAdapter())),
                () -> Assertions.assertTrue(a.contains(new OpenJpaAdapter()))
        );
    }
}
