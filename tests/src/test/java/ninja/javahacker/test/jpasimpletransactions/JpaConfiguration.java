package ninja.javahacker.test.jpasimpletransactions;

import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
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
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class JpaConfiguration {

    @NonNull
    StandardConnectorFactory<?> properties;

    @NonNull
    ProviderAdapter adapter;

    private JpaConfiguration(@NonNull ProviderAdapter adapter, @NonNull ProviderConnectorFactory<?> prop) {
        this.adapter = adapter;
        this.properties = prop
                .withPersistenceUnitName("test-1")
                .withUrl("jdbc:hsqldb:mem:test1")
                .withUser("sa")
                .withPassword("")
                .withSchemaGenerationAction(SchemaGenerationAction.DROP_AND_CREATE)
                .addEntity(Fruit.class);
    }

    public String getName() {
        return adapter.getClass().getSimpleName().replace("Adapter", "");
    }

    public static Stream<Arguments> all() {
        var ojpa = new OpenJpaConnectorFactory()
                .withDynamicEnhancementAgent(true)
                .withRuntimeUnenhancedClasses(Support.SUPPORTED)
                .withDriver(JDBCDriver.class);
        var hib = new HibernateConnectorFactory();
        var el = new EclipselinkConnectorFactory();
        var a = new JpaConfiguration(HibernateAdapter.CANONICAL, hib);
        var b = new JpaConfiguration(EclipselinkAdapter.CANONICAL, el);
        var c = new JpaConfiguration(OpenJpaAdapter.CANONICAL, ojpa);
        return Stream.of(a, b, c).map(x -> Arguments.of(x.getName(), x));
    }
}
