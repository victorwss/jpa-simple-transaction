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

    @ParameterizedTest(name = "testAdapterIdentityless {0}")
    @MethodSource("ninja.javahacker.test.jpasimpletransactions.JpaConfiguration#all")
    public void testAdapterIdentityless(String t, JpaConfiguration config) throws Exception {
        var adapter = config.getAdapter();
        Assertions.assertAll(
                () -> Assertions.assertSame(adapter, adapter.getClass().getField("CANONICAL").get(null)),
                () -> Assertions.assertEquals(adapter, adapter.getClass().getConstructor().newInstance()),
                () -> Assertions.assertEquals(adapter, load(adapter.getClass()).orElseThrow(AssertionError::new)),
                () -> Assertions.assertTrue(ProviderAdapter.all().collect(Collectors.toList()).contains(adapter)),
                () -> Assertions.assertTrue(ProviderAdapter.all().collect(Collectors.toList()).contains(adapter))
        );
    }

    @Test
    public void testAdapterList() throws Exception {
        var a = ServiceLoader
                .load(ProviderAdapter.class)
                .stream()
                .map(Provider::get)
                .collect(Collectors.toList());

        Assertions.assertAll(
                () -> Assertions.assertEquals(3, a.size()),
                () -> Assertions.assertTrue(a.contains(EclipselinkAdapter.CANONICAL)),
                () -> Assertions.assertTrue(a.contains(HibernateAdapter.CANONICAL)),
                () -> Assertions.assertTrue(a.contains(OpenJpaAdapter.CANONICAL))
        );
    }

    @Test
    public void testAdapterListProvided() throws Exception {
        var a = ProviderAdapter.all().collect(Collectors.toList());

        Assertions.assertAll(
                () -> Assertions.assertEquals(3, a.size()),
                () -> Assertions.assertTrue(a.contains(EclipselinkAdapter.CANONICAL)),
                () -> Assertions.assertTrue(a.contains(HibernateAdapter.CANONICAL)),
                () -> Assertions.assertTrue(a.contains(OpenJpaAdapter.CANONICAL))
        );
    }

    @ParameterizedTest(name = "testAdapterProvider {0}")
    @MethodSource("ninja.javahacker.test.jpasimpletransactions.JpaConfiguration#all")
    public void testAdapterProvider(String t, JpaConfiguration config) throws Exception {
        var adapter = config.getAdapter();
        var provider = adapter.getJpaProvider();
        Assertions.assertNotNull(provider);
        Assertions.assertSame(provider, adapter.getJpaProvider());
    }
}
