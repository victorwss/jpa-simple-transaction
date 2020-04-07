package ninja.javahacker.jpasimpletransactions.eclipselink;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Driver;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.With;
import lombok.experimental.FieldDefaults;
import ninja.javahacker.jpasimpletransactions.config.ProviderConnectorFactory;
import ninja.javahacker.jpasimpletransactions.config.SchemaGenerationAction;
import ninja.javahacker.jpasimpletransactions.config.SchemaGenerationActionTarget;
import ninja.javahacker.jpasimpletransactions.config.SchemaGenerationSource;
import ninja.javahacker.jpasimpletransactions.config.TriBoolean;

/**
 * Implementation of {@link ProviderConnectorFactory} for Eclipselink.
 * @author Victor Williams Stafusa da Silva
 */
@Value
@With
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EclipselinkConnectorFactory implements ProviderConnectorFactory<EclipselinkConnectorFactory> {

    private static final Optional<URL> NOWHERE;

    public static final EclipselinkAdapter CANONICAL = new EclipselinkAdapter();

    static {
        try {
            NOWHERE = Optional.of(new URL("http://0.0.0.0/"));
        } catch (MalformedURLException x) {
            throw new AssertionError(x);
        }
    }

    @NonNull String persistenceUnitName;
    @NonNull Class<? extends Driver> driver;
    @NonNull String url;
    @NonNull String user;
    @NonNull String password;
    @NonNull SchemaGenerationAction schemaGenerationAction;
    @NonNull SchemaGenerationSource schemaGenerationCreate;
    @NonNull SchemaGenerationSource schemaGenerationDrop;
    @NonNull SchemaGenerationActionTarget schemaGenerationScriptsCreate;
    @NonNull String loadScript;
    @NonNull String schemaGenerationConnection;
    @NonNull TriBoolean createDatabaseSchemas;
    @NonNull String databaseProductName;
    @NonNull String databaseMajorVersion;
    @NonNull String databaseMinorVersion;
    @NonNull Map<String, String> extras;
    @NonNull Set<Class<?>> entities;

    public EclipselinkConnectorFactory() {
        this.persistenceUnitName = "";
        this.driver = Driver.class;
        this.url = "";
        this.user = "";
        this.password = "";
        this.schemaGenerationAction = SchemaGenerationAction.unspecified();
        this.schemaGenerationCreate = SchemaGenerationSource.unspecified();
        this.schemaGenerationDrop = schemaGenerationCreate;
        this.schemaGenerationScriptsCreate = SchemaGenerationActionTarget.unspecified();
        this.loadScript = "";
        this.schemaGenerationConnection = "";
        this.createDatabaseSchemas = TriBoolean.UNSPECIFIED;
        this.databaseProductName = "";
        this.databaseMajorVersion = "";
        this.databaseMinorVersion = "";
        this.extras = Map.of();
        this.entities = Set.of();
    }

    @Override
    public EclipselinkAdapter getProviderAdapter() {
        return EclipselinkAdapter.CANONICAL;
    }

    @Override
    public Optional<URL> getPersistenceUnitUrl() {
        return NOWHERE;
    }
}
