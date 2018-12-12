package ninja.javahacker.test.jpasimpletransactions;

import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.function.Supplier;
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
import ninja.javahacker.jpasimpletransactions.properties.PersistenceProperties;
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

    private static Stream<Arguments> providers() {
        return Stream.of(
                Arguments.of("Hibernate", HibernateAdapter.class),
                Arguments.of("Eclipselink", EclipselinkAdapter.class),
                Arguments.of("OpenJpa", OpenJpaAdapter.class)
        );
    }

    private static Stream<Arguments> properties() {
        Supplier<HibernatePersistenceProperties> a = () -> new HibernatePersistenceProperties()
                .withPersistenceUnitName("test-1")
                .withDialect(HSQLDialect.class)
                .withDriver(JDBCDriver.class);
        Supplier<EclipselinkPersistenceProperties> b = () -> new EclipselinkPersistenceProperties()
                .withPersistenceUnitName("test-1")
                .withDriver(JDBCDriver.class);
        Supplier<OpenJpaPersistenceProperties> c = () -> new OpenJpaPersistenceProperties()
                .withPersistenceUnitName("test-1")
                .withDriver(JDBCDriver.class);
        return Stream.of(Arguments.of("Hibernate", a), Arguments.of("Eclipselink", b), Arguments.of("OpenJpa", c));
    }

    @DisplayName("testAdapter")
    @ParameterizedTest(name = "{0}")
    @MethodSource("providers")
    public void testAdapter(String t, Class<? extends ProviderAdapter> provider) throws Exception {
        Assertions.assertTrue(load(provider).isPresent());
    }

    @DisplayName("testSimpleConnect")
    @ParameterizedTest(name = "{0}")
    @MethodSource("properties")
    public void testSimpleConnect(String t, Supplier<? extends PersistenceProperties> prop) throws Exception {
        var p = prop.get();
        var con = Connector.withoutXml(List.of(Fruit.class), p);
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
