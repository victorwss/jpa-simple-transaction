package ninja.javahacker.test.jpasimpletransactions;

import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import ninja.javahacker.jpasimpletransactions.Connector;
import ninja.javahacker.jpasimpletransactions.ProviderAdapter;
import ninja.javahacker.jpasimpletransactions.eclipselink.EclipselinkAdapter;
import ninja.javahacker.jpasimpletransactions.eclipselink.EclipselinkPersistenceProperties;
import ninja.javahacker.jpasimpletransactions.hibernate.HibernateAdapter;
import ninja.javahacker.jpasimpletransactions.hibernate.HibernatePersistenceProperties;
import ninja.javahacker.jpasimpletransactions.openjpa.OpenJpaAdapter;
import ninja.javahacker.jpasimpletransactions.openjpa.OpenJpaPersistenceProperties;
import ninja.javahacker.jpasimpletransactions.openjpa.Support;
import ninja.javahacker.jpasimpletransactions.properties.PersistenceProperties;
import ninja.javahacker.jpasimpletransactions.properties.SchemaGenerationAction;
import org.hibernate.dialect.HSQLDialect;
import org.hsqldb.jdbc.JDBCDriver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

    @DisplayName("testAdapter")
    @ParameterizedTest(name = "{0}")
    @MethodSource("ninja.javahacker.test.jpasimpletransactions.JPAConfiguration#providers")
    public void testAdapter(String t, Class<? extends ProviderAdapter> provider) throws Exception {
        Assertions.assertTrue(load(provider).isPresent());
    }

    @DisplayName("testSimpleConnect")
    @ParameterizedTest(name = "{0}")
    @MethodSource("ninja.javahacker.test.jpasimpletransactions.JPAConfiguration#properties")
    public void testSimpleConnect(String t, PersistenceProperties prop) throws Exception {
        var con = Connector.withoutXml(List.of(Fruit.class), prop);
        Assertions.assertAll(
                () -> Assertions.assertEquals(con.getPersistenceUnitName(), "test-1"),
                () -> Assertions.assertThrows(IllegalStateException.class, () -> con.getEntityManager()),
                () -> Assertions.assertNotNull(con.transact(GetEm.class, con::getEntityManager))
        );
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
