package ninja.javahacker.test.jpasimpletransactions;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;
import ninja.javahacker.jpasimpletransactions.ProviderAdapter;
import ninja.javahacker.jpasimpletransactions.eclipselink.EclipselinkAdapter;
import ninja.javahacker.jpasimpletransactions.hibernate.HibernateAdapter;
import ninja.javahacker.jpasimpletransactions.openjpa.OpenJpaAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class AdapterTest {

    private <C extends ProviderAdapter> Optional<C> load(Class<C> c) {
        return ServiceLoader
                .load(ProviderAdapter.class)
                .stream()
                .filter(p -> p.type() == c)
                .findAny()
                .map(Provider::get)
                .map(c::cast);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ninja.javahacker.test.jpasimpletransactions.JpaConfiguration#all")
    public void testAdapterIdentityless(String t, JpaConfiguration config) throws Exception {
        var adapter = config.getAdapter();
        var canonical = adapter.getClass().getField("CANONICAL").get(null);
        var newi = adapter.getClass().getConstructor().newInstance();
        var loaded = load(adapter.getClass()).orElseThrow(AssertionError::new);
        Assertions.assertAll(
                () -> Assertions.assertSame(adapter, canonical),
                () -> Assertions.assertEquals(adapter, newi),
                () -> Assertions.assertEquals(adapter, loaded),
                () -> Assertions.assertEquals(newi, loaded)
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
                () -> Assertions.assertTrue(a.contains(EclipselinkAdapter.CANONICAL)),
                () -> Assertions.assertTrue(a.contains(HibernateAdapter.CANONICAL)),
                () -> Assertions.assertTrue(a.contains(OpenJpaAdapter.CANONICAL))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ninja.javahacker.test.jpasimpletransactions.JpaConfiguration#all")
    public void testAdapterProvider(String t, JpaConfiguration config) throws Exception {
        var adapter = config.getAdapter();
        var provider = adapter.getJpaProvider();
        Assertions.assertNotNull(provider);
        Assertions.assertEquals(provider, adapter.getJpaProvider());
    }
}
