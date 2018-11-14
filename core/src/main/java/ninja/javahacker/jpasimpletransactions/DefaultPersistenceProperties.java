package ninja.javahacker.jpasimpletransactions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.experimental.Tolerate;
import lombok.experimental.Wither;

/**
 * A collection of properties used to instantiate a {@link Connector}.
 * @author Victor Williams Stafusa da Silva
 */
@Getter
@Wither
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressFBWarnings("PRMC_POSSIBLY_REDUNDANT_METHOD_CALLS")
public class DefaultPersistenceProperties implements PersistenceProperties {

    @NonNull
    @Wither(AccessLevel.NONE)
    final ProviderAdapter providerAdapter;

    @NonNull String persistenceUnitName;
    @NonNull Class<? extends Driver> driver = Driver.class;
    @NonNull String url = "";
    @NonNull String user = "";
    @NonNull String password = "";
    @NonNull SchemaGenerationAction schemaGenerationAction = SchemaGenerationAction.unspecified();
    @NonNull SchemaGenerationSource schemaGenerationCreate = SchemaGenerationSource.unspecified();
    @NonNull SchemaGenerationSource schemaGenerationDrop = SchemaGenerationSource.unspecified();
    @NonNull SchemaGenerationActionTarget schemaGenerationScriptsCreate = SchemaGenerationActionTarget.unspecified();
    @NonNull String loadScript = "";
    @NonNull String schemaGenerationConnection = "";
    @NonNull TriBoolean createDatabaseSchemas = TriBoolean.UNSPECIFIED;
    @NonNull String databaseProductName = "";
    @NonNull String databaseMajorVersion = "";
    @NonNull String databaseMinorVersion = "";

    @NonNull Map<String, String> extras = new HashMap<>(20);

    public DefaultPersistenceProperties(@NonNull ProviderAdapter providerAdapter, @NonNull String persistenceUnitName) {
        this.providerAdapter = providerAdapter;
        this.persistenceUnitName = persistenceUnitName;
    }

    @Tolerate
    public DefaultPersistenceProperties withDriver(@NonNull String driverName) throws ClassNotFoundException {
        return withDriver(Class.forName(driverName).asSubclass(Driver.class));
    }

    @Tolerate
    public DefaultPersistenceProperties withCreateDatabaseSchemas(boolean newValue) {
        this.createDatabaseSchemas = TriBoolean.from(newValue);
        return this;
    }

    @Override
    public DefaultPersistenceProperties putExtra(String key, String value) {
        extras.put(key, value);
        return this;
    }

    @Override
    public String getExtra(String key) {
        return extras.get(key);
    }

    @Override
    public DefaultPersistenceProperties removeExtra(String key) {
        extras.remove(key);
        return this;
    }

    @Override
    public DefaultPersistenceProperties clearExtras() {
        extras.clear();
        return this;
    }

    @Override
    public Map<String, String> getExtras() {
        return Map.copyOf(new HashMap<>(extras));
    }

    @Override
    public Map<String, String> buildCore() {
        Map<String, String> props = new HashMap<>(64);
        if (driver != Driver.class) props.put("javax.persistence.jdbc.driver", driver.getName());
        work("javax.persistence.jdbc.url", url, props::put);
        work("javax.persistence.jdbc.user", user, props::put);
        work("javax.persistence.jdbc.password", password, props::put);
        work("javax.persistence.database-product-name", databaseProductName, props::put);
        work("javax.persistence.database-major-version", databaseMajorVersion, props::put);
        work("javax.persistence.database-minor-version", databaseMinorVersion, props::put);
        schemaGenerationAction.work("javax.persistence.schema-generation.database.action", props::put);
        schemaGenerationCreate.work(
                "javax.persistence.schema-generation.create-source",
                "javax.persistence.schema-generation.create-script-source",
                props::put);
        schemaGenerationDrop.work(
                "javax.persistence.schema-generation.drop-source",
                "javax.persistence.schema-generation.drop-script-source",
                props::put);
        schemaGenerationScriptsCreate.work(
                "javax.persistence.schema-generation.scripts.action",
                "javax.persistence.schema-generation.scripts.create-target",
                "javax.persistence.schema-generation.scripts.drop-target",
                props::put);
        work("javax.persistence.sql-load-script-source", loadScript, props::put);
        work("javax.persistence.schema-generation.connection", schemaGenerationConnection, props::put);
        createDatabaseSchemas.work("javax.persistence.schema-generation.create-database-schemas", props::put);
        return props;
    }

    private void work(@NonNull String key, @NonNull String value, @NonNull BiConsumer<String, String> acceptor) {
        if (!value.isEmpty()) acceptor.accept(key, value);
    }
}
