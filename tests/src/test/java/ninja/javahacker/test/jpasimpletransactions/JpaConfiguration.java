package ninja.javahacker.test.jpasimpletransactions;

import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import ninja.javahacker.jpasimpletransactions.Connector;
import ninja.javahacker.jpasimpletransactions.ProviderAdapter;
import ninja.javahacker.jpasimpletransactions.config.ProviderConnectorFactory;
import ninja.javahacker.jpasimpletransactions.config.SchemaGenerationAction;
import ninja.javahacker.jpasimpletransactions.config.StandardConnectorFactory;
import ninja.javahacker.jpasimpletransactions.eclipselink.EclipselinkAdapter;
import ninja.javahacker.jpasimpletransactions.eclipselink.EclipselinkConnectorFactory;
import ninja.javahacker.jpasimpletransactions.hibernate.HibernateAdapter;
import ninja.javahacker.jpasimpletransactions.hibernate.HibernateConnectorFactory;
import ninja.javahacker.jpasimpletransactions.openjpa.OpenJpaAdapter;
import ninja.javahacker.jpasimpletransactions.openjpa.OpenJpaConnectorFactory;
import ninja.javahacker.jpasimpletransactions.openjpa.Support;
import org.hsqldb.jdbc.JDBCDriver;
import org.junit.jupiter.params.provider.Arguments;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class JpaConfiguration {

    @NonNull
    Supplier<StandardConnectorFactory<?>> preparer;

    @NonNull
    Supplier<ProviderAdapter> hasAdapter;

    @NonNull
    @Getter
    String name;

    private JpaConfiguration(
            @NonNull String name,
            @NonNull Supplier<ProviderAdapter> hasAdapter,
            @NonNull Supplier<ProviderConnectorFactory<?>> prop)
    {
        this.name = name;
        this.hasAdapter = hasAdapter;
        this.preparer = () -> prop.get()
                .withPersistenceUnitName("test-1")
                .withUrl("jdbc:hsqldb:mem:test1")
                .withUser("sa")
                .withPassword("")
                .withSchemaGenerationAction(SchemaGenerationAction.DROP_AND_CREATE)
                .addEntity(Fruit.class);
    }

    public ProviderAdapter getAdapter() {
        return hasAdapter.get();
    }

    public StandardConnectorFactory<?> prepare() {
        return preparer.get();
    }

    public Connector connect() {
        return prepare().connect();
    }

    public static Stream<Arguments> all() {
        Supplier<ProviderConnectorFactory<?>> ojpa = () -> new OpenJpaConnectorFactory()
                .withDynamicEnhancementAgent(true)
                .withRuntimeUnenhancedClasses(Support.SUPPORTED)
                .withDriver(JDBCDriver.class);
        Supplier<ProviderConnectorFactory<?>> hib = HibernateConnectorFactory::new;
        Supplier<ProviderConnectorFactory<?>> el = EclipselinkConnectorFactory::new;
        var a = new JpaConfiguration("Hibernate", () -> HibernateAdapter.CANONICAL, hib);
        var b = new JpaConfiguration("Eclipselink", () -> EclipselinkAdapter.CANONICAL, el);
        var c = new JpaConfiguration("OpenJpa", () -> OpenJpaAdapter.CANONICAL, ojpa);
        return Stream.of(a, b, c).map(x -> Arguments.of(x.getName(), x));
    }
}
