package ninja.javahacker.test.jpasimpletransactions;

import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import ninja.javahacker.jpasimpletransactions.eclipselink.EclipselinkAdapter;
import ninja.javahacker.jpasimpletransactions.eclipselink.EclipselinkPersistenceProperties;
import ninja.javahacker.jpasimpletransactions.hibernate.HibernateAdapter;
import ninja.javahacker.jpasimpletransactions.hibernate.HibernatePersistenceProperties;
import ninja.javahacker.jpasimpletransactions.openjpa.OpenJpaAdapter;
import ninja.javahacker.jpasimpletransactions.openjpa.OpenJpaPersistenceProperties;
import ninja.javahacker.jpasimpletransactions.openjpa.Support;
import ninja.javahacker.jpasimpletransactions.properties.SchemaGenerationAction;
import org.hsqldb.jdbc.JDBCDriver;
import org.junit.jupiter.params.provider.Arguments;

/**
 * @author Victor Williams Stafusa da Silva
 */
@UtilityClass
public class JPAConfiguration {

    public Stream<Arguments> properties() {
        var a = new HibernatePersistenceProperties();
        var b = new EclipselinkPersistenceProperties();
        var c = new OpenJpaPersistenceProperties()
                .withDynamicEnhancementAgent(false)
                .withRuntimeUnenhancedClasses(Support.SUPPORTED)
                .withDriver(JDBCDriver.class);
        return Stream.of(a, b, c).map(r -> r
                .withPersistenceUnitName("test-1")
                .withUrl("jdbc:hsqldb:mem:test1")
                .withUser("sa")
                .withPassword("")
                .withSchemaGenerationAction(SchemaGenerationAction.dropAndCreate())
        ).map(x -> Arguments.of(x.getClass().getSimpleName().replace("PersistenceProperties", ""), x));
    }

    public Stream<Arguments> providers() {
        return Stream.of(
                Arguments.of("Hibernate", HibernateAdapter.class),
                Arguments.of("Eclipselink", EclipselinkAdapter.class),
                Arguments.of("OpenJpa", OpenJpaAdapter.class)
        );
    }
}
