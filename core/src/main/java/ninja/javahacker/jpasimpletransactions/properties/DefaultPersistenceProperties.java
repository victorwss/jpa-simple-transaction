package ninja.javahacker.jpasimpletransactions.properties;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.experimental.Tolerate;
import lombok.experimental.Wither;
import ninja.javahacker.jpasimpletransactions.ProviderAdapter;

/**
 * A collection of properties used to instantiate a {@link Connector}.
 * @author Victor Williams Stafusa da Silva
 */
@Value
@Wither
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressFBWarnings("PRMC_POSSIBLY_REDUNDANT_METHOD_CALLS")
public class DefaultPersistenceProperties {

    @NonNull ProviderAdapter providerAdapter;
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

    public DefaultPersistenceProperties(@NonNull ProviderAdapter providerAdapter) {
        this.providerAdapter = providerAdapter;
        this.persistenceUnitName = "";
        this.driver = Driver.class;
        this.url = "";
        this.user = "";
        this.password = "";
        this.schemaGenerationAction = SchemaGenerationAction.unspecified();
        this.schemaGenerationCreate = SchemaGenerationSource.unspecified();
        this.schemaGenerationDrop = SchemaGenerationSource.unspecified();
        this.schemaGenerationScriptsCreate = SchemaGenerationActionTarget.unspecified();
        this.loadScript = "";
        this.schemaGenerationConnection = "";
        this.createDatabaseSchemas = TriBoolean.UNSPECIFIED;
        this.databaseProductName = "";
        this.databaseMajorVersion = "";
        this.databaseMinorVersion = "";
        this.extras = Map.of();
    }

    @Tolerate
    public DefaultPersistenceProperties withDriver(@NonNull String driverName) throws ClassNotFoundException {
        return withDriver(Class.forName(driverName).asSubclass(Driver.class));
    }

    @Tolerate
    public DefaultPersistenceProperties withCreateDatabaseSchemas(boolean newValue) {
        return withCreateDatabaseSchemas(TriBoolean.from(newValue));
    }

    public DefaultPersistenceProperties putExtra(String key, String value) {
        var m = new HashMap<>(getExtras());
        m.put(key, value);
        return withExtras(Map.copyOf(m));
    }

    public DefaultPersistenceProperties removeExtra(String key) {
        var m = new HashMap<>(getExtras());
        m.remove(key);
        return withExtras(Map.copyOf(m));
    }

    public DefaultPersistenceProperties clearExtras() {
        return withExtras(Map.of());
    }

    public Map<String, String> build() {
        Map<String, String> props = new HashMap<>(64);
        Class<? extends Driver> d = getDriver();
        if (d != Driver.class) props.put("javax.persistence.jdbc.driver", d.getName());
        ComposingPersistenceProperties.work("javax.persistence.jdbc.url", getUrl(), props::put);
        ComposingPersistenceProperties.work("javax.persistence.jdbc.user", getUser(), props::put);
        ComposingPersistenceProperties.work("javax.persistence.jdbc.password", getPassword(), props::put);
        ComposingPersistenceProperties.work("javax.persistence.database-product-name", getDatabaseProductName(), props::put);
        ComposingPersistenceProperties.work("javax.persistence.database-major-version", getDatabaseMajorVersion(), props::put);
        ComposingPersistenceProperties.work("javax.persistence.database-minor-version", getDatabaseMinorVersion(), props::put);
        getSchemaGenerationAction().work("javax.persistence.schema-generation.database.action", props::put);
        getSchemaGenerationCreate().work(
                "javax.persistence.schema-generation.create-source",
                "javax.persistence.schema-generation.create-script-source",
                props::put);
        getSchemaGenerationDrop().work(
                "javax.persistence.schema-generation.drop-source",
                "javax.persistence.schema-generation.drop-script-source",
                props::put);
        getSchemaGenerationScriptsCreate().work(
                "javax.persistence.schema-generation.scripts.action",
                "javax.persistence.schema-generation.scripts.create-target",
                "javax.persistence.schema-generation.scripts.drop-target",
                props::put);
        ComposingPersistenceProperties.work("javax.persistence.sql-load-script-source", getLoadScript(), props::put);
        ComposingPersistenceProperties.work("javax.persistence.schema-generation.connection", getSchemaGenerationConnection(), props::put);
        getCreateDatabaseSchemas().work("javax.persistence.schema-generation.create-database-schemas", props::put);
        props.putAll(getExtras());
        return props;
    }
}
